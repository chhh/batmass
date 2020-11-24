/*
 * Copyright 2020 chhh.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package umich.ms.batmass.gui.viewers.map2d.noise;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author chhh
 */
public class PolynomialUtils {

    public static class Maximum {

        public final int idxLo;
        public final int idxHi;
        public final double xLo;
        public final double xHi;
        public final double val;

        public Maximum(int idxdxLo, int idxHi, double xLo, double xHi, double val) {
            this.idxLo = idxdxLo;
            this.idxHi = idxHi;
            this.xLo = xLo;
            this.xHi = xHi;
            this.val = val;
        }
    }

    public static double[] FitParabolaToMaximum(Maximum p, double[] mzs, double[] abs) {
        int lo = p.idxLo - 1;
        int hi = p.idxLo + 1;
        if (lo < 0 || hi >= mzs.length) {
            throw new RuntimeException("Tried to fit parabolic peak to a first/last point in an array");
        }

        if (p.idxLo == p.idxHi) {
            // this is a peak with a single point at the top, all fine
            double[] x = new double[3];
            double[] y = new double[3];
            x[0] = mzs[lo];
            x[1] = mzs[p.idxLo];
            x[2] = mzs[hi];
            y[0] = abs[lo];
            y[1] = p.val;
            y[2] = abs[hi];
            double[] parabola = new double[3];
            parabola = FitParabola(x, y, parabola);
            return parabola;
        } else {
            throw new RuntimeException("Commented out the piece of code that required QR decomposition");
//                double[] x = new double[4];
//                double[] y = new double[4];
//                x[0] = mzs[lo];
//                x[1] = mzs[p.idxLo];
//                x[2] = mzs[p.idxHi];
//                x[3] = mzs[hi];
//                y[0] = abs[lo];
//                y[1] = p.val;
//                y[2] = p.val;
//                y[3] = abs[hi];
//                double[] poly2 = Polynomial(x, y, 2);
//                return poly2;
        }
    }

    public static List<Maximum> FindMaxima(double[] x, double[] y, int initSizeOfResultList) {
        if (x.length < 3) {
            return new ArrayList<>(0);
        }
        if (x.length != y.length) {
            throw new RuntimeException("X and Y data size must match.");
        }
        List<Maximum> maxima = new ArrayList<>(initSizeOfResultList);
        double valPre = Double.POSITIVE_INFINITY;
        double valCur;
        int possibleMaxPlateuStartIdx = -1;
        int possibleMaxPlateuEndIdx = -1;
        for (int i = 0; i < y.length; i++) {
            valCur = y[i];
            if (valCur > valPre) { // climbing up
                possibleMaxPlateuStartIdx = i;
                possibleMaxPlateuEndIdx = i;
            } else if (valCur == valPre) { // on a high plateu
                possibleMaxPlateuEndIdx = i;
            } else if (valCur < valPre) { // found a peak/high plateu
                if (possibleMaxPlateuStartIdx > 0 && possibleMaxPlateuEndIdx > 0) {
                    maxima.add(new Maximum(
                            possibleMaxPlateuStartIdx, possibleMaxPlateuEndIdx,
                            x[possibleMaxPlateuStartIdx], x[possibleMaxPlateuEndIdx],
                            y[possibleMaxPlateuStartIdx]));
                }
                possibleMaxPlateuEndIdx = -1;
                possibleMaxPlateuStartIdx = -1;
            }
            valPre = valCur;
        }
        return maxima;
    }

    /// <summary>
    /// Fits a parabola to 3 points, returns
    /// </summary>
    /// <param name="x">array of x coordinates, length 3</param>
    /// <param name="y">array of y coordinates, length 3</param>
    /// <param name="parabola">an array of 4 values, will be populated with c,b,a (in that order!) and the x0 of the apex (a*x^2 + b*x + c)</param>
    /// <returns></returns>
    public static double[] FitParabola(double[] x, double[] y, double[] parabola) {
        if (x.length != 3) {
            throw new RuntimeException("Length of input data arrays must be 3.");
        }
        if (parabola.length != 3) {
            throw new RuntimeException("Length of the output parabola array must be 3.");
        }
        if (x.length != y.length) {
            throw new RuntimeException("Arrays x and y must be of the same length: 3.");
        }
        double a = (y[1] * (x[2] - x[0]) - y[0] * (x[2] - x[1]) - y[2] * (x[1] - x[0])) / (Math.pow(x[0], 2) * (x[1] - x[2]) - Math.pow(x[2], 2) * (x[1] - x[0]) - Math.pow(x[1], 2) * (x[0] - x[2]));
        double b = (y[1] - y[0] + a * (Math.pow(x[0], 2) - Math.pow(x[1], 2))) / (x[1] - x[0]);
        double c = -1 * a * Math.pow(x[0], 2) - b * x[0] + y[0];
        parabola[0] = c;
        parabola[1] = b;
        parabola[2] = a;
        return parabola;
    }

