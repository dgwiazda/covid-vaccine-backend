package com.dgwiazda.covidvaccine.functional.forecasting.matrix;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.Objects;

@Getter
@Setter
public class InsightsVector {

    private int vectorSize;
    private double[] data;
    private boolean valid;

    /**
     * Constructor for InsightVector
     *
     * @param localVectorSize size of the vector
     * @param value initial value for all entries
     */
    public InsightsVector(int localVectorSize, double value) {
        if (localVectorSize <= 0) {
            throw new RuntimeException("[InsightsVector] invalid size");
        } else {
            data = new double[localVectorSize];
            for (int j = 0; j < localVectorSize; ++j) {
                data[j] = value;
            }
            this.vectorSize = localVectorSize;
            valid = true;
        }
    }

    /**
     * Constructor for InsightVector
     *
     * @param data 1-dimensional double array with pre-populated values
     * @param deepCopy if TRUE, allocated new memory space and copy data over
     *                 if FALSE, re-use the given memory space and overwrites on it
     */
    public InsightsVector(double[] data, boolean deepCopy) {
        if (Objects.isNull(data) || data.length == 0) {
            throw new RuntimeException("[InsightsVector] invalid data");
        } else {
            vectorSize = data.length;
            if (deepCopy) {
                this.data = Arrays.copyOf(data, vectorSize);
            } else {
                this.data = data;
            }
            valid = true;
        }
    }

    /**
     * Create and allocate memory for a new copy of double array of current elements in the vector
     *
     * @return the new copy
     */
    public double[] deepCopy() {
        return Arrays.copyOf(data, vectorSize);
    }

    /**
     * Getter for the i-th element in the vector
     *
     * @param i element index
     * @return the i-th element
     */
    public double get(int i) {
        if (!valid) {
            throw new RuntimeException("[InsightsVector] invalid Vector");
        } else if (i >= vectorSize) {
            throw new IndexOutOfBoundsException(String.format("[InsightsVector] Index: %d, Size: %d", i, vectorSize));
        }
        return data[i];
    }

    /**
     * Getter for the size of the vector
     *
     * @return size of the vector
     */
    public int size() {
        if (!valid) {
            throw new RuntimeException("[InsightsVector] invalid Vector");
        }
        return vectorSize;
    }

    /**
     * Setter to modify a element in the vector
     *
     * @param i element index
     * @param val new value
     */
    public void set(int i, double val) {
        if (!valid) {
            throw new RuntimeException("[InsightsVector] invalid Vector");
        } else if (i >= vectorSize) {
            throw new IndexOutOfBoundsException(String.format("[InsightsVector] Index: %d, Size: %d", i, vectorSize));
        }
        data[i] = val;
    }

    /**
     * Perform dot product operation with another vector of the same size
     *
     * @param vector vector of the same size
     * @return dot product of the two vector
     */
    public double dot(InsightsVector vector) {
        if (!valid || !vector.isValid()) {
            throw new RuntimeException("[InsightsVector] invalid Vector");
        } else if (vectorSize != vector.size()) {
            throw new RuntimeException("[InsightsVector][dot] invalid vector size.");
        }

        double sumOfProducts = 0;
        for (int i = 0; i < vectorSize; i++) {
            sumOfProducts += data[i] * vector.get(i);
        }
        return sumOfProducts;
    }
}
