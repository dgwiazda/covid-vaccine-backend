package com.dgwiazda.covidvaccine.functional.forecasting;

import com.dgwiazda.covidvaccine.functional.forecasting.struct.ArimaModel;
import com.dgwiazda.covidvaccine.functional.forecasting.struct.ArimaParams;
import com.dgwiazda.covidvaccine.functional.forecasting.struct.ForecastResult;
import com.dgwiazda.covidvaccine.functional.forecasting.util.ForecastUtil;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class Arima {

    /**
     * Raw-level ARIMA forecasting function.
     *
     * @param data UNMODIFIED, list of double numbers representing time-series with constant time-gap
     * @param forecastSize integer representing how many data points AFTER the data series to be
     *        forecasted
     * @param params ARIMA parameters
     * @return a ForecastResult object, which contains the forecasted values and/or error message(s)
     */
    public static ForecastResult forecast_arima(final double[] data, final int forecastSize, ArimaParams params) {
        try {
            final int p = params.p;
            final int d = params.d;
            final int q = params.q;
            final int P = params.P;
            final int D = params.D;
            final int Q = params.Q;
            final int m = params.m;
            final ArimaParams paramsForecast = new ArimaParams(p, d, q, P, D, Q, m);
            final ArimaParams paramsXValidation = new ArimaParams(p, d, q, P, D, Q, m);
            // estimate ARIMA model parameters for forecasting
            final ArimaModel fittedModel =
                    ArimaSolver.estimateARIMA(paramsForecast, data, data.length, data.length + 1);

            // compute RMSE to be used in confidence interval computation
            final double rmseValidation =
                    ArimaSolver.computeRMSEValidation(data, ForecastUtil.TEST_SET_PERCENTAGE, paramsXValidation);
            fittedModel.setRmse(rmseValidation);

            // successfully built ARIMA model and its forecast
            return fittedModel.forecast(forecastSize);

        } catch (final Exception ex) {
            // failed to build ARIMA model
            throw new RuntimeException("Failed to build ARIMA forecast: " + ex.getMessage());
        }
    }
}
