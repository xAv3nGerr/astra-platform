package pl.v3bc.platform.utils;

import java.text.DecimalFormat;

/**
 * @Author: v3bc_
 * @Date: 8/26/26
 * @Project: astra-platform
 */

public class FormatUtil {
    private static final DecimalFormat df = new DecimalFormat("#.##");

    public static String format(int n) {
        if (n < 1000.0) {
            return String.valueOf(n);
        }
        int n2 = (int)(Math.log(n) / Math.log(1000.0));
        String[] stringArray = new String[]{"", "k", "MLN", "MLD", "B", "BLD", "QD", "QN", "SX", "SP", " OC", "NIL", "DIL", "UND", "DUO", "TRE", "QUA", "QUI", "SXD", "SPD", "OCD", "NVD", "VIG", "UNV", "DVI", "TRV", "QUT", "QUN", "SXV", "SPV", "OCV", "NOG", "TRI", "UTR", "DUT", "TDU", "CET"};
        if (n2 >= stringArray.length) {
            n2 = stringArray.length - 1;
        }
        double d = n / Math.pow(1000.0, n2);
        DecimalFormat decimalFormat = new DecimalFormat("0.##");
        String string = decimalFormat.format(d);
        return string + stringArray[n2];
    }

    public static String format(double d) {
        if (d < 1000.0) {
            return df.format(d);
        }
        int n = (int)(Math.log(d) / Math.log(1000.0));
        String[] stringArray = new String[]{"", "k", "MLN", "MLD", "B", "BLD", "QD", "QN", "SX", "SP", " OC", "NIL", "DIL", "UND", "DUO", "TRE", "QUA", "QUI", "SXD", "SPD", "OCD", "NVD", "VIG", "UNV", "DVI", "TRV", "QUT", "QUN", "SXV", "SPV", "OCV", "NOG", "TRI", "UTR", "DUT", "TDU", "CET"};
        if (n >= stringArray.length) {
            n = stringArray.length - 1;
        }
        double d2 = d / Math.pow(1000.0, n);
        DecimalFormat decimalFormat = new DecimalFormat("0.##");
        String string = decimalFormat.format(d2);
        return string + stringArray[n];
    }
}