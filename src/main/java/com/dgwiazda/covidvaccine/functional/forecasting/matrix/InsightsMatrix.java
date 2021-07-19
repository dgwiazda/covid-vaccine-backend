package com.dgwiazda.covidvaccine.functional.forecasting.matrix;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.Objects;

@Getter
@Setter
public class InsightsMatrix {

    // Primary
    private int rowsCount = -1;
    private int columnsCount = -1;
    private double[][] data = null;
    private boolean valid;

    // Secondary
    private boolean cholZero = false;
    private boolean cholPos = false;
    private boolean cholNeg = false;
    private double[] cholD = null;
    private double[][] cholL = null;

    //=====================================================================
    // Constructors
    //=====================================================================

    /**
     * Constructor for InsightsMatrix
     *
     * @param data         2-dimensional double array with pre-populated values
     * @param makeDeepCopy if TRUE, allocated new memory space and copy data over
     *                     if FALSE, re-use the given memory space and overwrites on it
     */
    public InsightsMatrix(double[][] data, boolean makeDeepCopy) {
        if (valid = isValid2D(data)) {
            rowsCount = data.length;
            columnsCount = data[0].length;
            if (!makeDeepCopy) {
                this.data = data;
            } else {
                this.data = copy2DArray(data);
            }
        }
    }

