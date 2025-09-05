package com.unicenta.poc.domain;

import com.unicenta.poc.interfaces.dto.StockCurrentDto;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StockCurrentRepository extends CrudRepository<StockCurrent, String> {

    @Query("SELECT * FROM stockcurrent WHERE location = :locationId")
    List<StockCurrent> findByLocationId(@Param("locationId") String locationId);

    @Query("SELECT * FROM stockcurrent WHERE product = :productId")
    List<StockCurrent> findByProductId(@Param("productId") String productId);

    @Query("""
           SELECT * FROM stockcurrent 
           WHERE location = :locationId 
           AND product = :productId 
           AND COALESCE(attributesetinstance_id, 'NULL') = COALESCE(:attributeSetInstanceId, 'NULL')
            """)
    Optional<StockCurrent> findByCompositeKey(
            @Param("locationId") String locationId,
            @Param("productId") String productId,
            @Param("attributeSetInstanceId") String attributeSetInstanceId);

    @Query("SELECT * FROM stockcurrent WHERE location = :locationId AND product = :productId")
    List<StockCurrent> findByLocationIdAndProductId(
            @Param("locationId") String locationId,
            @Param("productId") String productId);

    @Query(
            """
            SELECT * FROM stockcurrent 
            WHERE location = :locationId 
             AND product = :productId 
             AND COALESCE(attributesetinstance_id, 'NULL') = COALESCE(:attributeSetInstanceId, 'NULL')
             """)
    List<StockCurrent> findStockCurrentRecords(
            @Param("locationId") String locationId,
            @Param("productId") String productId,
            @Param("attributeSetInstanceId") String attributeSetInstanceId);

    @Modifying
    @Query("""
    DELETE FROM stockcurrent 
    WHERE location = :locationId 
      AND product = :productId 
      AND COALESCE(attributesetinstance_id, 'NULL') = COALESCE(:attributeSetInstanceId, 'NULL')
    """)
    void deleteByCompositeKey(
            @Param("locationId") String locationId,
            @Param("productId") String productId,
            @Param("attributeSetInstanceId") String attributeSetInstanceId);

    @Query("SELECT location, product, attributesetinstance_id, units FROM stockcurrent")
    List<StockCurrent> findAllProjected();

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
            @Param("search") String search
    );

    // 🔑 Use ON DUPLICATE KEY UPDATE for safe upsert
    /*@Modifying
    @Query("""
        INSERT INTO stockcurrent (location, product, attributesetinstance_id, units)
        VALUES (
           :#{#stock.locationId}, 
           :#{#stock.productId}, 
           :#{#stock.attributeSetInstanceId != null ? #stock.attributeSetInstanceId : 'NULL'},
           :#{#stock.units})
        ON DUPLICATE KEY UPDATE units = VALUES(units)
        """)
    void saveOrUpdate(@Param("stock") StockCurrent stock);*/
    @Modifying
    @Query("""
        INSERT INTO stockcurrent (location, product, attributesetinstance_id, units)
        VALUES (:#{#stock.locationId}, :#{#stock.productId}, :#{#stock.attributeSetInstanceId}, :#{#stock.units})
        ON DUPLICATE KEY UPDATE units = VALUES(units)
        """)
    void saveOrUpdate(@Param("stock") StockCurrent stock);
}
