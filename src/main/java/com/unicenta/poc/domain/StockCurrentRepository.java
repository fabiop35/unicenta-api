package com.unicenta.poc.domain;

import com.unicenta.poc.interfaces.dto.StockCurrentDto;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing current stock levels.
 * 
 * This table is a materialized view of stockdiary movements.
 * DO NOT write directly using save() unless using safe upsert.
 * 
 * Based on uniCenta oPOS model:
 * - Composite key: (location, product, attributesetinstance_id)
 * - Foreign keys to locations, products, attributesetinstance
 * - Updates triggered by stockdiary inserts
 */
@Repository
public interface StockCurrentRepository extends CrudRepository<StockCurrent, String> {

    /**
     * Find all stock records for a location.
     */
    @Query("SELECT location, product, attributesetinstance_id, units " +
           "FROM stockcurrent WHERE location = :locationId")
    List<StockCurrent> findByLocationId(@Param("locationId") String locationId);

    /**
     * Find all stock records for a product.
     */
    @Query("SELECT location, product, attributesetinstance_id, units " +
           "FROM stockcurrent WHERE product = :productId")
    List<StockCurrent> findByProductId(@Param("productId") String productId);

    /**
     * Find by location and product (used for filtering).
     */
    @Query("SELECT location, product, attributesetinstance_id, units " +
           "FROM stockcurrent WHERE location = :locationId AND product = :productId")
    List<StockCurrent> findByLocationIdAndProductId(
            @Param("locationId") String locationId,
            @Param("productId") String productId);

    /**
     * Find by composite key: location + product + attributesetinstance_id
     * Uses COALESCE to handle NULL safely in unique constraint.
     */
    @Query("SELECT location, product, attributesetinstance_id, units " +
           "FROM stockcurrent " +
           "WHERE location = :locationId " +
           "  AND product = :productId " +
           "  AND COALESCE(attributesetinstance_id, 'NULL') = COALESCE(:attributeSetInstanceId, 'NULL')")
    Optional<StockCurrent> findByCompositeKey(
            @Param("locationId") String locationId,
            @Param("productId") String productId,
            @Param("attributeSetInstanceId") String attributeSetInstanceId);

    /**
     * Find all records matching location, product, and optional asi_id.
     * Used for duplicate detection and fallback lookup.
     */
    @Query("SELECT location, product, attributesetinstance_id, units " +
           "FROM stockcurrent " +
           "WHERE location = :locationId " +
           "  AND product = :productId " +
           "  AND COALESCE(attributesetinstance_id, 'NULL') = COALESCE(:attributeSetInstanceId, 'NULL')")
    List<StockCurrent> findStockCurrentRecords(
            @Param("locationId") String locationId,
            @Param("productId") String productId,
            @Param("attributeSetInstanceId") String attributeSetInstanceId);

    /**
     * Delete by composite key (safe with NULL handling).
     */
    @Modifying
    @Query("DELETE FROM stockcurrent " +
           "WHERE location = :locationId " +
           "  AND product = :productId " +
           "  AND COALESCE(attributesetinstance_id, 'NULL') = COALESCE(:attributeSetInstanceId, 'NULL')")
    void deleteByCompositeKey(
            @Param("locationId") String locationId,
            @Param("productId") String productId,
            @Param("attributeSetInstanceId") String attributeSetInstanceId);

    /**
     * Find all projected (safe subset of columns).
     */
    @Query("SELECT location, product, attributesetinstance_id, units FROM stockcurrent")
    List<StockCurrent> findAllProjected();

    /**
     * Custom query for search by product name or reference.
     */
    @Query("""
        SELECT sc.location as locationId,
               sc.product as productId,
               sc.attributesetinstance_id as attributeSetInstanceId,
               sc.units
        FROM stockcurrent sc
        WHERE (:search IS NULL OR sc.product IN (
            SELECT p.id FROM products p WHERE LOWER(p.name) LIKE %:search%
        ))
        AND (:locationId IS NULL OR sc.location = :locationId)
        """)
    List<StockCurrentDto> findStockCurrentDtos(
            @Param("locationId") String locationId,
            @Param("search") String search);

    /**
     * UPSERT: Insert or update on duplicate key.
     * Uses COALESCE to ensure NULL values are handled consistently.
     * Prevents foreign key violations by not inserting invalid IDs.
     */
    @Modifying
    @Query("""
        INSERT INTO stockcurrent (location, product, attributesetinstance_id, units)
        VALUES (
            :#{#stock.locationId},
            :#{#stock.productId},
            :#{#stock.attributeSetInstanceId != null ? #stock.attributeSetInstanceId : null},
            :#{#stock.units}
        )
        ON DUPLICATE KEY UPDATE units = VALUES(units)
        """)
    void saveOrUpdate(@Param("stock") StockCurrent stock);
}