    /**
     * Determine whether a 2-dimensional array is in valid matrix format.
     *
     * @param matrix 2-dimensional double array
     * @return TRUE, matrix is in valid format
     * FALSE, matrix is not in valid format
     */
    private static boolean isValid2D(double[][] matrix) {
        boolean result = true;
        if (Objects.isNull(matrix) || Objects.isNull(matrix[0]) || matrix[0].length == 0) {
            throw new RuntimeException("[InsightsMatrix][constructor] null data given");
        } else {
            int row = matrix.length;
            int col = matrix[0].length;
            for (int i = 1; i < row; ++i) {
                if (Objects.isNull(matrix[i]) || matrix[i].length != col) {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Create a copy of 2-dimensional double array by allocating new memory space and copy data over
     *
     * @param source source 2-dimensional double array
     * @return new copy of the source 2-dimensional double array
     */
    private static double[][] copy2DArray(double[][] source) {
        if (Objects.isNull(source)) {
            return null;
        } else if (source.length == 0) {
            return new double[0][];
        }

        int row = source.length;
        double[][] target = new double[row][];
        for (int i = 0; i < row; i++) {
            if (Objects.isNull(source[i])) {
                target[i] = null;
            } else {
                int rowLength = source[i].length;
                target[i] = Arrays.copyOf(source[i], rowLength);
            }
        }
        return target;
    }

    /**
     * Multiply a InsightMatrix (n x m) by a InsightVector (m x 1)
     *
     * @param v a InsightVector
     * @return a InsightVector of dimension (n x 1)
     */
    public InsightsVector timesVector(InsightsVector v) {
        if (!valid || !v.isValid() || columnsCount != v.getVectorSize()) {
            throw new RuntimeException("[InsightsMatrix][timesVector] size mismatch");
        }
        double[] data = new double[rowsCount];
        double dotProduc;
        for (int i = 0; i < rowsCount; ++i) {
            InsightsVector rowVector = new InsightsVector(this.data[i], false);
            dotProduc = rowVector.dot(v);
            data[i] = dotProduc;
        }
        return new InsightsVector(data, false);
    }

    /**
     * Compute the Cholesky Decomposition
     *
     * @param maxConditionNumber maximum condition number
     */
    private void computeCholeskyDecomposition(final double maxConditionNumber) {
        cholD = new double[rowsCount];
        cholL = new double[rowsCount][columnsCount];
        int i;
        int j;
        int k;
        double val;
        double currentMax = -1;
        // Backward marching method
        for (j = 0; j < columnsCount; ++j) {
            val = 0;
            for (k = 0; k < j; ++k) {
                val += cholD[k] * cholL[j][k] * cholL[j][k];
            }
            double diagTemp = data[j][j] - val;
            final int diagSign = (int) (Math.signum(diagTemp));
            switch (diagSign) {
                case 0:    // singular diagonal value detected
                    if (maxConditionNumber < -0.5) { // no bound on maximum condition number
                        cholZero = true;
                        cholL = null;
                        cholD = null;
                        return;
                    } else {
                        cholPos = true;
                    }
                    break;
                case 1:
                    cholPos = true;
                    break;
                case -1:
                    cholNeg = true;
                    break;
            }
            if (maxConditionNumber > -0.5) {
                if (currentMax <= 0.0) { // this is the first time
                    if (diagSign == 0) {
                        diagTemp = 1.0;
                    }
                } else { // there was precedent
                    if (diagSign == 0) {
                        diagTemp = Math.abs(currentMax / maxConditionNumber);
                    } else {
                        if (Math.abs(diagTemp * maxConditionNumber) < currentMax) {
                            diagTemp = diagSign * Math.abs(currentMax / maxConditionNumber);
                        }
                    }
                }
            }
            cholD[j] = diagTemp;
            if (Math.abs(diagTemp) > currentMax) {
                currentMax = Math.abs(diagTemp);
            }
            cholL[j][j] = 1;
            for (i = j + 1; i < rowsCount; ++i) {
                val = 0;
                for (k = 0; k < j; ++k) {
                    val += cholD[k] * cholL[j][k] * cholL[i][k];
                }
                val = ((data[i][j] + data[j][i]) / 2 - val) / cholD[j];
                cholL[j][i] = val;
                cholL[i][j] = val;
            }
        }
    }

    /**
     * Solve SPD(Symmetric positive definite) into vector
     *
     * @param vector             vector
     * @param maxConditionNumber maximum condition number
     * @return solution vector of SPD
     */
    public InsightsVector solveSPDIntoVector(InsightsVector vector, final double maxConditionNumber) {
        if (!valid || Objects.isNull(vector) || columnsCount != vector.getVectorSize()) {
            // invalid linear system
            throw new RuntimeException("[InsightsMatrix][solveSPDIntoVector] invalid linear system");
        }
        if (Objects.isNull(cholL)) {
            // computing Cholesky Decomposition
            this.computeCholeskyDecomposition(maxConditionNumber);
        }
        if (cholZero) {
            // singular matrix. returning null
            return null;
        }

        double[] y = new double[rowsCount];
        double[] bt = new double[columnsCount];
        int i;
        int j;
        for (i = 0; i < rowsCount; ++i) {
            bt[i] = vector.getData()[i];
        }
        double val;
        for (i = 0; i < rowsCount; ++i) {
            val = 0;
            for (j = 0; j < i; ++j) {
                val += cholL[i][j] * y[j];
            }
            y[i] = bt[i] - val;
        }
        for (i = rowsCount - 1; i >= 0; --i) {
            val = 0;
            for (j = i + 1; j < columnsCount; ++j) {
                val += cholL[i][j] * bt[j];
            }
            bt[i] = y[i] / cholD[i] - val;
        }
        return new InsightsVector(bt, false);
    }

    /**
     * Computu the product of the matrix (m x n) and its transpose (n x m)
     *
     * @return matrix of size (m x m)
     */
    public InsightsMatrix computeAAT() {
        if (!valid) {
            throw new RuntimeException("[InsightsMatrix][computeAAT] invalid matrix");
        }
        final double[][] data = new double[rowsCount][rowsCount];
        for (int i = 0; i < rowsCount; ++i) {
            final double[] rowI = this.data[i];
            for (int j = 0; j < rowsCount; ++j) {
                final double[] rowJ = this.data[j];
                double temp = 0;
                for (int k = 0; k < columnsCount; ++k) {
                    temp += rowI[k] * rowJ[k];
                }
                data[i][j] = temp;
            }
        }
        return new InsightsMatrix(data, false);
    }

    /**
     * Setter to modify a particular element in the matrix
     *
     * @param i   i-th row
     * @param j   j-th column
     * @param val new value
     */
    public void set(int i, int j, double val) {
        data[i][j] = val;
    }
}
