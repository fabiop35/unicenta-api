package com.unicenta.poc.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends PagingAndSortingRepository<Product, String> {

    //@Override // Though optional, it helps confirm you're overriding the correct method
    <S extends Product> S save(S entity);
    public Optional<Product> findById(String id);
    public List<Product> findTop10ByNameContainingIgnoreCase(String name);
    public List<Product> findTop10ByCodeContainingIgnoreCase(String code);
    
    @Query("SELECT CONCAT('REF-', LPAD(COUNT(*) + 1, 10, '0')) AS next_reference FROM products WHERE reference LIKE 'REF-%' ORDER BY reference DESC LIMIT 1")
    public String getNextProductRerence();
    
    
    @Query("SELECT MAX(catorder)+1 FROM products_cat")
    public int getNextCatOrder();
    
    @Modifying
    @Query("""
        INSERT INTO products_cat (product, catorder)
          VALUES (:product, :catorder) 
             """)
    public void saveProductsCat(String product, int catorder);
}
