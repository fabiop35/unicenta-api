package com.unicenta.poc.application;

import com.unicenta.poc.domain.StockCurrentRepository;
import com.unicenta.poc.interfaces.dto.InventoryValueByLocationDto;
import com.unicenta.poc.interfaces.dto.InventoryValueDto;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Computes the total monetary value of the inventory at cost price.
 * <p>
 * Unlike the legacy {@link StockService#getInventoryValuation()} (which loads
 * every stock row and resolves each product one-by-one), this service pushes
 * the aggregation down to the database with a single {@code JOIN + SUM} query.
 * This avoids the N+1 query problem and scales to very large catalogs.
 * </p>
 * <p>
 * The queries are read-only {@code SELECT}s: no tables are created or modified
 * and no triggers/procedures are required.
 * </p>
 */
@Service
@Transactional(readOnly = true)
public class InventoryValuationService {

    private final StockCurrentRepository stockCurrentRepository;

    public InventoryValuationService(StockCurrentRepository stockCurrentRepository) {
        this.stockCurrentRepository = stockCurrentRepository;
    }

    /**
     * Total inventory value at cost (units * pricebuy), optionally restricted
     * to a single location, plus a per-location breakdown.
     *
     * @param locationId optional location filter (null / blank / "ALL" = all
     * locations)
     * @return the inventory value summary
     */
    public InventoryValueDto getInventoryValue(String locationId) {
        String normalized = normalize(locationId);

        List<InventoryValueByLocationDto> breakdowns = getInventoryValueByLocation(normalized);

        double totalValue = breakdowns.stream()
                .mapToDouble(InventoryValueByLocationDto::getTotalValue)
                .sum();
        long itemCount = breakdowns.stream()
                .mapToLong(InventoryValueByLocationDto::getItemCount)
                .sum();

        InventoryValueDto dto = new InventoryValueDto();
        dto.setTotalValue(totalValue);
        dto.setItemCount(itemCount);
        dto.setProductCount(stockCurrentRepository.countDistinctProducts(normalized));
        dto.setAsOf(LocalDateTime.now());
        dto.setBreakdowns(breakdowns);
        return dto;
    }

    /**
     * Per-location breakdown of inventory value, optionally filtered to one
     * location.
     *
     * @param locationId optional location filter
     * @return list of per-location totals
     */
    public List<InventoryValueByLocationDto> getInventoryValueByLocation(String locationId) {
        return stockCurrentRepository.inventoryValueByLocation(normalize(locationId));
    }

    private String normalize(String locationId) {
        if (locationId == null || locationId.isBlank() || "ALL".equalsIgnoreCase(locationId)) {
            return null;
        }
        return locationId;
    }
}
