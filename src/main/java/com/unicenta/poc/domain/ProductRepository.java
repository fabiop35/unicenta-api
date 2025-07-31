package com.unicenta.poc.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends PagingAndSortingRepository<Product, String> {
    
     //@Override // Though optional, it helps confirm you're overriding the correct method
    <S extends Product> S save(S entity);
    public Optional<Product> findById(String id);
    public List<Product> findTop10ByNameContainingIgnoreCase(String name);
}
