package com.dgwiazda.covidvaccine.statistics.service;

import com.dgwiazda.covidvaccine.statistics.dto.ChartDataDTO;
import com.dgwiazda.covidvaccine.functional.forecasting.Arima;
import com.dgwiazda.covidvaccine.functional.forecasting.struct.ArimaParams;
import com.dgwiazda.covidvaccine.functional.forecasting.struct.ForecastResult;
import com.dgwiazda.covidvaccine.statistics.infections.persistance.dao.InfectionsRepository;
import com.dgwiazda.covidvaccine.statistics.infections.persistance.model.InfectionsEntity;
import com.dgwiazda.covidvaccine.statistics.nop.persistance.dao.NopRepository;
import com.dgwiazda.covidvaccine.statistics.vaccines.persistance.dao.VaccinesRepository;
import com.dgwiazda.covidvaccine.statistics.vaccines.persistance.model.VaccinesEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final InfectionsRepository infectionsRepository;
    private final VaccinesRepository vaccinesRepository;
    private final NopRepository nopRepository;

    public ChartDataDTO getInfectionsDeathsByProvince(String province, boolean deaths) {
        ChartDataDTO chartDataDTO = new ChartDataDTO();
        if (deaths) {
            chartDataDTO.setData(getDeathsByProvince(province));
        } else {
            chartDataDTO.setData(getInfectionsByProvince(province));
        }
        chartDataDTO.setTime(getInfectionsDateByProvince(province));
        return chartDataDTO;
    }

    private List<String> getInfectionsDateByProvince(String province) {
        return infectionsRepository.getAllByProvinceOrderByDateAsc(province)
                .stream()
                .map(InfectionsEntity::getDate)
                .map(LocalDate::toString)
                .collect(Collectors.toList());
    }

    public ChartDataDTO predictNextTenDaysByProvince(String province) {
        LocalDate tenDaysAgo = LocalDate.now().minusDays(20);
        List<InfectionsEntity> lastInfections = infectionsRepository.getAllByProvinceAndDateAfterOrderByDateAsc(province, tenDaysAgo);
        double[] dataArray = new double[lastInfections.size()];
        List<String> dates = new ArrayList<>();
        for (int i = 0; i < lastInfections.size(); i++) {
            dataArray[i] = lastInfections.get(i).getInfectionCases();
        }
        // Set ARIMA model parameters.
        int p = 3;
        int d = 0;
        int q = 3;
        int P = 1;
        int D = 1;
        int Q = 0;
        int m = 0;
        int forecastSize = 10;

        // Obtain forecast result. The structure contains forecasted values and performance metric etc.
        ForecastResult forecastResult = Arima.forecast_arima(dataArray, forecastSize, new ArimaParams(p, d, q, P, D, Q, m));

        // Read forecast values
        double[] forecastData = forecastResult.getForecast();

        List<Long> predictedValues = new ArrayList<>();
        for (int i = 0; i < forecastData.length; i++) {
            predictedValues.add((long) Math.floor(forecastData[i]));
            dates.add(LocalDate.now().plusDays(i).toString());
        }
        return new ChartDataDTO(predictedValues, dates);
    }

    public Long getLastVaccinesCount() {
        return vaccinesRepository.findFirstByProvinceOrderByDateDesc("Cały kraj").getVaccinesCount();
    }

    public ChartDataDTO getVaccinesCountByProvince(String province) {
        ChartDataDTO chartDataDTO = new ChartDataDTO();
        List<Long> data = vaccinesRepository.findAllByProvinceOrderByDateAsc(province)
                .stream()
                .map(VaccinesEntity::getVaccinesCount)
                .collect(Collectors.toList());
        List<String> time = getVaccinesDateByProvince(province);
        chartDataDTO.setData(data);
        chartDataDTO.setTime(time);
        return chartDataDTO;
    }

    private List<String> getVaccinesDateByProvince(String province) {
        return vaccinesRepository.findAllByProvinceOrderByDateAsc(province)
                .stream()
                .map(VaccinesEntity::getDate)
                .map(LocalDate::toString)
                .collect(Collectors.toList());
    }

    public Long getRowsCount() {
        return nopRepository.count();
    }

    public List<Long> getRowsCountGroupBySex() {
        return nopRepository.countBySex();
    }

    public List<Long> getChoosenNopCountBySexGroupBySex(String nop, String sex) {
        return nopRepository.getCountByNopGroupBySex("%" + nop + "%", sex);
    }


    private List<Long> getInfectionsByProvince(String province) {
        return infectionsRepository.getAllByProvinceOrderByDateAsc(province)
                .stream()
                .map(InfectionsEntity::getInfectionCases)
                .collect(Collectors.toList());
    }

    private List<Long> getDeathsByProvince(String province) {
        return infectionsRepository.getAllByProvinceOrderByDateAsc(province)
                .stream()
                .map(InfectionsEntity::getDeaths)
                .collect(Collectors.toList());
    }
}
