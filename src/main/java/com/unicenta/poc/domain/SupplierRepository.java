package com.unicenta.poc.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends PagingAndSortingRepository<Supplier, String> {

    public Optional<Supplier> findById(String id);

    public Supplier save(Supplier supplier);

    public boolean existsById(String id);

    public void deleteById(String id);

    public void deleteAll();

    public List<Supplier> findTop10BySearchkeyContainingIgnoreCase(String searchTerm);
}