    /// <summary>
    /// Find the X position of the vertex of the parabola (a*x^2 + b*x + c).
    /// </summary>
    /// <param name="a">a*x^2</param>
    /// <param name="b">b*x</param>
    /// <returns></returns>
    public static double ParabolaVertexX(double a, double b) {
        return -1 * b / (2.0 * a);
    }

    /// <summary>
    /// Fit a polynomial to a set of data points by Gaussian elimination
    /// </summary>
    /// <param name="x"></param>
    /// <param name="y"></param>
    /// <param name="polynomial">The degree of the fitted polynomial is determined by the size of this array, coefficients correspond to the index in the array</param>
    /// <returns></returns>
    public static double[] FitPolynomial(double[] x, double[] y, double[] polynomial) {
        if (x.length != y.length) {
            throw new RuntimeException("Arrays x and y must be of equal size.");
        }
        int n = polynomial.length - 1; // polynomial degree
        int N = x.length;
        if (n >= N) {
            throw new RuntimeException("There must be at least one more point in the input data than the degree of the polynomial.");
        }

        int i, j, k;

        double[] X = new double[2 * n + 1];    //Array that will store the values of sigma(xi),sigma(xi^2),sigma(xi^3)....sigma(xi^2n)

        for (i = 0; i < 2 * n + 1; i++) {
            X[i] = 0;
            for (j = 0; j < N; j++) {
                X[i] = X[i] + Math.pow(x[j], i);// consecutive positions of the array will store N,sigma(xi),sigma(xi^2),sigma(xi^3)....sigma(xi^2n)
            }
        }
        double[][] B = new double[n + 1][n + 2]; // B is the Normal matrix(augmented) that will store the equations
        double[] a = new double[n + 1];         // 'a' is for value of the final coefficients
        for (i = 0; i <= n; i++) {
            for (j = 0; j <= n; j++) {
                B[i][j] = X[i + j];             //Build the Normal matrix by storing the corresponding coefficients at the right positions except the last column of the matrix
            }
        }
        double[] Y = new double[n + 1];         //Array to store the values of sigma(yi),sigma(xi*yi),sigma(xi^2*yi)...sigma(xi^n*yi)
        for (i = 0; i < n + 1; i++) {
            Y[i] = 0;
            for (j = 0; j < N; j++) {
                Y[i] = Y[i] + Math.pow(x[j], i) * y[j]; //consecutive positions will store sigma(yi),sigma(xi*yi),sigma(xi^2*yi)...sigma(xi^n*yi)
            }
        }
        for (i = 0; i <= n; i++) {
            B[i][n + 1] = Y[i];   //load the values of Y as the last column of B(Normal Matrix but augmented)
        }
        n = n + 1;                //n is made n+1 because the Gaussian Elimination part below was for n equations, but here n is the degree of polynomial and for n degree we get n+1 equations

        for (i = 0; i < n; i++) //From now Gaussian Elimination starts(can be ignored) to solve the set of linear equations (Pivotisation)
        {
            for (k = i + 1; k < n; k++) {
                if (B[i][i] < B[k][i]) {
                    for (j = 0; j <= n; j++) {
                        double temp = B[i][j];
                        B[i][j] = B[k][j];
                        B[k][j] = temp;
                    }
                }
            }
        }

        for (i = 0; i < n - 1; i++) //loop to perform the gauss elimination
        {
            for (k = i + 1; k < n; k++) {
                double t = B[k][i] / B[i][i];
                for (j = 0; j <= n; j++) {
                    B[k][j] = B[k][j] - t * B[i][j]; //make the elements below the pivot elements equal to zero or elimnate the variables
                }
            }
        }
        for (i = n - 1; i >= 0; i--) //back-substitution
        {                                  //x is an array whose values correspond to the values of x,y,z..
            a[i] = B[i][n];                //make the variable to be calculated equal to the rhs of the last equation
            for (j = 0; j < n; j++) {
                if (j != i) //then subtract all the lhs values except the coefficient of the variable whose value is being calculated
                {
                    a[i] = a[i] - B[i][j] * a[j];
                }
            }
            a[i] = a[i] / B[i][i];         //now finally divide the rhs by the coefficient of the variable to be calculated
        }

        for (i = 0; i < n; i++) {
            polynomial[i] = a[i];
        }
        return polynomial;
    }

    /// <summary>
    /// Fit a polynomial using Math.NET Numerics QR decomposition. Slower than using normal equations,but more robust.
    /// </summary>
    /// <param name="x">data points' X coords</param>
    /// <param name="y">data points' Y coords</param>
    /// <param name="order">polynomial degree</param>
    /// <returns></returns>
//        public static double[] Polynomial(double[] x, double[] y, int order)
//        {
//            var design = Matrix<double>.Build.Dense(x.Length, order + 1, (i, j) => Math.Pow(x[i], j)); // Vandermonde matrix
//            return MultipleRegression.QR(design, Vector<double>.Build.Dense(y)).ToArray();
//        }
}
