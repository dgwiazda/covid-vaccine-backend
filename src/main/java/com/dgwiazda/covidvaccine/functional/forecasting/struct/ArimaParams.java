package com.dgwiazda.covidvaccine.functional.forecasting.struct;

import com.dgwiazda.covidvaccine.functional.forecasting.matrix.InsightsVector;
import com.dgwiazda.covidvaccine.functional.forecasting.util.Integrator;

public final class ArimaParams {

    public final int p;
    public final int d;
    public final int q;
    public final int P;
    public final int D;
    public final int Q;
    public final int m;
    // ARMA part
    private final BackShift opAR;
    private final BackShift opMA;
    private final int dp;
    private final int dq;
    private final int np;
    private final int nq;
    private final double[][] init_seasonal;
    private final double[][] diff_seasonal;
    private final double[][] integrate_seasonal;
    private final double[][] init_non_seasonal;
    private final double[][] diff_non_seasonal;
    private final double[][] integrate_non_seasonal;

    /**
     * Constructor for ArimaParams
     *
     * @param p ARIMA parameter, the order (number of time lags) of the autoregressive model
     * @param d ARIMA parameter, the degree of differencing
     * @param q ARIMA parameter, the order of the moving-average model
     * @param P ARIMA parameter, autoregressive term for the seasonal part
     * @param D ARIMA parameter, differencing term for the seasonal part
     * @param Q ARIMA parameter, moving average term for the seasonal part
     * @param m ARIMA parameter, the number of periods in each season
     */
    public ArimaParams(int p, int d, int q, int P, int D, int Q, int m) {
        this.p = p;
        this.d = d;
        this.q = q;
        this.P = P;
        this.D = D;
        this.Q = Q;
        this.m = m;

        // dependent states
        this.opAR = getNewOperatorAR();
        this.opMA = getNewOperatorMA();
        opAR.initializeParams(false);
        opMA.initializeParams(false);
        this.dp = opAR.getDegree();
        this.dq = opMA.getDegree();
        this.np = opAR.numParams();
        this.nq = opMA.numParams();
        this.init_seasonal = (D > 0 && m > 0) ? new double[D][m] : null;
        this.init_non_seasonal = (d > 0) ? new double[d][1] : null;
        this.diff_seasonal = (D > 0 && m > 0) ? new double[D][] : null;
        this.diff_non_seasonal = (d > 0) ? new double[d][] : null;
        this.integrate_seasonal = (D > 0 && m > 0) ? new double[D][] : null;
        this.integrate_non_seasonal = (d > 0) ? new double[d][] : null;
    }

    /**
     * ARMA forecast of one data point.
     *
     * @param data   input data
     * @param errors array of errors
     * @param index  index
     * @return one data point
     */
    public double forecastOnePointARMA(final double[] data, final double[] errors, final int index) {
        final double estimateAR = opAR.getLinearCombinationFrom(data, index);
        final double estimateMA = opMA.getLinearCombinationFrom(errors, index);
        return estimateAR + estimateMA;
    }

    /**
     * Getter for the degree of parameter p
     *
     * @return degree of p
     */
    public int getDegreeP() {
        return dp;
    }

    /**
     * Getter for the degree of parameter q
     *
     * @return degree of q
     */
    public int getDegreeQ() {
        return dq;
    }

    /**
     * Getter for the number of parameters p
     *
     * @return number of parameters p
     */
    public int getNumParamsP() {
        return np;
    }

    /**
     * Getter for the number of parameters q
     *
     * @return number of parameters q
     */
    public int getNumParamsQ() {
        return nq;
    }

    /**
     * Getter for the parameter offsets of AR
     *
     * @return parameter offsets of AR
     */
    public int[] getOffsetsAR() {
        return opAR.paramOffsets();
    }

    /**
     * Getter for the parameter offsets of MA
     *
     * @return parameter offsets of MA
     */
    public int[] getOffsetsMA() {
        return opMA.paramOffsets();
    }

    /**
     * Getter for the last integrated seasonal data
     *
     * @return integrated seasonal data
     */
    public double[] getLastIntegrateSeasonal() {
        return integrate_seasonal[D - 1];
    }

    /**
     * Getter for the last integrated NON-seasonal data
     *
     * @return NON-integrated NON-seasonal data
     */
    public double[] getLastIntegrateNonSeasonal() {
        return integrate_non_seasonal[d - 1];
    }

