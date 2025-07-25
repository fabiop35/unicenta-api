package com.unicenta.poc.domain;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaxCategoryRepository extends ListCrudRepository<TaxCategory, String> {
}
