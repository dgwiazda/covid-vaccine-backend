package com.dgwiazda.covidvaccine.rest;

import com.dgwiazda.covidvaccine.persistance.model.VaccineTypeEntity;
import com.dgwiazda.covidvaccine.services.repository.VaccineTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vaccine-description")
@CrossOrigin(origins = "*", maxAge = 3600)
public class VaccineDescriptionController {

    @Autowired
    VaccineTypeRepository vaccineTypeRepository;

    public VaccineDescriptionController(VaccineTypeRepository vaccineTypeRepository) {
        this.vaccineTypeRepository = vaccineTypeRepository;
    }

    @GetMapping("/{name}")
    public ResponseEntity<VaccineTypeEntity> getVaccineDescriptionByName(@PathVariable String name) {
        return ResponseEntity.ok(vaccineTypeRepository.getVaccineTypeEntitiesByName(name));
    }
}
