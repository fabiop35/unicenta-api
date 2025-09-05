package com.unicenta.poc.application;

import com.unicenta.poc.application.services.LookupService;
import com.unicenta.poc.domain.AttributeSetInstance;
import com.unicenta.poc.domain.AttributeSetInstanceRepository;
import com.unicenta.poc.domain.Location;
import com.unicenta.poc.domain.LocationRepository;
import com.unicenta.poc.domain.PeopleRepository;
import com.unicenta.poc.domain.Product;
import com.unicenta.poc.domain.ProductRepository;
import com.unicenta.poc.domain.exceptions.ResourceNotFoundException;
import com.unicenta.poc.domain.StockCurrent;
import com.unicenta.poc.domain.StockCurrentRepository;
import com.unicenta.poc.domain.StockDiary;
import com.unicenta.poc.domain.StockDiaryRepository;
import com.unicenta.poc.interfaces.dto.InventoryItemValuationDto;
import com.unicenta.poc.interfaces.dto.InventoryValuationDto;
import com.unicenta.poc.interfaces.dto.StockCurrentDto;
import com.unicenta.poc.interfaces.dto.StockHistoryDto;
import com.unicenta.poc.interfaces.dto.StockMovementDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// StockService.java
@Service
public class StockService {

    private final StockCurrentRepository stockCurrentRepository;
    private final StockDiaryRepository stockDiaryRepository;
    private final LocationRepository locationRepository;
    private final ProductRepository productRepository;
    private final LookupService lookupService;
    private final PeopleRepository peopleRepository;
    private final AttributeSetInstanceRepository attributeSetInstanceRepository;

    private static final Logger logger = LoggerFactory.getLogger(StockService.class);

    public StockService(
            StockCurrentRepository stockCurrentRepository,
            StockDiaryRepository stockDiaryRepository,
            LocationRepository locationRepository,
            ProductRepository productRepository,
            LookupService lookupService,
            PeopleRepository peopleRepository,
            AttributeSetInstanceRepository attributeSetInstanceRepository) {
        this.stockCurrentRepository = stockCurrentRepository;
        this.stockDiaryRepository = stockDiaryRepository;
        this.locationRepository = locationRepository;
        this.productRepository = productRepository;
        this.lookupService = lookupService;
        this.peopleRepository = peopleRepository;
        this.attributeSetInstanceRepository = attributeSetInstanceRepository;
    }

    public Page<StockCurrentDto> getCurrentStock(int page, int size, String search, String locationId) {
        System.out.println(">>> StockService.getCurrentStock().page: " + page);
        System.out.println(">>> StockService.getCurrentStock().size: " + size);
        System.out.println(">>> StockService.getCurrentStock().search: " + search);
        System.out.println(">>> StockService.getCurrentStock().locationId: " + locationId);

        Pageable pageable = PageRequest.of(page, size);

        List<StockCurrent> allStock = stockCurrentRepository.findAllProjected();

        List<StockCurrent> filteredStock = allStock.stream()
                .filter(stock -> locationId == null || locationId.isEmpty() || locationId.equals(stock.getLocationId()))
                .filter(stock -> {
                    if (search == null || search.isEmpty()) {
                        return true;
                    }
                    String searchTerm = search.toLowerCase();
                    String productName = lookupService.getProductName(stock.getProductId());
                    String productReference = lookupService.getProductReference(stock.getProductId());
                    String productCode = lookupService.getProductCode(stock.getProductId());
                    return (productName != null && productName.toLowerCase().contains(searchTerm))
                            || (productReference != null && productReference.toLowerCase().contains(searchTerm))
                            || (productCode != null && productCode.toLowerCase().contains(searchTerm));
                })
                .collect(Collectors.toList());

        List<StockCurrentDto> stockDtos = filteredStock.stream()
                .map(this::convertToStockCurrentDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), stockDtos.size());
        List<StockCurrentDto> pagedStock = start > end ? Collections.emptyList() : stockDtos.subList(start, end);

