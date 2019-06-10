package com.bosch.pai.bearing.sensordatastore.filter;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.math3.util.MathUtils;

/**
 * Created by HKH5KOR on 11/3/2017.
 */
public class KalmanFilter {

    private Double R = 1.0;
    private Double Q = 1.0;
    private Double A = 1.0;
    private Double B = 0.0;
    private Double C = 1.0;
    private Double cov;
    private Double x;

    /**
     * Instantiates a new Kalman filter.
     *
     * @param R the r
     * @param Q the q
     * @param A the a
     * @param B the b
     * @param C the c
     */
    public KalmanFilter(Double R, Double Q, Double A, Double B, Double C) {
        this.R = R;
        this.Q = Q;
        this.A = A;
        this.B = B;
        this.C = C;
        this.cov = null;
        this.x = null;
    }


    /**
     * Filter double.
     *
     * @param z the z
     * @param u the u
     * @return the double
     */
    public Double filter(Double z, Double u) {
        if (this.x == null) {
            this.x = (1 / this.C) * z;
            this.cov = (1 / this.C) * this.Q * (1 / this.C);
        } else {
            // Compute prediction
            final Double predX = (this.A * this.x) + (this.B * u);
            final Double predCov = ((this.A * this.cov) * this.A) + this.R;

            // Kalman gain
            final Double K = predCov * this.C * (1 / ((this.C * predCov * this.C) + this.Q));

            // Correction
            this.x = predX + K * (z - (this.C * predX));
            this.cov = predCov - (K * this.C * predCov);
        }

        return this.x;
    }

}
