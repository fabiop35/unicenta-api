package com.unicenta.poc.application;

import com.unicenta.poc.domain.ProductRepository;
import com.unicenta.poc.domain.StockDiary;
import com.unicenta.poc.domain.StockDiaryRepository;
import com.unicenta.poc.domain.Supplier;
import com.unicenta.poc.domain.SupplierRepository;
import com.unicenta.poc.domain.exceptions.ResourceNotFoundException;
import com.unicenta.poc.interfaces.dto.SupplierDto;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final StockDiaryRepository stockDiaryRepository;
    private final ProductRepository productRepository;
    private static final Logger log = LoggerFactory.getLogger(SupplierService.class);

    public SupplierService(SupplierRepository supplierRepository, StockDiaryRepository stockDiaryRepository, ProductRepository productRepository) {
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
        this.stockDiaryRepository = stockDiaryRepository;
    }

    @Transactional(readOnly = true)
    public Page<Supplier> getAllSuppliers(Pageable pageable) {
        return supplierRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Supplier getSupplierById(String id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));
    }

    @Transactional
    public Supplier createSupplier(SupplierDto dto) {
        Supplier supplier = new Supplier(dto.getSearchkey(), dto.getName(), dto.getMaxdebt());
        mapDtoToEntity(dto, supplier);
        return supplierRepository.save(supplier);
    }

    @Transactional
    public Supplier updateSupplier(String id, SupplierDto dto) {
        Supplier supplier = getSupplierById(id);
        mapDtoToEntity(dto, supplier);
        supplier.setNew(false);
        return supplierRepository.save(supplier);
    }

    @Transactional
    public void deleteSupplier(String id) {
        if (!supplierRepository.existsById(id)) {
            throw new ResourceNotFoundException("Supplier not found with id: " + id);
        }
        supplierRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Supplier> searchSuppliers(String searchTerm) {
        log.info("Searching for suppliers with term: '{}'", searchTerm);
        List<Supplier> results = supplierRepository.findTop10BySearchkeyContainingIgnoreCase(searchTerm);
        log.info("Found {} suppliers for term '{}'", results.size(), searchTerm);
        return results;
    }

    /**
     * Retrieves all StockDiary records associated with a specific supplier,
     * including the product name.
     *
     * @param supplierId The ID of the supplier.
     * @return A list of StockDiary records with product names populated.
     */
    @Transactional(readOnly = true)
    public List<StockDiary> getStockDiaryBySupplier(String supplierId) {
        // Fetch all stock diary records for the given supplier
        List<StockDiary> stockDiaries = stockDiaryRepository.findBySupplier(supplierId);

        // Map product IDs to product names
        return stockDiaries.stream()
                .peek(sd -> {
                    productRepository.findById(sd.getProductId())
                            .ifPresent(p -> sd.setProductName(p.getName()));
                })
                .collect(Collectors.toList());
    }

    private void mapDtoToEntity(SupplierDto dto, Supplier supplier) {
        supplier.setSearchkey(dto.getSearchkey());
        supplier.setName(dto.getName());
        supplier.setMaxdebt(dto.getMaxdebt());
        supplier.setTaxid(dto.getTaxid());
        supplier.setAddress(dto.getAddress());
        supplier.setAddress2(dto.getAddress2());
        supplier.setPostal(dto.getPostal());
        supplier.setCity(dto.getCity());
        supplier.setRegion(dto.getRegion());
        supplier.setCountry(dto.getCountry());
        supplier.setFirstname(dto.getFirstname());
        supplier.setLastname(dto.getLastname());
        supplier.setEmail(dto.getEmail());
        supplier.setPhone(dto.getPhone());
        supplier.setPhone2(dto.getPhone2());
        supplier.setFax(dto.getFax());
        supplier.setNotes(dto.getNotes());
        supplier.setVisible(dto.isVisible());
        supplier.setCurdebt(dto.getCurdebt());
        supplier.setVatid(dto.getVatid());
        supplier.setCurdate(dto.getCurdate());
    }
}
