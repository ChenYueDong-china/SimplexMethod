package com.haerbin.main;

import java.math.BigDecimal;

public class Utils {


    /**
     * 打印二维矩阵
     *
     * @param matrixName 矩阵名
     * @param matrix     矩阵，二维数组
     */
    public static void printTwoMatrix(String matrixName, double[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            //打印前面的东西
            if (i == (matrix.length - 1) / 2) {
                System.out.print(matrixName + " = ");
            } else {
                for (int j = 0; j < matrixName.length() + 3; j++) {
                    System.out.print(" ");
                }
            }

            //打印矩阵
            System.out.print("| ");
            for (int j = 0; j < matrix[0].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println("|");
        }
    }

    /**
     * 打印一维矩阵
     *
     * @param matrixName 矩阵名
     * @param matrix     矩阵，二维数组
     */
    public static void printOneMatrix(String matrixName, double[] matrix) {
        //打印前面的东西
        System.out.print(matrixName + " = ");
        System.out.print("| ");
        for (int i = 0; i < matrix.length; i++) {
            System.out.print(matrix[i] + " ");
        }
        System.out.println("|");
    }

    /**
     * 打印一维矩阵
     *
     * @param matrixName 矩阵名
     * @param matrix     矩阵，二维数组
     */
    public static void printOneMatrixInt(String matrixName, int[] matrix) {
        //打印前面的东西
        System.out.print(matrixName + " = ");
        System.out.print("| ");
        for (int i = 0; i < matrix.length; i++) {
            System.out.print(matrix[i] + " ");
        }
        System.out.println("|");
    }

    /**
     * 最小值所在位置
     *
     * @param matrix
     * @return
     */
    public static int max(double[] matrix) {
        int maxPosition = 0;
        double maxValue = matrix[0];
        for (int i = 1; i < matrix.length; i++) {
            if (matrix[i] > maxValue) {
                maxPosition = i;
                maxValue = matrix[i];
            }
        }
        return maxPosition;
    }

    /**
     * 最大值所在位置
     *
     * @param matrix
     * @return
     */
    public static int min(double[] matrix) {
        int minPosition = 0;
        double minValue = matrix[0];
        for (int i = 1; i < matrix.length; i++) {
            if (matrix[i] < minValue) {
                minPosition = i;
                minValue = matrix[i];
            }
        }
        return minPosition;
    }

    /**
     * 消除误差
     *
     * @param a
     */
    public static void eliminateErrorTwo(double[][] a) {
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                a[i][j] = eliminateErrorCore(a[i][j]);
            }
        }
    }

    /**
     * 字符串法
     *
     * @param value 值
     */
    private static Double stringMethod(double value) {
        int con = 0;
        if (value < 0) {
            con = 1;
        }
        String s = String.valueOf(value);
        //不需要消除误差，长度小于8或者倒数第二位不是0或9，防止意外
        char c = s.charAt(s.length() - 2);
        boolean bool = c == '0' || c == '9';
        if (s.length() <= 8 || !bool) {
            return value;
        }
        StringBuilder result = new StringBuilder();
        int z = s.length() - 2; //倒数第二位开始
        boolean is9 = false;
        for (; z > 0; z--) {
            if (s.charAt(z) == '0' && s.charAt(z - 1) != '0') {
                break;
            }
            if (s.charAt(z) == '9' && s.charAt(z - 1) != '9') {
                is9 = true;
                break;
            }
        }
        result.append(s.toCharArray(), 0, z + 1);
        if (is9) {
            BigDecimal bigDecimal = new BigDecimal("1");
            if (con == 1) {
                bigDecimal = new BigDecimal("-1");
            }
            for (int i = 0; i < z - 1 - con; i++) {
                bigDecimal = bigDecimal.multiply(new BigDecimal("0.1"));
            }
            bigDecimal = bigDecimal.add(new BigDecimal(result.toString()));
            return Double.valueOf(bigDecimal.toString());
        } else {
            return Double.valueOf(result.toString());
        }
    }

    public static void eliminateErrorOne(double[] b) {
        for (int i = 0; i < b.length; i++) {
            b[i] = eliminateErrorCore(b[i]);
        }
    }

    public static double eliminateError(double currentNegativeZValue) {
        return eliminateErrorCore(currentNegativeZValue);
    }

    private static double eliminateErrorCore(double value) {
        //绝对值大于1，直接消除
        if (Math.abs(value) > 1 && Math.abs(Math.round(value) - value) < 1e-5) {
            value = Math.round(value);
            return value;
        }

        //小于1，字符串法
        return stringMethod(value);
    }
}
