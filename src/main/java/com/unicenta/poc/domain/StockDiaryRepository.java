package com.unicenta.poc.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockDiaryRepository extends CrudRepository<StockDiary, String> {

    /**
     * Retrieves a list of StockDiary records for a specific supplier.
     *
     * @param supplierId The ID of the supplier.
     * @return A list of StockDiary records.
     */
    List<StockDiary> findBySupplier(String supplierId);
}
