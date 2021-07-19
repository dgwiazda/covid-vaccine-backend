package com.dgwiazda.covidvaccine.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChartDataDTO {

    private List<Long> data;
    private List<String> time;
}
