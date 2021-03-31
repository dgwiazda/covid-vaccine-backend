package com.dgwiazda.covidvaccine.services.repository;

import com.dgwiazda.covidvaccine.persistance.model.VaccineTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VaccineTypeRepository extends JpaRepository<VaccineTypeEntity, Long> {

    VaccineTypeEntity getVaccineTypeEntitiesByName(String name);
}
