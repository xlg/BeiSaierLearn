package com.xlg.util;


import com.xlg.beisaierlearn.Log;

/**
 * Created by xulinggang on 15/6/29.
 */
public class Util {
    private static final String TAG = "Util";
    public static double calculate3X(double a, double b, double c, double d) {
        double A = b*b - 3*a*c;
        double B = b*c - 9*a*d;
        double C = c*c - 3*b*d;
        double D = B * B - 4*A*C;
        Log.i(TAG, "a = " + a + ", b = " + b + ", c = " + c + ", d = " + d);
        Log.i(TAG,"A = "+A+", B = " + b + ", C = "+C +", D = "+D);
//        System.out.println("A ==> "+A);
//        System.out.println("B ==> "+B);
//        System.out.println("C ==> "+C);
//        System.out.println("D ==> "+D);
        double result = 0;
        if (A == 0 && B == 0) {
            //盛金公式1
            Log.i(TAG,"盛金公式1");
            result = -3*d/c;
        } else if (D > 0) {
            //盛金公式2
            Log.i(TAG,"盛金公式2");
            //先计算y1,y2
            double Y1 = A * b + 3 * a * ((-1*B + Math.sqrt(B * B - 4*A*C))/2);
            double Y2 = A * b + 3 * a * ((-1*B - Math.sqrt(B * B - 4*A*C))/2);
            Log.i(TAG, "Y1 ==> " + Y1);
            Log.i(TAG, "Y2 ==> " + Y2);

            double Y1F = 1d;
            double Y2F = 1d;
            if (Y1 < 0) {
                Y1F = -1d;
            }
            if (Y2 < 0) {
                Y2F = -1d;
            }
            double bot = 1d/3;

            result = (-b - (Y1F*Math.pow(Y1*Y1F,bot) + Y2F*Math.pow(Y2*Y2F,bot))) / (3*a);
            System.out.println("X ==> "+result);
        } else if (D == 0) {
            Log.i(TAG,"盛金公式3");
            if (A == 0) {
                Log.i(TAG,"A == 0");
                return result;
            }
            double K = B / A;
            double X1 = -b/a + K;
            double X2 = -K / 2;
            System.out.println("X1 ==>"+X1);
            System.out.println("X2 ==>"+X2);
            double max = Math.max(X1,X2);
            if (max >= 0) {
                result = max;
            } else {
                Log.i(TAG, "Max < 0");
            }
        } else if (D < 0) {
            Log.i(TAG,"盛金公式4");
            if (A <= 0) {
                Log.i(TAG, "A < 0");
                return result;
            }
            double T = (2*A*b - 3*a*B) / (2*Math.sqrt(A*A*A));
            double Z = Math.toDegrees(Math.acos(T));
            Log.i(TAG, "Z ==>"+Z);
            double cosZ_3 = Math.cos(Math.toRadians(Z / 3));
            double sinZ_3 = Math.sin(Math.toRadians(Z / 3));
            double X1 = (-b - 2*Math.sqrt(A)*cosZ_3) / (3*a);
            double X2 = (-b + Math.sqrt(A)*(cosZ_3 + Math.sqrt(3)*sinZ_3)) / (3*a);
            double X3 = (-b + Math.sqrt(A)*(cosZ_3 - Math.sqrt(3)*sinZ_3)) / (3*a);
            Log.i(TAG, "X1 ==>" + X1);
            Log.i(TAG, "X2 ==>" + X2);
            Log.i(TAG, "X3 ==>" + X3);
            //因为t是0-1的，所以
            if (X1 >= 0 && X1 <= 1) {
                result = X1;
            } else if (X2 >= 0 && X2 <= 1) {
                result = X2;
            } else if (X3 >= 0 && X3 <= 1) {
                result = X3;
            }
        }

        return result;

    }
}
