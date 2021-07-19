package com.dgwiazda.covidvaccine.functional.forecasting;

import com.dgwiazda.covidvaccine.functional.forecasting.matrix.InsightsMatrix;
import com.dgwiazda.covidvaccine.functional.forecasting.matrix.InsightsVector;
import com.dgwiazda.covidvaccine.functional.forecasting.struct.ArimaParams;
import com.dgwiazda.covidvaccine.functional.forecasting.util.ForecastUtil;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class HannanRissanen {

    /**
     * Estimate ARMA(p,q) parameters, i.e. AR-parameters: \phi_1, ... , \phi_p
     * MA-parameters: \theta_1, ... , \theta_q
     * Input data is assumed to be stationary, has zero-mean, aligned, and imputed
     *
     * @param data_orig       original data
     * @param params          ARIMA parameters
     * @param forecast_length forecast length
     * @param maxIteration    maximum number of iteration
     */
    public static void estimateARMA(final double[] data_orig, final ArimaParams params, final int forecast_length, final int maxIteration) {
        final double[] data = new double[data_orig.length];
        final int total_length = data.length;
        System.arraycopy(data_orig, 0, data, 0, total_length);
        final int r = (params.getDegreeP() > params.getDegreeQ()) ? 1 + params.getDegreeP() : 1 + params.getDegreeQ();
        final int length = total_length - forecast_length;
        final int size = length - r;
        if (length < 2 * r) {
            throw new RuntimeException("not enough data points: length=" + length + ", r=" + r);
        }

        // step 1: apply Yule-Walker method and estimate AR(r) model on input data
        final double[] errors = new double[length];
        for (int j = 0; j < r; ++j) {
            errors[j] = 0;
        }

        // step 2: iterate Least-Square fitting until the parameters converge
        // instantiate Z-matrix
        final double[][] matrix = new double[params.getNumParamsP() + params.getNumParamsQ()][size];

        double bestRMSE = -1; // initial value
        int remainIteration = maxIteration;
        InsightsVector bestParams = null;
        while (--remainIteration >= 0) {
            final InsightsVector estimatedParams = iterationStep(params, data, errors, matrix, r, size);
            params.setParamsFromVector(estimatedParams);

            // forecast for validation data and compute RMSE
            final double[] forecasts = ArimaSolver.forecastARMA(params, data, length, data.length);
            final double anotherRMSE = ArimaSolver.computeRMSE(data, forecasts, length, 0, forecast_length);
            // update errors
            final double[] train_forecasts = ArimaSolver.forecastARMA(params, data, r, data.length);
            for (int j = 0; j < size; ++j) {
                errors[j + r] = data[j + r] - train_forecasts[j];
            }
            if (bestRMSE < 0 || anotherRMSE < bestRMSE) {
                bestParams = estimatedParams;
                bestRMSE = anotherRMSE;
            }
        }
        params.setParamsFromVector(bestParams);
    }

    private static InsightsVector iterationStep(final ArimaParams params, final double[] data, final double[] errors,
                                                final double[][] matrix, final int r, final int size) {

        int rowIdx = 0;
        // copy over shifted timeseries data into matrix
        final int[] offsetsAR = params.getOffsetsAR();
        for (int pIdx : offsetsAR) {
            System.arraycopy(data, r - pIdx, matrix[rowIdx], 0, size);
            ++rowIdx;
        }
        // copy over shifted errors into matrix
        final int[] offsetsMA = params.getOffsetsMA();
        for (int qIdx : offsetsMA) {
            System.arraycopy(errors, r - qIdx, matrix[rowIdx], 0, size);
            ++rowIdx;
        }

        // instantiate matrix to perform least squares algorithm
        final InsightsMatrix zt = new InsightsMatrix(matrix, false);

        // instantiate target vector
        final double[] vector = new double[size];
        System.arraycopy(data, r, vector, 0, size);
        final InsightsVector x = new InsightsVector(vector, false);

        // obtain least squares solution
        final InsightsVector ztx = zt.timesVector(x);
        final InsightsMatrix ztz = zt.computeAAT();

        return ztz.solveSPDIntoVector(ztx, ForecastUtil.MAX_CONDITION_NUMBER);
    }
}
