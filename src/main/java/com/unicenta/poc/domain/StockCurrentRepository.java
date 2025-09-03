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
public interface StockCurrentRepository extends CrudRepository<StockCurrent, StockCurrentId> {

    @Query("SELECT * FROM stockcurrent WHERE location = :locationId")
    List<StockCurrent> findByLocationId(@Param("locationId") String locationId);

    @Query("SELECT * FROM stockcurrent WHERE product = :productId")
    List<StockCurrent> findByProductId(@Param("productId") String productId);

    @Query("SELECT * FROM stockcurrent WHERE location = :locationId AND product = :productId")
    List<StockCurrent> findByLocationIdAndProductId(
            @Param("locationId") String locationId,
            @Param("productId") String productId);

    @Query("SELECT * FROM stockcurrent WHERE location = :locationId AND product = :productId " +
           "AND (:attributeSetInstanceId IS NULL AND attributesetinstance_id IS NULL OR attributesetinstance_id = :attributeSetInstanceId) " +
           "LIMIT 1")
    Optional<StockCurrent> findByLocationIdAndProductIdAndAttributeSetInstanceId(
            @Param("locationId") String locationId,
            @Param("productId") String productId,
            @Param("attributeSetInstanceId") String attributeSetInstanceId);

    @Query("SELECT * FROM stockcurrent WHERE location = :locationId AND product = :productId "
            + "AND (:attributeSetInstanceId IS NULL AND attributesetinstance_id IS NULL OR attributesetinstance_id = :attributeSetInstanceId)")
    List<StockCurrent> findStockCurrentRecords(
            @Param("locationId") String locationId,
            @Param("productId") String productId,
            @Param("attributeSetInstanceId") String attributeSetInstanceId);

    @Modifying
    @Query("DELETE FROM stockcurrent WHERE location = :locationId AND product = :productId "
            + "AND (:attributeSetInstanceId IS NULL AND attributesetinstance_id IS NULL OR attributesetinstance_id = :attributeSetInstanceId)")
    void deleteByCompositeKey(
            @Param("locationId") String locationId,
            @Param("productId") String productId,
            @Param("attributeSetInstanceId") String attributeSetInstanceId);

    @Query("SELECT location, product, attributesetinstance_id, units FROM stockcurrent")
    List<StockCurrent> findAllCustom();

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
    
    @Query("SELECT location, product, attributesetinstance_id, units FROM stockcurrent")
List<StockCurrent> findAllProjected();
}
