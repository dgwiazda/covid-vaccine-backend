package com.dgwiazda.covidvaccine.vaccinePoints;

import com.dgwiazda.covidvaccine.vaccinePoints.persistance.model.VaccinePointsEntity;
import com.dgwiazda.covidvaccine.vaccinePoints.service.VaccinePointsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping(VaccinePointsResource.RESOURCE_URL)
public class VaccinePointsResource {

    static final String RESOURCE_URL = "/vaccine-points";
    private static final String TOWN_COUNT = "/town/count/{town}";
    private static final String TOWN = "/town/{town}";

    private final VaccinePointsService vaccinePointsService;

    @GetMapping(TOWN_COUNT)
    public ResponseEntity<Integer> getVaccinePointsCountByTown(@PathVariable String town) {
        return ResponseEntity.ok(vaccinePointsService.getVaccinePointsCountByTown(town));
    }

    @GetMapping(TOWN)
    public ResponseEntity<List<VaccinePointsEntity>> getVaccinePointsByTown(@PathVariable String town) {
        return ResponseEntity.ok(vaccinePointsService.getVaccinePointsByTown(town));
    }

    @GetMapping()
    public ResponseEntity<List<VaccinePointsEntity>> getVaccinePoints() {
        return ResponseEntity.ok(vaccinePointsService.getAll());
    }
}
