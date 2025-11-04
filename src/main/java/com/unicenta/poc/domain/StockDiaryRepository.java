package com.unicenta.poc.domain;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing stock movement history.
 *
 * This is the source of truth for inventory changes. All adjustments,
 * purchases, and sales are recorded here.
 *
 * Based on uniCenta oPOS model: - Primary key: id (UUID) - Foreign keys to
 * product, location, user, supplier - Drives updates to stockcurrent
 */
@Repository
public interface StockDiaryRepository extends CrudRepository<StockDiary, String> {

    /**
     * Find all movements for a supplier.
     */
    @Query("SELECT * FROM stockdiary WHERE supplier = :supplierId")
    List<StockDiary> findBySupplier(@Param("supplierId") String supplierId);

    /**
     * Find all movements for a product.
     */
    @Query("SELECT * FROM stockdiary WHERE product = :productId")
    List<StockDiary> findByProductId(@Param("productId") String productId);

    /**
     * Find all movements for a location.
     */
    @Query("SELECT * FROM stockdiary WHERE location = :locationId")
    List<StockDiary> findByLocationId(@Param("locationId") String locationId);

    /**
     * Find all movements within a date range.
     */
    @Query("SELECT * FROM stockdiary WHERE datenew BETWEEN :startDate AND :endDate")
    List<StockDiary> findByDateBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Find by product and date range.
     * @param productId
     * @param startDate
     * @param endDate
     * @return 
     */
    @Query("SELECT * FROM stockdiary WHERE product = :productId AND datenew BETWEEN :startDate AND :endDate")
    List<StockDiary> findByProductIdAndDateBetween(
            @Param("productId") String productId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Find by location and product.
     */
    @Query("SELECT * FROM stockdiary WHERE location = :locationId AND product = :productId")
    List<StockDiary> findByLocationIdAndProductId(
            @Param("locationId") String locationId,
            @Param("productId") String productId);

    /**
     * Find by location, product, and attribute set instance.
     *
     * @Query("SELECT * FROM stockdiary " + "WHERE location = :locationId " + "
     * AND product = :productId " + " AND COALESCE(attributesetinstance_id,
     * 'NULL') = COALESCE(:attributeSetInstanceId, 'NULL')") List<StockDiary>
     * findByLocationIdAndProductIdAndAttributeSetInstanceId(
     * @Param("locationId") String locationId,
     * @Param("productId") String productId,
     * @Param("attributeSetInstanceId") String attributeSetInstanceId);
     */
    /**
     * Find by location, product, and date range.
     */
    @Query("SELECT * FROM stockdiary "
            + "WHERE location = :locationId "
            + "  AND product = :productId "
            + "  AND datenew BETWEEN :startDate AND :endDate")
    List<StockDiary> findByLocationIdAndProductIdAndDateBetween(
            @Param("locationId") String locationId,
            @Param("productId") String productId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Find by location, product, attribute set instance, and date range.
     */
    @Query("SELECT * FROM stockdiary "
            + "WHERE location = :locationId "
            + "  AND product = :productId "
            + "  AND COALESCE(attributesetinstance_id, 'NULL') = COALESCE(:attributeSetInstanceId, 'NULL') "
            + "  AND datenew BETWEEN :startDate AND :endDate")
    List<StockDiary> findByLocationIdAndProductIdAndAttributeSetInstanceIdAndDateBetween(
            @Param("locationId") String locationId,
            @Param("productId") String productId,
            @Param("attributeSetInstanceId") String attributeSetInstanceId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Find by ID.
     */
    @Override
    Optional<StockDiary> findById(String id);

    /**
     * Find diary entries for a specific location, product, and optional
     * attribute set instance
     *
     * @param locationId
     * @param productId
     * @param attributeSetInstanceId
     * @return
     */
    @Query("""
        SELECT * FROM stockdiary 
        WHERE location = :locationId 
          AND product = :productId 
          AND COALESCE(attributesetinstance_id, 'NULL') = COALESCE(:attributeSetInstanceId, 'NULL')
        ORDER BY datenew DESC -- Order by date, newest first
        """)
    List<StockDiary> findByLocationIdAndProductIdAndAttributeSetInstanceId(
            @Param("locationId") String locationId,
            @Param("productId") String productId,
            @Param("attributeSetInstanceId") String attributeSetInstanceId);

    // Potentially add a paginated version later if needed
}
