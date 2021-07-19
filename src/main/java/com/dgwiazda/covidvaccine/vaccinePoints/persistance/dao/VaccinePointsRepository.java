package com.dgwiazda.covidvaccine.vaccinePoints.persistance.dao;

import com.dgwiazda.covidvaccine.vaccinePoints.persistance.model.VaccinePointsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaccinePointsRepository extends JpaRepository<VaccinePointsEntity, Long> {

    long count();

    Integer countByTownIgnoreCase(String town);

    List<VaccinePointsEntity> findByTownIgnoreCase(String town);
}
