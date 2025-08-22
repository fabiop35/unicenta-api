package com.unicenta.poc.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaxRepository extends ListCrudRepository<Tax, String> {

    List<Tax> findAllByTaxcatIdIn(List<String> categories);
    public Optional<Tax> findByTaxcatId(String taxcatId);
}
