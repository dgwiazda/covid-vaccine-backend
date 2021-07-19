package com.dgwiazda.covidvaccine.statistics.nop.persistance.dao;

import com.dgwiazda.covidvaccine.statistics.nop.persistance.model.NopEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NopRepository extends JpaRepository<NopEntity, Long> {

    long count();

    @Query(value = "SELECT COUNT(*) FROM t_nop GROUP BY sex", nativeQuery = true)
    List<Long> countBySex();

    @Query(value = "SELECT n1.count FROM (SELECT COUNT(*) AS count, sex FROM t_nop WHERE nop_description ILIKE :nop GROUP BY sex) AS n1 WHERE n1.sex = :tSex", nativeQuery = true)
    List<Long> getCountByNopGroupBySex(@Param("nop") String nop, @Param("tSex") String tSex);
}
