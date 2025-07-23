package com.unicenta.poc.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaxCategoryRepository extends JpaRepository<TaxCategory, String> {
}
