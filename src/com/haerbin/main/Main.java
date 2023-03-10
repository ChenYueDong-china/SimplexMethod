package com.haerbin.main;

import java.util.ArrayList;
import java.util.Arrays;

public class Main {


    private static int count;
    private static ArrayList<Double> resultX = new ArrayList<>();

    public static void main(String[] args) {

        //10.1 大M法
        double[][] A = {
                {2, -1, 3, 1, 0},
                {1, 2, 4, 0, 1}
        };
        double[] b = {30, 40};
        double[] maxZ = {4, 2, 8, 0,-MMethod.M};
        simplexAlgorithm(maxZ, A, b);

        //10.2 两阶段法
//        double[][] A = {
//                {2, -1, 3, 1, 0},
//                {1, 2, 4, 0, 1}
//        };
//        double[] b = {30, 40};
//        double[] maxZ1 = {0, 0, 0, -1, -1};
//        double[] maxZ2 = {4, 2, 8, 0, 0};
//
//        for (int i = 0; i < 2; i++) {
//            if (i == 1) {
//                //分开一下
//                split();
//
//                for (int j = 0; j < resultX.size()-b.length; j++) {
//                    if(resultX.get(resultX.size()-j-1)!=0){
//                        System.out.println("无最优解");
//                        return;
//                    }
//                }
//                simplexAlgorithm(maxZ2, A, b);
//            } else {
//                simplexAlgorithm(maxZ1, A, b);
//            }
//        }


        //9.2 单纯型法
//        double[][] A = {
//                {4, 5, -2, 1, 0},
//                {1, -2, 1, 0, 1}
//        };
//        double[] b = {22, 30};
//        double[] maxZ = {-3, 2, 4, 0,0};

        // 9.1
//        double[][] A = {
//                {1, 4, 2, 1, 0},
//                {1, 2, 4, 0, 1}
//        };
//        double[] b = {48, 60};
//        double[] maxZ = {6, 14, 13, 0,0};
        //  simplexAlgorithm(maxZ, A, b);
    }

    private static void split() {
        System.out.println();
        System.out.println();
        System.out.println();
        for (int j = 0; j < 50; j++) {
            System.out.print("--");
        }
        System.out.println();
        System.out.println("第二阶段开始");
    }

    /**
     * Ax=b,求maxZ
     *
     * @param maxZ z函数,输入z函数的系数矩阵
     * @param A    A矩阵，标准化，并且为[B E]的形式
     * @param b    B矩阵
     */
    private static int simplexAlgorithm(double[] maxZ, double[][] A, double[] b) {
        if (A.length != b.length) {
            return -2;
        }
        //变量个数
        int numberOfVariables = A[0].length;
        //主元所在位置[0]:y轴位置,[1]:x轴位置
        int[] pivotPosition = new int[2];
        //当前-z的值,初始化默认为0
        double currentNegativeZValue = 0;
        //c_B数组
        double[] c_B = new double[A.length];
        //x_B数组,存当前基变量的位置
        int[] x_B = new int[A.length];
        //theta数组
        double[] theta = new double[A.length];
        //检验数，sigma数组
        double[] sigma = new double[numberOfVariables];

        //x_B初始化，默认当前基变量为最后几个变量
        for (int i = x_B.length - 1; i >= 0; i--) {
            x_B[i] = numberOfVariables - x_B.length + i;
        }

        //c_B初始化
        for (int i = 0; i < c_B.length; i++) {
            c_B[i] = maxZ[x_B[i]];
        }

        //-z初始化
        for (int i = 0; i < b.length; i++) {
            currentNegativeZValue -= b[i] * c_B[i];
        }

        //检验数sigma初始化，用maxZ减去所有的c_B*A
        for (int i = 0; i < sigma.length; i++) {
            sigma[i] = maxZ[i];
            for (int j = 0; j < A.length; j++) {
                sigma[i] -= c_B[j] * A[j][i];
            }
        }
        //debugPrint(A, b, c_B, x_B, theta, currentNegativeZValue, sigma);

        for (int i = 0; i < 1000; i++) {
            //是否获得最优解判断
            if (allLeqZero(sigma)) {
                debugPrint(A, b, c_B, x_B, theta, currentNegativeZValue, sigma);
                System.out.println("获得最优解");
                return 1;
            }
            //求主元的x轴位置
            pivotPosition[1] = Utils.max(sigma);

            //有限最优解判断，全部小于等于0，无有限最优解
            if (isLimited(A, pivotPosition)) {
                System.out.println("无有限最优解");
                return -1;
            }

            //更新theta,此时一定有一个theta[i]大于0
            for (int j = 0; j < theta.length; j++) {
                //如果此时a[i][j]<=0，更新为一个很大的数
                if (A[j][pivotPosition[1]] <= 0) {
                    theta[j] = MMethod.M;
                    continue;
                }
                theta[j] = b[j] / A[j][pivotPosition[1]];
            }

            debugPrint(A, b, c_B, x_B, theta, currentNegativeZValue, sigma);

            //求主元y轴位置
            pivotPosition[0] = Utils.min(theta);

            //更新Ab矩阵
            updateAb(A, b, pivotPosition);

            //更新x_B
            x_B[pivotPosition[0]] = pivotPosition[1];

            //更新c_B
            c_B[pivotPosition[0]] = maxZ[pivotPosition[1]];

            //更新-z
            currentNegativeZValue -= sigma[pivotPosition[1]] * b[pivotPosition[0]];

            //更新sigma,此时无需再减去所有的，减去入基那一行即可。直接迭代，与书上不同
            updateSigma(A, pivotPosition, sigma);

        }

        return 0;
    }

