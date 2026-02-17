package com.unicenta.poc.domain;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends ListCrudRepository<Category, String> {
    
//    @Query("")
//    public Category findById(ID id);
}