        return new PageImpl<>(pagedStock, pageable, stockDtos.size());
    }

    @Transactional
    public StockDiary adjustStock(
            String locationId,
            String productId,
            String attributeSetInstanceId,
            Double newStock,
            String userId,
            String notes) {
        
        System.out.println(">>> StockService.adjustStock() <<<");
        System.out.println(">>> StockService.adjustStock().locationId: " + locationId + " <<<");
        System.out.println(">>> StockService.adjustStock().productId: " + productId + " <<<");
        System.out.println(">>> StockService.adjustStock().attributeSetInstanceId: " + attributeSetInstanceId + " <<<");
        System.out.println(">>> StockService.adjustStock().newStock: " + newStock + " <<<");

        if (attributeSetInstanceId != null && attributeSetInstanceId.trim().isEmpty()) {
            attributeSetInstanceId = "null";
        }

        // ✅ Validate foreign key: if attributeSetInstanceId is not null, check if it exists
        if (attributeSetInstanceId != null) {
            System.out.println("attributeSetInstanceId != null");
            Optional<AttributeSetInstance> asiOpt = attributeSetInstanceRepository.findById(attributeSetInstanceId);
            if (asiOpt.isEmpty()) {
                // Create a placeholder
                AttributeSetInstance placeholder = new AttributeSetInstance();
                placeholder.setId(attributeSetInstanceId);
                placeholder.setDescription("Auto-created placeholder");
                placeholder.setAttributeSetId("UNKNOWN"); // or some default
                attributeSetInstanceRepository.save(placeholder);
                System.out.println("Created placeholder AttributeSetInstance: " + attributeSetInstanceId);
            }
        }

        List<StockCurrent> currentList = stockCurrentRepository.findStockCurrentRecords(locationId, productId, attributeSetInstanceId);
        if (currentList.isEmpty() && attributeSetInstanceId != null) {
            currentList = stockCurrentRepository.findStockCurrentRecords(locationId, productId, null);
        }

        StockCurrent current;
        Double adjustment;

        if (currentList.isEmpty()) {
            current = new StockCurrent(locationId, productId, attributeSetInstanceId, newStock);
            adjustment = newStock;
        } else if (currentList.size() > 1) {
            logger.error("Multiple stock records found for location {} and product {}: {} records", locationId, productId, currentList.size());
            current = consolidateDuplicateRecords(locationId, productId, attributeSetInstanceId, currentList);
            adjustment = newStock - current.getUnits();
            current.setUnits(newStock);
        } else {
            current = currentList.get(0);
            adjustment = newStock - current.getUnits();
            current.setUnits(newStock);
        }

        // ✅ Always use saveOrUpdate — never save()
        stockCurrentRepository.saveOrUpdate(current);

        return recordStockMovement(createAdjustmentDto(locationId, productId, attributeSetInstanceId, adjustment, userId, notes));
    }

    private StockCurrent consolidateDuplicateRecords(
            String locationId, String productId, String attributeSetInstanceId, List<StockCurrent> duplicates) {
        if (duplicates.isEmpty()) {
            throw new IllegalStateException("Cannot consolidate empty list");
        }

        double totalUnits = duplicates.stream().mapToDouble(StockCurrent::getUnits).sum();

        for (StockCurrent duplicate : duplicates) {
            try {
                stockCurrentRepository.deleteByCompositeKey(
                        duplicate.getLocationId(),
                        duplicate.getProductId(),
                        duplicate.getAttributeSetInstanceId()
                );
            } catch (Exception e) {
                logger.error("Error deleting duplicate", e);
            }
        }

        StockCurrent consolidated = new StockCurrent(locationId, productId, attributeSetInstanceId, totalUnits);
        stockCurrentRepository.saveOrUpdate(consolidated); // ✅ Use upsert
        return consolidated;
    }

    @Transactional
    public StockDiary recordStockMovement(StockMovementDto dto) {
        StockDiary stockDiary = new StockDiary(
                dto.getDate(),
                dto.getReason(),
                dto.getLocationId(),
                dto.getProductId(),
                dto.getAttributeSetInstanceId(),
                dto.getUnits(),
                dto.getPrice() != null ? dto.getPrice() : 0.0,
                dto.getUserId(),
                dto.getSupplierId(),
                dto.getSupplierDoc(),
                dto.getNotes()
        );

        try {
            stockDiary.setProductName(lookupService.getProductName(dto.getProductId()));
        } catch (Exception e) {
            logger.warn("Could not load product name for {}", dto.getProductId(), e);
            stockDiary.setProductName("Unknown");
        }

        return stockDiaryRepository.save(stockDiary);
    }

    private StockMovementDto createAdjustmentDto(String locationId, String productId, String attributeSetInstanceId,
            Double adjustment, String userId, String notes) {
        StockMovementDto dto = new StockMovementDto();
        dto.setDate(LocalDateTime.now());
        dto.setReason(2);
        dto.setLocationId(locationId);
        dto.setProductId(productId);
        dto.setAttributeSetInstanceId(attributeSetInstanceId);
        dto.setUnits(adjustment);
        dto.setUserId(userId);
        dto.setNotes(notes != null ? notes : "Stock adjustment");
        return dto;
    }

    // Use findAllProjected() instead of findAll()
    public List<StockCurrentDto> getLowStockItems(Double threshold) {
        return stockCurrentRepository.findAllProjected().stream()
                .filter(stock -> stock.getUnits() <= threshold)
                .map(this::convertToStockCurrentDto)
                .filter(Objects::nonNull)
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

    private StockCurrentDto convertToStockCurrentDto(StockCurrent stockCurrent) {
        if (stockCurrent == null) {
            return null;
        }

        if (stockCurrent.getProductId() == null) {
            System.out.println("Null productId for stock record: location=" + stockCurrent.getLocationId());
            return null;
        }

        String productName = lookupService.getProductName(stockCurrent.getProductId());
        String locationName = lookupService.getLocationName(stockCurrent.getLocationId());
        String attributeSetInstanceDescription = lookupService.getAttributeSetInstanceDescription(
                stockCurrent.getAttributeSetInstanceId());
        String productReference = lookupService.getProductReference(stockCurrent.getProductId());
        String productCode = lookupService.getProductCode(stockCurrent.getProductId());

        StockCurrentDto dto = new StockCurrentDto();
        dto.setLocationId(stockCurrent.getLocationId());
        dto.setProductId(stockCurrent.getProductId());
        dto.setAttributeSetInstanceId(stockCurrent.getAttributeSetInstanceId());
        dto.setUnits(stockCurrent.getUnits());
        dto.setProductName(productName);
        dto.setLocationName(locationName);
        dto.setProductReference(productReference);
        dto.setAttributeSetInstanceDescription(attributeSetInstanceDescription);
        dto.setProductCode(productCode);
        return dto;
    }

    // Add other methods: getCurrentStockByLocation, getStockHistory, etc. as needed
    // Get current stock by location
    public List<StockCurrentDto> getCurrentStockByLocation(String locationId) {
        return stockCurrentRepository.findByLocationId(locationId).stream()
                .map(this::convertToStockCurrentDto)
                .collect(Collectors.toList());
    }

    public List<StockHistoryDto> getStockHistoryForProduct(
            String locationId,
            String productId,
            String attributeSetInstanceId,
            LocalDateTime startDate,
            LocalDateTime endDate) {

        List<StockDiary> stockDiaryList;

        // Determine which repository method to call based on parameters
        if (startDate == null || endDate == null) {
            if (attributeSetInstanceId == null || attributeSetInstanceId.isEmpty()) {
                stockDiaryList = stockDiaryRepository.findByLocationIdAndProductId(locationId, productId);
            } else {
                stockDiaryList = stockDiaryRepository.findByLocationIdAndProductIdAndAttributeSetInstanceId(
                        locationId, productId, attributeSetInstanceId);
            }
        } else {
            if (attributeSetInstanceId == null || attributeSetInstanceId.isEmpty()) {
                stockDiaryList = stockDiaryRepository.findByLocationIdAndProductIdAndDateBetween(
                        locationId, productId, startDate, endDate);
            } else {
                stockDiaryList = stockDiaryRepository.findByLocationIdAndProductIdAndAttributeSetInstanceIdAndDateBetween(
                        locationId, productId, attributeSetInstanceId, startDate, endDate);
            }
        }

        return stockDiaryList.stream()
                .map(this::convertToStockHistoryDto)
                .collect(Collectors.toList());
    }

    private StockHistoryDto convertToStockHistoryDto(StockDiary stockDiary) {

        String productName = lookupService.getProductName(stockDiary.getProductId());
        String locationName = lookupService.getLocationName(stockDiary.getLocationId());
        String userName = lookupService.getUserName(stockDiary.getUserId());
        String supplierName = "";
        if (stockDiary.getSupplier() != null) {
            supplierName = lookupService.getSupplierName(stockDiary.getSupplier());
        }

        String attributeSetInstanceDescription = lookupService.getAttributeSetInstanceDescription(
                stockDiary.getAttributeSetInstanceId());

        StockHistoryDto dto = new StockHistoryDto();
        dto.setId(stockDiary.getId());
        dto.setDate(stockDiary.getDate());
        dto.setReason(stockDiary.getReason());
        dto.setLocationId(stockDiary.getLocationId());
        dto.setProductId(stockDiary.getProductId());
        dto.setAttributeSetInstanceId(stockDiary.getAttributeSetInstanceId());
        dto.setUnits(stockDiary.getUnits());
        dto.setPrice(stockDiary.getPrice());
        dto.setUserId(stockDiary.getUserId());
        dto.setSupplierId(stockDiary.getSupplier());
        dto.setSupplierDoc(stockDiary.getSupplierDoc());
        dto.setNotes(stockDiary.getNotes());
        dto.setProductName(productName);
        dto.setLocationName(locationName);
        dto.setUserName(userName);
        dto.setSupplierName(supplierName);
        dto.setAttributeSetInstanceDescription(attributeSetInstanceDescription);
        return dto;
    }

    public StockHistoryDto getStockMovementById(String id) {
        return stockDiaryRepository.findById(id)
                .map(this::convertToStockHistoryDto)
                .orElseThrow(() -> new ResourceNotFoundException("Movement not found with id: " + id));
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

    // Get stock history
    public List<StockHistoryDto> getStockHistory(String productId, LocalDateTime startDate, LocalDateTime endDate) {
        List<StockDiary> stockDiaryList;

        if (startDate == null || endDate == null) {
            stockDiaryList = stockDiaryRepository.findByProductId(productId);
        } else {
            stockDiaryList = stockDiaryRepository.findByProductIdAndDateBetween(productId, startDate, endDate);
        }

        return stockDiaryList.stream()
                .map(this::convertToStockHistoryDto)
                .collect(Collectors.toList());
    }
}
