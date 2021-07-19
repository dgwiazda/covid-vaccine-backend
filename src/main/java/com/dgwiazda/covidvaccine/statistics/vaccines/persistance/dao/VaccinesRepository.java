package com.dgwiazda.covidvaccine.statistics.vaccines.persistance.dao;

import com.dgwiazda.covidvaccine.statistics.vaccines.persistance.model.VaccinesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaccinesRepository extends JpaRepository<VaccinesEntity, Long> {

    VaccinesEntity findFirstByProvinceOrderByDateDesc(String province);

    List<VaccinesEntity> findAllByProvinceOrderByDateAsc(String province);
}
