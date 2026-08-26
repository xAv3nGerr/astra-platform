package pl.v3bc.platform.utils;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @Author: v3bc_
 * @Date: 8/26/26
 * @Project: astra-platform
 */

public class RandomUtil {
    public static int getRandInt(int n, int n2) {
        if (n == n2) {
            return n;
        }
        if (n2 <= n) {
            throw new IllegalArgumentException("Max can't be smaller than min!");
        }
        return ThreadLocalRandom.current().nextInt(n2 - n + 1) + n;
    }

    public static double getRandDouble(double d, double d2) {
        if (d == d2) {
            return d;
        }
        if (d2 <= d) {
            throw new IllegalArgumentException("Max can't be smaller than min!");
        }
        return ThreadLocalRandom.current().nextDouble() * (d2 - d) + d;
    }

    public static float getRandFloat(float f, float f2) {
        if (f == f2) {
            return f;
        }
        if (f2 <= f) {
            throw new IllegalArgumentException("Max can't be smaller than min!");
        }
        return ThreadLocalRandom.current().nextFloat() * (f2 - f) + f;
    }

    public static boolean getChance(double d) {
        return d >= 100.0 || d >= RandomUtil.getRandDouble(0.0, 100.0);
    }
}
