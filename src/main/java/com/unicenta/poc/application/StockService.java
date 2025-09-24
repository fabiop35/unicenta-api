package com.unicenta.poc.application;

import com.unicenta.poc.application.services.LookupService;
import com.unicenta.poc.domain.*;
import com.unicenta.poc.interfaces.dto.InventoryItemValuationDto;
import com.unicenta.poc.interfaces.dto.InventoryValuationDto;

import com.unicenta.poc.interfaces.dto.StockCurrentDto;
import com.unicenta.poc.interfaces.dto.StockHistoryDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class StockService {

    private final StockCurrentRepository stockCurrentRepository;
    private final StockDiaryRepository stockDiaryRepository;
    private final LocationRepository locationRepository;
    private final ProductRepository productRepository;
    private final AttributeSetInstanceRepository attributeSetInstanceRepository;
    private final LookupService lookupService;

    private static final Logger logger = LoggerFactory.getLogger(StockService.class);

    public StockService(
            StockCurrentRepository stockCurrentRepository,
            StockDiaryRepository stockDiaryRepository,
            LocationRepository locationRepository,
            ProductRepository productRepository,
            AttributeSetInstanceRepository attributeSetInstanceRepository,
            LookupService lookupService) {
        this.stockCurrentRepository = stockCurrentRepository;
        this.stockDiaryRepository = stockDiaryRepository;
        this.locationRepository = locationRepository;
        this.productRepository = productRepository;
        this.attributeSetInstanceRepository = attributeSetInstanceRepository;
        this.lookupService = lookupService;
    }

    public Page<StockCurrentDto> getCurrentStock(int page, int size, String search, String locationId) {
        Pageable pageable = PageRequest.of(page, size);
        List<StockCurrent> allStock = stockCurrentRepository.findAllProjected();

        List<StockCurrent> filteredStock = allStock.stream()
                .filter(stock -> locationId == null || locationId.equals(stock.getLocationId()))
                .filter(stock -> {
                    if (search == null || search.isEmpty()) {
                        return true;
                    }
                    String searchTerm = search.toLowerCase();
                    String productName = lookupService.getProductName(stock.getProductId());
                    String productRef = lookupService.getProductReference(stock.getProductId());
                    String productCode = lookupService.getProductCode(stock.getProductId());
                    return (productName != null && productName.toLowerCase().contains(searchTerm))
                            || (productRef != null && productRef.toLowerCase().contains(searchTerm))
                            || (productCode != null && productCode.toLowerCase().contains(searchTerm));
                })
                .collect(Collectors.toList());

        List<StockCurrentDto> dtos = filteredStock.stream()
                .map(this::convertToStockCurrentDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), dtos.size());
        List<StockCurrentDto> paged = start > end ? List.of() : dtos.subList(start, end);

        return new PageImpl<>(paged, pageable, dtos.size());
    }

    @Transactional
    public StockDiary adjustStock(
            String locationId,
            String productId,
            String attributeSetInstanceId,
            Double newStock,
            String userId,
            String notes) {

        // Normalize
        if (attributeSetInstanceId != null && attributeSetInstanceId.trim().isEmpty()) {
            attributeSetInstanceId = null;
        }

        // Validate attributeSetInstanceId exists (FK constraint)
        if (attributeSetInstanceId != null
                && !attributeSetInstanceRepository.findById(attributeSetInstanceId).isPresent()) {
            logger.warn("Invalid attributeSetInstanceId: {}. Setting to NULL.", attributeSetInstanceId);
            attributeSetInstanceId = null;
        }

        // Find current record
        Optional<StockCurrent> currentOpt = stockCurrentRepository.findByCompositeKey(
                locationId, productId, attributeSetInstanceId);

        Double currentUnits = currentOpt.map(StockCurrent::getUnits).orElse(0.0);
        Double adjustment = newStock - currentUnits;

        if (Math.abs(adjustment) < 1e-6) {
            logger.info("No change in stock for product {} at location {}", productId, locationId);
            return null;
        }

        // Record movement
        StockDiary diary = new StockDiary(
                LocalDateTime.now(),
                2, // Adjustment
                locationId,
                productId,
                attributeSetInstanceId,
                adjustment,
                null, // Price not tracked for adjustment
                userId,
                null, null,
                notes != null ? notes : "Stock adjustment"
        );

        // Default price to 0.0 if DB requires NOT NULL
        diary.setPrice(diary.getPrice() != null ? diary.getPrice() : 0.0);

        try {
            diary.setProductName(lookupService.getProductName(productId));
        } catch (Exception e) {
            diary.setProductName("Unknown");
        }

        StockDiary savedDiary = stockDiaryRepository.save(diary);

        // Upsert stockcurrent
        StockCurrent stock = new StockCurrent(locationId, productId, attributeSetInstanceId, newStock);
        stockCurrentRepository.saveOrUpdate(stock);

        return savedDiary;
    }

    public List<StockHistoryDto> getStockHistory(String productId) {
        return stockDiaryRepository.findByProductId(productId).stream()
                .map(this::convertToStockHistoryDto)
                .collect(Collectors.toList());
    }

    private StockCurrentDto convertToStockCurrentDto(StockCurrent stock) {
        if (stock == null || stock.getProductId() == null) {
            return null;
        }

        return StockCurrentDto.builder()
                .locationId(stock.getLocationId())
                .productId(stock.getProductId())
                .attributeSetInstanceId(stock.getAttributeSetInstanceId())
                .units(stock.getUnits())
                .productName(lookupService.getProductName(stock.getProductId()))
                .locationName(lookupService.getLocationName(stock.getLocationId()))
                .productReference(lookupService.getProductReference(stock.getProductId()))
                .productCode(lookupService.getProductCode(stock.getProductId()))
                .attributeSetInstanceDescription(lookupService.getAttributeSetInstanceDescription(stock.getAttributeSetInstanceId()))
                .build();
    }
    
    // Get current stock by location
    public List<StockCurrentDto> getCurrentStockByLocation(String locationId) {
        return stockCurrentRepository.findByLocationId(locationId).stream()
                .map(this::convertToStockCurrentDto)
                .collect(Collectors.toList());
    }
    
    public InventoryValuationDto getInventoryValuation() {
        List<StockCurrent> stockCurrentList = stockCurrentRepository.findAllProjected();
        List<InventoryItemValuationDto> itemValues = new ArrayList<>();
        double totalValue = 0;

        for (StockCurrent stock : stockCurrentList) {
            Optional<Product> productOptional = productRepository.findById(stock.getProductId());
            if (!productOptional.isPresent()) {
                continue;
            }

            Product product = productOptional.get();
            double itemValue = stock.getUnits() * product.getPricebuy();
            totalValue += itemValue;

            InventoryItemValuationDto itemValuation = new InventoryItemValuationDto();
            itemValuation.setProductId(product.getId());
            itemValuation.setProductName(product.getName());
            itemValuation.setAttributeSetInstanceId(stock.getAttributeSetInstanceId());
            itemValuation.setUnits(stock.getUnits());
            itemValuation.setCostPrice(product.getPricebuy());
            itemValuation.setItemValue(itemValue);
            itemValues.add(itemValuation);
        }

        InventoryValuationDto valuation = new InventoryValuationDto();
        valuation.setTotalValue(totalValue);
        valuation.setItems(itemValues);
        return valuation;
    }

    private StockHistoryDto convertToStockHistoryDto(StockDiary diary) {
        return StockHistoryDto.builder()
                .id(diary.getId())
                .date(diary.getDate())
                .reason(diary.getReason())
                .locationId(diary.getLocationId())
                .productId(diary.getProductId())
                .attributeSetInstanceId(diary.getAttributeSetInstanceId())
                .units(diary.getUnits())
                .price(diary.getPrice())
                .userId(diary.getUserId())
                .supplierId(diary.getSupplier())
                .supplierDoc(diary.getSupplierDoc())
                .notes(diary.getNotes())
                .productName(lookupService.getProductName(diary.getProductId()))
                .locationName(lookupService.getLocationName(diary.getLocationId()))
                .userName(lookupService.getUserName(diary.getUserId()))
                .supplierName(diary.getSupplier() != null ? lookupService.getSupplierName(diary.getSupplier()) : "N/A")
                .attributeSetInstanceDescription(lookupService.getAttributeSetInstanceDescription(diary.getAttributeSetInstanceId()))
                .build();
    }

    public List<Location> getLocations() {
        return locationRepository.findAll();
    }
    // Get current stock by product
    public List<StockCurrentDto> getCurrentStockByProduct(String productId) {
        return stockCurrentRepository.findByProductId(productId).stream()
                .map(this::convertToStockCurrentDto)
                .collect(Collectors.toList());
    }
    /**
     * NEW History: Get Stock History for a specific item (location, product,
     * attributeSetInstance)
     *
     * @param locationId
     * @param productId
     * @param attributeSetInstanceId
     * @return
     */
    public List<StockHistoryDto> getStockHistoryForItem(String locationId, String productId, String attributeSetInstanceId) {
        // Normalize ASI ID
        if (attributeSetInstanceId != null && attributeSetInstanceId.trim().isEmpty()) {
            attributeSetInstanceId = null;
        }
        // Use the new repository method
        List<StockDiary> diaryEntries = stockDiaryRepository.findByLocationIdAndProductIdAndAttributeSetInstanceId(
                locationId, productId, attributeSetInstanceId);

        // Convert to DTOs
        return diaryEntries.stream()
                .map(this::convertToStockHistoryDto) // Use the existing conversion method
                .collect(Collectors.toList());
    }

    // Use findAllProjected() instead of findAll()
    public List<StockCurrentDto> getLowStockItems(Double threshold) {
        return stockCurrentRepository.findAllProjected().stream()
                .filter(stock -> stock.getUnits() <= threshold)
                .map(this::convertToStockCurrentDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
