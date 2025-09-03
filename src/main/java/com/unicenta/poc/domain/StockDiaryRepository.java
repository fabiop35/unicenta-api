package com.unicenta.poc.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockDiaryRepository extends CrudRepository<StockDiary, String> {

    List<StockDiary> findBySupplier(String supplierId);

    List<StockDiary> findByProductId(String productId);

    List<StockDiary> findByLocationId(String locationId);

    List<StockDiary> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<StockDiary> findByProductIdAndDateBetween(String productId, LocalDateTime startDate, LocalDateTime endDate);

    List<StockDiary> findByLocationIdAndProductIdAndAttributeSetInstanceId(String locationId, String productId, String attributeSetInstanceId);

    List<StockDiary> findByLocationIdAndProductIdAndDateBetween(String locationId, String productId,
            LocalDateTime startDate, LocalDateTime endDate);

    List<StockDiary> findByLocationIdAndProductIdAndAttributeSetInstanceIdAndDateBetween(String locationId, String productId,
            String attributeSetInstanceId, LocalDateTime startDate, LocalDateTime endDate);

    List<StockDiary> findByLocationIdAndProductId(String locationId, String productId);
}
