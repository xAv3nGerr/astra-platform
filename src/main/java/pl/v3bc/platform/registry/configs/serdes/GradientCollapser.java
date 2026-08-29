package pl.v3bc.platform.registry.configs.serdes;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GradientCollapser {

    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("^<#([0-9a-fA-F]{6})>([^<]+)");
    private static final int TOLERANCE = 3;
    private static final int MAX_STOPS = 5;

    private GradientCollapser() {
    }

    public static String collapse(String input) {
        if (input == null || !input.contains("<#")) {
            return input;
        }
        StringBuilder result = new StringBuilder();
        int i = 0;
        int n = input.length();
        List<int[]> runColors = new ArrayList<>();
        List<String> runChars = new ArrayList<>();
        while (i < n) {
            if (input.startsWith("<#", i)) {
                Matcher m = HEX_COLOR_PATTERN.matcher(input.substring(i));
                if (m.find()) {
                    String hex = m.group(1);
                    String text = m.group(2);
                    runColors.add(hexToRgb(hex));
                    runChars.add(text);
                    i += m.end();
                    continue;
                }
            }
            flushRun(result, runColors, runChars);
            result.append(input.charAt(i));
            i++;
        }
        flushRun(result, runColors, runChars);
        return result.toString();
    }

    private static void flushRun(StringBuilder result, List<int[]> colors, List<String> chars) {
        if (colors.isEmpty()) {
            return;
        }
        String gradientTag = tryBuildGradientTag(colors, chars);
        if (gradientTag != null) {
            result.append(gradientTag);
        } else {
            for (int i = 0; i < colors.size(); i++) {
                result.append("<#").append(rgbToHex(colors.get(i))).append('>').append(chars.get(i));
            }
        }
        colors.clear();
        chars.clear();
    }

    private static String tryBuildGradientTag(List<int[]> colors, List<String> chars) {
        if (colors.size() < 2) {
            return null;
        }

        int totalLen = 0;
        int[] startPos = new int[colors.size()];
        int[] lengths = new int[colors.size()];

        for (int i = 0; i < colors.size(); i++) {
            lengths[i] = chars.get(i).length();
            startPos[i] = totalLen;
            totalLen += lengths[i];
        }

        if (totalLen == 0) return null;

        List<int[]> stopColors = detectStops(colors, startPos, lengths, totalLen);
        if (stopColors == null) {
            return null;
        }

        StringBuilder tag = new StringBuilder("<gradient");
        for (int[] c : stopColors) {
            tag.append(':').append('#').append(rgbToHex(c));
        }
        tag.append('>');

        for (String c : chars) {
            tag.append(c);
        }
        tag.append("</gradient>");
        return tag.toString();
    }

    private static List<int[]> detectStops(List<int[]> colors, int[] start, int[] visLen, int total) {
        int n = colors.size();
        int cap = Math.min(MAX_STOPS, n);

        for (int k = 2; k <= cap; k++) {
            List<int[]> candidateStops = new ArrayList<>(k);
            for (int m = 0; m < k; m++) {
                int pos = total == 1 ? 0 : Math.round((m * (float) (total - 1)) / (k - 1));
                int run = findRunForPosition(start, visLen, pos);
                candidateStops.add(colors.get(run));
            }
            if (matchesGradient(colors, start, visLen, total, candidateStops)) {
                return candidateStops;
            }
        }
        return null;
    }

    private static int findRunForPosition(int[] start, int[] visLen, int pos) {
        for (int r = 0; r < start.length; r++) {
            int end = start[r] + visLen[r] - 1;
            if (pos >= start[r] && pos <= end) {
                return r;
            }
        }
        return start.length - 1;
    }

    private static boolean matchesGradient(List<int[]> colors, int[] start, int[] visLen, int total, List<int[]> stopColors) {
        int k = stopColors.size();
        for (int r = 0; r < colors.size(); r++) {
            int[] actual = colors.get(r);
            int rangeStart = start[r];
            int rangeEnd = start[r] + visLen[r] - 1;
            for (int pos = rangeStart; pos <= rangeEnd; pos++) {
                float t = total == 1 ? 0 : pos / (float) (total - 1);
                float segFloat = t * (k - 1);
                int seg = Math.min(k - 2, (int) Math.floor(segFloat));
                float localT = segFloat - seg;
                int[] c0 = stopColors.get(seg);
                int[] c1 = stopColors.get(seg + 1);
                for (int ch = 0; ch < 3; ch++) {
                    int expected = Math.round(c0[ch] + (c1[ch] - c0[ch]) * localT);
                    if (Math.abs(expected - actual[ch]) > TOLERANCE) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static int[] hexToRgb(String hex) {
        return new int[]{Integer.parseInt(hex.substring(0, 2), 16), Integer.parseInt(hex.substring(2, 4), 16), Integer.parseInt(hex.substring(4, 6), 16)};
    }

    private static String rgbToHex(int[] rgb) {
        return String.format("%02X%02X%02X", rgb[0], rgb[1], rgb[2]);
    }
}