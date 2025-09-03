package com.unicenta.poc.domain;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttributeSetInstanceRepository extends CrudRepository<AttributeSetInstance, String> {

    Optional<AttributeSetInstance> findById(String id);
}
