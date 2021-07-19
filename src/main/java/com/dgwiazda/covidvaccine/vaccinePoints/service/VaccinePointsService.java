package com.dgwiazda.covidvaccine.vaccinePoints.service;

import com.dgwiazda.covidvaccine.vaccinePoints.persistance.model.VaccinePointsEntity;
import com.dgwiazda.covidvaccine.vaccinePoints.persistance.dao.VaccinePointsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VaccinePointsService {

    private final VaccinePointsRepository vaccinePointsRepository;

    public Integer getVaccinePointsCountByTown(String town) {
        return vaccinePointsRepository.countByTownIgnoreCase(town);
    }

    public List<VaccinePointsEntity> getAll() {
        return vaccinePointsRepository.findAll();
    }

    public List<VaccinePointsEntity> getVaccinePointsByTown(String town) {
        return vaccinePointsRepository.findByTownIgnoreCase(town);
    }
}
