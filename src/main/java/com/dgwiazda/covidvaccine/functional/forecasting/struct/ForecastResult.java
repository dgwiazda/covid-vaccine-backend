package com.dgwiazda.covidvaccine.functional.forecasting.struct;

public class ForecastResult {

    private final double[] forecast;
    private final double dataVariance;
    private final StringBuilder log;
    private double modelRMSE;

    /**
     * Constructor for ForecastResult
     *
     * @param pForecast     forecast data
     * @param pDataVariance data variance of the original data
     */
    public ForecastResult(final double[] pForecast, final double pDataVariance) {

        this.forecast = pForecast;

        this.dataVariance = pDataVariance;

        this.modelRMSE = -1;

        this.log = new StringBuilder();
    }

    /**
     * Compute normalized variance
     *
     * @param v variance
     * @return Normalized variance
     */
    private double getNormalizedVariance(final double v) {
        if (v < -0.5 || dataVariance < -0.5) {
            return -1;
        } else if (dataVariance < 0.0000001) {
            return v;
        } else {
            return Math.abs(v / dataVariance);
        }
    }

    /**
     * Setter for Root Mean-Squared Error
     *
     * @param rmse Root Mean-Squared Error
     */
    void setRMSE(double rmse) {
        this.modelRMSE = rmse;
    }

    /**
     * Compute and set confidence intervals
     *
     * @param constant          confidence interval constant
     * @param cumulativeSumOfMA cumulative sum of MA coefficients
     * @return Max Normalized Variance
     */
    public double setConfInterval(final double constant, final double[] cumulativeSumOfMA) {
        double maxNormalizedVariance = -1.0;
        double bound;
        for (int i = 0; i < forecast.length; i++) {
            bound = constant * modelRMSE * cumulativeSumOfMA[i];
            final double normalizedVariance = getNormalizedVariance(Math.pow(bound, 2));
            if (normalizedVariance > maxNormalizedVariance) {
                maxNormalizedVariance = normalizedVariance;
            }
        }
        return maxNormalizedVariance;
    }

    /**
     * Getter for forecast data
     *
     * @return forecast data
     */
    public double[] getForecast() {
        return forecast;
    }

    /**
     * Append message to log of forecast result
     *
     * @param message string message
     */
    public void log(String message) {
        this.log.append(message).append("\n");
    }
}
