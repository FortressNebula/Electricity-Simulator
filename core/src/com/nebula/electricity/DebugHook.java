package com.nebula.electricity;

import Jama.Matrix;

public class DebugHook {
    static void run () {
        Matrix equations = new Matrix(new double[][]{
                new double[] {2, 2, 1},
                new double[] {-3, -1, -1},
                new double[] {1, 1, 2}
        });

        Matrix solutions = equations.solve(new Matrix(new double[][]{
                new double[] {20},
                new double[] {-18},
                new double[] {16}
        }));

        System.out.println(solutions.get(0,0));
        System.out.println(solutions.get(1,0));
        System.out.println(solutions.get(2,0));
    }
}
