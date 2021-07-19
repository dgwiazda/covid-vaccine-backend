package com.dgwiazda.covidvaccine.statistics.infections.persistance.dao;

import com.dgwiazda.covidvaccine.statistics.infections.persistance.model.InfectionsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InfectionsRepository extends JpaRepository<InfectionsEntity, Long> {

    List<InfectionsEntity> getAllByProvinceOrderByDateAsc(String province);

    List<InfectionsEntity> getAllByProvinceAndDateAfterOrderByDateAsc(String province, LocalDate tenDaysAgo);
}
