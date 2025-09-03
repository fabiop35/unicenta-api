package com.unicenta.poc.web.rest;

import com.unicenta.poc.application.StockService;
import com.unicenta.poc.domain.Location;
import com.unicenta.poc.domain.StockDiary;
import com.unicenta.poc.interfaces.dto.*;
import jakarta.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;

@RestController
@RequestMapping("/api/v1/stock")
@CrossOrigin(origins = "http://localhost:4200, https://localhost:4200, http://192.168.10.3:4200, https://192.168.10.3:4200")
public class StockController {
    private final StockService stockService;
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }
    @GetMapping("/current")
    public PageDto<StockCurrentDto> getCurrentStock(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String locationId) {
        Page<StockCurrentDto> result = stockService.getCurrentStock(page, size, search, locationId);
        return new PageDto<>(
                result.getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.isFirst(),
                result.isLast(),
                result.isEmpty()
        );
    }
    @GetMapping("/current/location/{locationId}")
    public List<StockCurrentDto> getCurrentStockByLocation(@PathVariable String locationId) {
        return stockService.getCurrentStockByLocation(locationId);
    }
    @GetMapping("/locations")
    public List<Location> getLocations() {
        return stockService.getLocations();
    }
    @GetMapping("/current/product/{productId}")
    public List<StockCurrentDto> getCurrentStockByProduct(@PathVariable String productId) {
        return stockService.getCurrentStockByProduct(productId);
    }
    @GetMapping("/history/product/{productId}")
    public List<StockHistoryDto> getStockHistory(
            @PathVariable String productId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return stockService.getStockHistory(productId, startDate, endDate);
    }
    @PostMapping("/movement")
    public ResponseEntity<StockDiary> recordStockMovement(@RequestBody StockMovementDto dto) {
        StockDiary stockDiary = stockService.recordStockMovement(dto);
        return ResponseEntity.ok().body(stockDiary);
    }
   @PostMapping("/adjust")
    public ResponseEntity<StockDiary> adjustStock(@RequestBody @Valid StockAdjustmentRequest request) {
        System.out.println(">>> StockController.adjustStock() <<< ");
        StockDiary stockDiary = stockService.adjustStock(
                request.getLocationId(),
                request.getProductId(),
                request.getAttributeSetInstanceId(),
                request.getNewStock(),
                request.getUserId(),
                request.getNotes()
        );
        return ResponseEntity.ok().body(stockDiary);
    }

    @GetMapping("/low-stock")
    public List<StockCurrentDto> getLowStockItems(@RequestParam(defaultValue = "5.0") Double threshold) {
        return stockService.getLowStockItems(threshold);
    }
    @GetMapping("/valuation")
    public InventoryValuationDto getInventoryValuation() {
        return stockService.getInventoryValuation();
    }
    @GetMapping("/movements/product")
    public List<StockHistoryDto> getStockMovementsForProduct(
            @RequestParam String locationId,
            @RequestParam String productId,
            @RequestParam(required = false) String attributeSetInstanceId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        System.out.println(">>> StockController.getStockMovementsForProduct()");
        return stockService.getStockHistoryForProduct(locationId, productId, attributeSetInstanceId, startDate, endDate);
    }
    @GetMapping("/movements/{id}")
    public StockHistoryDto getStockMovementById(@PathVariable String id) {
        System.out.println(">>> StockController.getStockMovementById()");
        return stockService.getStockMovementById(id);
    }
}