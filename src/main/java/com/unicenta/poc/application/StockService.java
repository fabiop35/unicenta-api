package com.unicenta.poc.application;

import com.unicenta.poc.application.services.LookupService;
import com.unicenta.poc.domain.*;
import com.unicenta.poc.interfaces.dto.InventoryItemValuationDto;
import com.unicenta.poc.interfaces.dto.InventoryValuationDto;

import com.unicenta.poc.interfaces.dto.StockCurrentDto;
import com.unicenta.poc.interfaces.dto.StockEntryRequest;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

@Service
@Transactional(readOnly = true)
public class StockService {

    private final StockCurrentRepository stockCurrentRepository;
    private final StockDiaryRepository stockDiaryRepository;
    private final LocationRepository locationRepository;
    private final ProductRepository productRepository;
    private final AttributeSetInstanceRepository attributeSetInstanceRepository;
    private final LookupService lookupService;
    private final SupplierRepository supplierRepository;

    private static final Logger logger = LoggerFactory.getLogger(StockService.class);
    
    @Autowired
    private CacheManager cacheManager;
    
    public StockService(
            StockCurrentRepository stockCurrentRepository,
            StockDiaryRepository stockDiaryRepository,
            LocationRepository locationRepository,
            ProductRepository productRepository,
            AttributeSetInstanceRepository attributeSetInstanceRepository,
            LookupService lookupService,
            SupplierRepository supplierRepository) {

        this.stockCurrentRepository = stockCurrentRepository;
        this.stockDiaryRepository = stockDiaryRepository;
        this.locationRepository = locationRepository;
        this.productRepository = productRepository;
        this.attributeSetInstanceRepository = attributeSetInstanceRepository;
        this.lookupService = lookupService;
        this.supplierRepository = supplierRepository;
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
                    double pricebuy = lookupService.getProductPricebuy(stock.getProductId());
                    double pricesell = lookupService.getProductPricesell(stock.getProductId());
                    String productCode = lookupService.getProductCode(stock.getProductId());
                    return (productName != null && productName.toLowerCase().contains(searchTerm))
                            || (productRef != null && productRef.toLowerCase().contains(searchTerm))
                            || (productCode != null && productCode.toLowerCase().contains(searchTerm)
                            || pricebuy != 0.0
                            || pricesell != 0.0);
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
                .pricebuy(lookupService.getProductPricebuy(stock.getProductId()))
                .idSupplier(lookupService.getIdSupplier(stock.getProductId()))
                .pricesell(lookupService.getProductPricesell(stock.getProductId()))
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
     * Get Stock History for a specific item (location, product,
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

    //Get Current Stock Items by Product Code
    public List<StockCurrentDto> getCurrentStockByProductCode(String productCode, String locationId) {
        return stockCurrentRepository.findByProductCode(productCode);
    }

    /**
     * Creates a new stock diary entry based on the provided request and updates
     * the current stock levels accordingly.
     * <p>
     * This method performs the following actions within a single transaction:
     * <ul>
     * <li>Validates the existence of referenced entities: product, location,
     * and (if provided) supplier.</li>
     * <li>Creates and persists a {@link StockDiary} entry with the given data,
     * setting a default price of 0.0 if none is provided.</li>
     * <li>Atomically updates the current stock quantity in the
     * {@code stock_current} table by:
     * <ul>
     * <li>Looking up the existing stock level using the composite key (location
     * ID, product ID, and optional attribute set instance ID).</li>
     * <li>Deleting the existing row (handling {@code NULL} values
     * correctly).</li>
     * <li>Inserting a new row with the updated stock quantity.</li>
     * </ul>
     * </li>
     * <li>Updates the product's purchase price and supplier in the product
     * table.</li>
     * </ul>
     * </p>
     * <p>
     * The operation uses a safe upsert pattern without requiring an {@code @Id}
     * on {@link StockCurrent} or changes to the database schema, relying
     * instead on direct SQL operations for atomicity and correctness.
     * </p>
     *
     * @param request the {@link StockEntryRequest} containing all necessary
     * data for the stock entry, including product ID, location ID, quantity,
     * optional price, supplier, and attribute set instance ID.
     * @param userId the identifier of the user performing the operation;
     * recorded in the diary entry for audit purposes.
     * @return the persisted {@link StockDiary} entity representing the newly
     * created stock entry.
     * @throws IllegalArgumentException if any referenced entity (product,
     * location, or supplier) does not exist.
     * @throws DataAccessException if a database error occurs during the
     * transaction.
     */
    @Transactional
    public StockDiary createStockEntry(StockEntryRequest request, String userId) {
        String asi = (request.attributeSetInstanceId() != null && !request.attributeSetInstanceId().isBlank())
                ? request.attributeSetInstanceId() : null;

        // Validate FKs
        if (!productRepository.existsById(request.productId())) {
            throw new IllegalArgumentException("Producto no válido: " + request.productId());
        }
        if (!locationRepository.existsById(request.locationId())) {
            throw new IllegalArgumentException("Ubicación no válida: " + request.locationId());
        }
        if (request.supplier() != null && !supplierRepository.existsById(request.supplier())) {
            throw new IllegalArgumentException("Proveedor no válido: " + request.supplier());
        }

        // Create diary entry
        StockDiary diary = new StockDiary(
                request.date(),
                request.reason(),
                request.locationId(),
                request.productId(),
                asi,
                request.units(),
                request.price(),
                userId,
                request.supplier(),
                request.supplierDoc(),
                "Manual entry via UI"
        );
        diary.setPrice(diary.getPrice() != null ? diary.getPrice() : 0.0);
        StockDiary savedDiary = stockDiaryRepository.save(diary);

        // 🔥 SAFE UPSERT WITHOUT @Id or DB SCHEMA CHANGE
        Optional<StockCurrent> existingOpt = stockCurrentRepository.findByCompositeKey(
                request.locationId(), request.productId(), asi);

        double currentUnits = existingOpt.map(StockCurrent::getUnits).orElse(0.0);
        double newUnits = currentUnits + request.units();

        // Delete existing row (handles NULL correctly via COALESCE)
        stockCurrentRepository.deleteByCompositeKey(
                request.locationId(),
                request.productId(),
                asi
        );

        // Insert new row using raw SQL (no @Id needed)
        stockCurrentRepository.insertRaw(
                request.locationId(),
                request.productId(),
                asi, // can be null
                newUnits
        );

        //Update the product pricebuy, pricesell, supplier
        productRepository.updatePricebuyAndSupplier(request.productId(), request.price(), request.pricesell(), request.supplier());
        
        /*Cache pricebuyCache = cacheManager.getCache("pricebuy");
        if (pricebuyCache != null) {
            pricebuyCache.clear();
        }*/
        
        return savedDiary;
    }
}
