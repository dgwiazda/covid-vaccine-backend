package com.dgwiazda.covidvaccine.statistics;

import com.dgwiazda.covidvaccine.functional.files.service.FilesService;
import com.dgwiazda.covidvaccine.statistics.dto.ChartDataDTO;
import com.dgwiazda.covidvaccine.statistics.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(StatisticsResource.STATISTICS)
@CrossOrigin(origins = "*")
public class StatisticsResource {

    static final String STATISTICS = "/statistics";

    private final String PREDICTION = "/prediction/{province}";

    private final String INFECTIONS = "/infections/count/province/{province}/deaths/{deaths}";

    private final String VACCINES = "/vaccines";
    private final String VACCINES_COUNT = VACCINES + "/count/{province}";

    private final String NOP_COUNT = "/nop/count";
    private final String NOP_COUNT_GROUPBY_SEX = NOP_COUNT + "/group-by/sex";
    private final String NOP_COUNT_BY_NOP_AND_SEX = NOP_COUNT + "/nop/{nop}/sex/{sex}";

    private final String UPDATE_DATE = "/update-date";

    private final StatisticsService statisticsService;
    private final FilesService filesService;

    @GetMapping(PREDICTION)
    public ResponseEntity<ChartDataDTO> getPredictionByProvince(@PathVariable String province) {
        return ResponseEntity.ok(statisticsService.predictNextTenDaysByProvince(province));
    }

    @GetMapping(INFECTIONS)
    public ResponseEntity<ChartDataDTO> getInfectionsCountByProvinceAndDeaths(@PathVariable String province, @PathVariable Boolean deaths) {
        return ResponseEntity.ok(statisticsService.getInfectionsDeathsByProvince(province, deaths));
    }

    @GetMapping(NOP_COUNT)
    public ResponseEntity<Long> getNopRowCount() {
        return ResponseEntity.ok(statisticsService.getRowsCount());
    }

    @GetMapping(NOP_COUNT_GROUPBY_SEX)
    public ResponseEntity<List<Long>> getRowsCountGroupBySex() {
        return ResponseEntity.ok(statisticsService.getRowsCountGroupBySex());
    }

    @GetMapping(NOP_COUNT_BY_NOP_AND_SEX)
    public ResponseEntity<List<Long>> getChoosenNopCountBySexGroupBySex(@PathVariable String nop, @PathVariable String sex) {
        return ResponseEntity.ok(statisticsService.getChoosenNopCountBySexGroupBySex(nop, sex));
    }

    @GetMapping(UPDATE_DATE)
    public ResponseEntity<LocalDateTime> getUpdateDateTime() {
        return ResponseEntity.ok(filesService.getUpdateDateTime());
    }

    @GetMapping(VACCINES)
    public ResponseEntity<Long> getLastVaccinesCount() {
        return ResponseEntity.ok(statisticsService.getLastVaccinesCount());
    }

    @GetMapping(VACCINES_COUNT)
    public ResponseEntity<ChartDataDTO> getVaccinesCountByProvince(@PathVariable String province) {
        return ResponseEntity.ok(statisticsService.getVaccinesCountByProvince(province));
    }
}