    /**
     * 更新sigma
     *
     * @param A
     * @param pivotPosition
     * @param sigma
     */
    private static void updateSigma(double[][] A, int[] pivotPosition, double[] sigma) {
        double[] tmp = Arrays.copyOf(sigma, sigma.length);
        for (int j = 0; j < sigma.length; j++) {
            sigma[j] -= tmp[pivotPosition[1]] * A[pivotPosition[0]][j];
        }
    }

    /**
     * 打印一下，调试
     *
     * @param A
     * @param b
     * @param c_b
     * @param x_b
     * @param theta
     * @param currentNegativeZValue
     * @param sigma
     */
    private static void debugPrint(double[][] A, double[] b, double[] c_b, int[] x_b, double[] theta, double currentNegativeZValue, double[] sigma) {

        //消除误差
        Utils.eliminateErrorTwo(A);
        Utils.eliminateErrorOne(b);
        Utils.eliminateErrorOne(c_b);
        Utils.eliminateErrorOne(theta);
        Utils.eliminateErrorOne(sigma);
        currentNegativeZValue = Utils.eliminateError(currentNegativeZValue);

        System.out.println("迭代次数：" + (++count));
        Utils.printTwoMatrix("A", A);
        Utils.printOneMatrix("b", b);
        Utils.printOneMatrix("c_b", c_b);
        Utils.printOneMatrixInt("x_b", x_b);
        Utils.printOneMatrix("theta", theta);
        Utils.printOneMatrix("sigma", sigma);

        //求一下当前的x
        double[] tmp = new double[sigma.length];
        for (int i = 0; i < tmp.length; i++) {
            for (int j = 0; j < b.length; j++) {
                if (x_b[j] == i) {
                    tmp[i] = b[j];
                    break;
                }
            }
        }
        resultX.clear();
        for (int i = 0; i < tmp.length; i++) {
            resultX.add(tmp[i]);
        }
        Utils.printOneMatrix("x", tmp);
        System.out.println("currentNegativeZValue = " + currentNegativeZValue);
        for (int i = 0; i < 100; i++) {
            System.out.print("-");
        }
        System.out.println();

    }


    /**
     * 判断是否有无限个最优解
     *
     * @param A
     * @param pivotPosition
     * @return
     */
    private static boolean isLimited(double[][] A, int[] pivotPosition) {
        for (int i = 0; i < A.length; i++) {
            if (A[i][pivotPosition[1]] > 0) {
                return false;
            }
        }
        return true;
    }


    /**
     * 更新A矩阵
     *
     * @param A             A矩阵
     * @param b             b矩阵
     * @param pivotPosition 主元素位置
     */
    private static void updateAb(double[][] A, double[] b, int[] pivotPosition) {
        double ark = A[pivotPosition[0]][pivotPosition[1]];

        // 临时存储A的r（即A主元所在的行）,和b主元所在的行
        double tmp = b[pivotPosition[0]];
        double[] tmp1 = new double[A[0].length];
        for (int j = 0; j < tmp1.length; j++) {
            tmp1[j] = A[pivotPosition[0]][j];
        }

        for (int i = 0; i < A.length; i++) {
            double aik = A[i][pivotPosition[1]];
            for (int j = 0; j < tmp1.length; j++) {
                tmp1[j] *= (aik / ark);
            }

            if (i == pivotPosition[0]) {
                for (int j = 0; j < A[0].length; j++) {
                    A[i][j] *= 1 / ark;
                }
                b[i] *= 1 / ark;
            } else {
                for (int j = 0; j < A[0].length; j++) {
                    A[i][j] -= tmp1[j];
                }
                b[i] -= tmp * aik / ark;
            }
        }
    }

    /**
     * 判断检验数是否全部小于等于0
     *
     * @param sigma 检验数
     * @return 是否全部小于等于0
     */
    private static boolean allLeqZero(double[] sigma) {
        for (int i = 0; i < sigma.length; i++) {
            if (sigma[i] > 0) {
                return false;
            }
        }
        return true;
    }


}