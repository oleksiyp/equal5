package util;

import java.util.Arrays;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/28/13
 * Time: 12:17 AM
 */
public class VectorUtils {
    public static double[][] copy(double[][] input) {
        double [][]ret = new double[input.length][];
        for (int i = 0; i < input.length; i++) {
            double[] values = input[i];
            ret[i] = Arrays.copyOf(values, values.length);
        }
        return ret;
    }
}
