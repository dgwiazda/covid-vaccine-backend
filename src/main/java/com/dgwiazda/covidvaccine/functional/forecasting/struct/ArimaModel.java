package com.dgwiazda.covidvaccine.functional.forecasting.struct;

import com.dgwiazda.covidvaccine.functional.forecasting.ArimaSolver;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArimaModel {

    private final ArimaParams params;
    private final double[] data;
    private final int trainDataSize;
    private double rmse;

    /**
     * Constructor for ArimaModel
     *
     * @param params ARIMA parameter
     * @param data original data
     * @param trainDataSize size of train data
     */
    public ArimaModel(ArimaParams params, double[] data, int trainDataSize) {
        this.params = params;
        this.data = data;
        this.trainDataSize = trainDataSize;
    }

    /**
     * Forecast data base on training data and forecast size.
     *
     * @param forecastSize size of forecast
     * @return forecast result
     */
    public ForecastResult forecast(final int forecastSize) {
        ForecastResult forecastResult = ArimaSolver.forecastARIMA(
                params,
                data,
                trainDataSize,
                trainDataSize + forecastSize
        );
        forecastResult.setRMSE(rmse);
        return forecastResult;
    }

}