    /**
     * Getter for the last differentiated seasonal data
     *
     * @return differentiate seasonal data
     */
    public double[] getLastDifferenceSeasonal() {
        return diff_seasonal[D - 1];
    }

    /**
     * Getter for the last differentiated NON-seasonal data
     *
     * @return differentiated NON-seasonal data
     */
    public double[] getLastDifferenceNonSeasonal() {
        return diff_non_seasonal[d - 1];
    }

    /**
     * Summary of the parameters
     *
     * @return String of summary
     */
    public String summary() {
        return "ModelInterface ParamsInterface:" +
                ", p= " + p +
                ", d= " + d +
                ", q= " + q +
                ", P= " + P +
                ", D= " + D +
                ", Q= " + Q +
                ", m= " + m;
    }

    /**
     * Setting parameters from a Insight Vector
     * <p>
     * It is assumed that the input vector has _np + _nq entries first _np entries are AR-parameters
     * and the last _nq entries are MA-parameters
     *
     * @param paramVec a vector of parameters
     */
    public void setParamsFromVector(final InsightsVector paramVec) {
        int index = 0;
        final int[] offsetsAR = getOffsetsAR();
        final int[] offsetsMA = getOffsetsMA();
        for (int pIdx : offsetsAR) {
            opAR.setParam(pIdx, paramVec.get(index++));
        }
        for (int qIdx : offsetsMA) {
            opMA.setParam(qIdx, paramVec.get(index++));
        }
    }

    /**
     * Create a Insight Vector that contains the parameters.
     * <br/>
     * It is assumed that the input vector has _np + _nq entries first _np entries are AR-parameters
     * and the last _nq entries are MA-parameters
     *
     * @return Insight Vector of parameters
     */
    public InsightsVector getParamsIntoVector() {
        int index = 0;
        final InsightsVector paramVec = new InsightsVector(np + nq, 0.0);
        final int[] offsetsAR = getOffsetsAR();
        final int[] offsetsMA = getOffsetsMA();
        for (int pIdx : offsetsAR) {
            paramVec.set(index++, opAR.getParam(pIdx));
        }
        for (int qIdx : offsetsMA) {
            paramVec.set(index++, opMA.getParam(qIdx));
        }
        return paramVec;
    }

    public BackShift getNewOperatorAR() {
        return mergeSeasonalWithNonSeasonal(p, P, m);
    }

    public BackShift getNewOperatorMA() {
        return mergeSeasonalWithNonSeasonal(q, Q, m);
    }

    public double[] getCurrentARCoefficients() {
        return opAR.getCoefficientsFlattened();
    }

    public double[] getCurrentMACoefficients() {
        return opMA.getCoefficientsFlattened();
    }

    private BackShift mergeSeasonalWithNonSeasonal(int nonSeasonalLag, int seasonalLag, int seasonalStep) {
        final BackShift nonSeasonal = new BackShift(nonSeasonalLag, true);
        final BackShift seasonal = new BackShift(seasonalLag * seasonalStep, false);
        for (int s = 1; s <= seasonalLag; ++s) {
            seasonal.setIndex(s * seasonalStep, true);
        }
        return seasonal.apply(nonSeasonal);
    }

    public void differentiateSeasonal(final double[] data) {
        double[] current = data;
        for (int j = 0; j < D; ++j) {
            final double[] next = new double[current.length - m];
            diff_seasonal[j] = next;
            final double[] init = init_seasonal[j];
            Integrator.differentiate(current, next, init, m);
            current = next;
        }
    }

    public void differentiateNonSeasonal(final double[] data) {
        double[] current = data;
        for (int j = 0; j < d; ++j) {
            final double[] next = new double[current.length - 1];
            diff_non_seasonal[j] = next;
            final double[] init = init_non_seasonal[j];
            Integrator.differentiate(current, next, init, 1);
            current = next;
        }
    }

    public void integrateSeasonal(final double[] data) {
        double[] current = data;
        for (int j = 0; j < D; ++j) {
            final double[] next = new double[current.length + m];
            integrate_seasonal[j] = next;
            final double[] init = init_seasonal[j];
            Integrator.integrate(current, next, init, m);
            current = next;
        }
    }

    public void integrateNonSeasonal(final double[] data) {
        double[] current = data;
        for (int j = 0; j < d; ++j) {
            final double[] next = new double[current.length + 1];
            integrate_non_seasonal[j] = next;
            final double[] init = init_non_seasonal[j];
            Integrator.integrate(current, next, init, 1);
            current = next;
        }
    }
}
