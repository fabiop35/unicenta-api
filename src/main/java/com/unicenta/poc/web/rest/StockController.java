package com.unicenta.poc.web.rest;

import com.unicenta.poc.application.StockService;
import com.unicenta.poc.domain.Location;
import com.unicenta.poc.domain.StockCurrent;
import com.unicenta.poc.domain.StockDiary;
import com.unicenta.poc.interfaces.dto.InventoryValuationDto;
import com.unicenta.poc.interfaces.dto.StockAdjustmentRequest;
import com.unicenta.poc.interfaces.dto.StockCurrentDto;
import com.unicenta.poc.interfaces.dto.StockHistoryDto;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stock")
@CrossOrigin(origins = "*")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/current")
    public Page<StockCurrentDto> getCurrentStock(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String locationId) {
        return stockService.getCurrentStock(page, size, search, locationId);
    }
    @GetMapping("/current/location/{locationId}")
    public List<StockCurrentDto> getCurrentStockByLocation(@PathVariable String locationId) {
        return stockService.getCurrentStockByLocation(locationId);
    }
    @PostMapping("/adjust")
    public ResponseEntity<StockDiary> adjustStock(@RequestBody @Valid StockAdjustmentRequest request) {
        StockDiary result = stockService.adjustStock(
                request.getLocationId(),
                request.getProductId(),
                request.getAttributeSetInstanceId(),
                request.getNewStock(),
                request.getUserId(),
                request.getNotes()
        );
        System.out.println(">>> StockController.adjustStock() <<<");
               
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/low-stock")
    public List<StockCurrentDto> getLowStockItems(@RequestParam(defaultValue = "5.0") Double threshold) {
        return stockService.getLowStockItems(threshold);
    }
    
    @GetMapping("/history/{productId}")
    public List<StockHistoryDto> getStockHistory(@PathVariable String productId) {
        return stockService.getStockHistory(productId);
    }
    @GetMapping("/valuation")
    public InventoryValuationDto getInventoryValuation() {
        return stockService.getInventoryValuation();
    }
    
    @GetMapping("/locations")
    public List<Location> getLocations() {
        return stockService.getLocations();
    }
    @GetMapping("/current/product/{productId}")
    public List<StockCurrentDto> getCurrentStockByProduct(@PathVariable String productId) {
        return stockService.getCurrentStockByProduct(productId);
    }
    /**
     * NEW History: Get Stock History for a specific item (location, product, attributeSetInstance)
     * @param locationId
     * @param productId
     * @param attributeSetInstanceId
     * @return 
     */
    @GetMapping("/history/item")
    public List<StockHistoryDto> getStockHistoryForItem(
            @RequestParam String locationId,
            @RequestParam String productId,
            @RequestParam(required = false) String attributeSetInstanceId) { // Optional parameter
        
        System.out.println(">>> StockController.getStockHistoryForItem.productId: "+productId+" <<<");
        System.out.println(">>> StockController.getStockHistoryForItem.locationId: "+locationId+" <<<");
        
        return stockService.getStockHistoryForItem(locationId, productId, attributeSetInstanceId);
    }
    
    @GetMapping("/current/byCode")
    public List<StockCurrentDto> getCurrentStockByProductCode(
            @RequestParam String code,
            @RequestParam(required = false) String locationId) { // Optional location filter
        
        System.out.println(">>> StockController.code: "+code+" <<<");
        System.out.println(">>> StockController.locationId: "+locationId+" <<<");
        
        List<StockCurrentDto> stockCurrentDtoList = stockService.getCurrentStockByProductCode(code, locationId);
        System.out.println(">>>StockController.getCurrentStockByProductCode().stockCurrentDtoList.size()"+stockCurrentDtoList.size());
        stockCurrentDtoList.forEach(e -> System.out.println(e.getProductId()));
        
        return stockCurrentDtoList;
    }

}