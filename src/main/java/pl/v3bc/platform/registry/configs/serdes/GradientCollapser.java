package pl.v3bc.platform.registry.configs.serdes;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Odtwarza <gradient:...> z rozbitego przez Adventure/MiniMessage
 * ciągu per-znakowych tagów koloru (<#RRGGBB>x</#RRGGBB>...).
 *
 * Działa tylko na ciągłych sekwencjach takich tagów - zwykły tekst
 * pomiędzy nimi jest przepisywany bez zmian.
 */
public final class GradientCollapser {

    private static final Pattern CHAR_COLOR_TAG =
            Pattern.compile("<#([0-9a-fA-F]{6})>(.*?)</#\\1>", Pattern.DOTALL);

    private static final int TOLERANCE = 2;
    private static final int MAX_STOPS = 5;

    private GradientCollapser() {
    }

    public static String collapse(String input) {
        Matcher matcher = CHAR_COLOR_TAG.matcher(input);

        StringBuilder result = new StringBuilder();
        int lastEnd = 0;

        int runStart = -1;
        int runEnd = -1;
        List<int[]> colors = new ArrayList<>();
        List<String> chars = new ArrayList<>();

        while (matcher.find()) {
            if (runStart != -1 && matcher.start() != runEnd) {
                flush(result, input, lastEnd, runStart, colors, chars);
                lastEnd = runEnd;
                colors.clear();
                chars.clear();
                runStart = -1;
            }

            if (runStart == -1) {
                runStart = matcher.start();
            }

            runEnd = matcher.end();
            colors.add(hexToRgb(matcher.group(1)));
            chars.add(matcher.group(2));
        }

        if (runStart != -1) {
            flush(result, input, lastEnd, runStart, colors, chars);
            lastEnd = runEnd;
        }

        result.append(input, lastEnd, input.length());
        return result.toString();
    }

    private static void flush(StringBuilder result, String input, int from, int runStart,
                              List<int[]> colors, List<String> chars) {
        result.append(input, from, runStart);

        String gradientTag = tryBuildGradientTag(colors, chars);
        if (gradientTag != null) {
            result.append(gradientTag);
            return;
        }

        // fallback - nie udało się dopasować modelu gradientu, zostaw jak było
        for (int i = 0; i < colors.size(); i++) {
            String hex = rgbToHex(colors.get(i));
            result.append("<#").append(hex).append('>')
                    .append(chars.get(i))
                    .append("</#").append(hex).append('>');
        }
    }

    private static String tryBuildGradientTag(List<int[]> colors, List<String> chars) {
        if (colors.size() < 2) {
            return null;
        }

        List<Integer> stops = detectStops(colors);
        if (stops == null) {
            return null;
        }

        StringBuilder tag = new StringBuilder("<gradient");
        for (int idx : stops) {
            tag.append(':').append('#').append(rgbToHex(colors.get(idx)));
        }
        tag.append('>');

        for (String c : chars) {
            tag.append(c);
        }
        tag.append("</gradient>");
        return tag.toString();
    }

    private static List<Integer> detectStops(List<int[]> colors) {
        int n = colors.size();
        if (n < 2) {
            return null;
        }

        int cap = Math.min(MAX_STOPS, n);
        for (int k = 2; k <= cap; k++) {
            List<Integer> stopIndices = new ArrayList<>(k);
            for (int m = 0; m < k; m++) {
                int idx = Math.round((m * (n - 1)) / (float) (k - 1));
                stopIndices.add(idx);
            }

            if (matchesGradient(colors, stopIndices)) {
                return stopIndices;
            }
        }
        return null;
    }

    private static boolean matchesGradient(List<int[]> colors, List<Integer> stopIndices) {
        int n = colors.size();
        int k = stopIndices.size();

        for (int i = 0; i < n; i++) {
            float t = i / (float) (n - 1);
            float segFloat = t * (k - 1);
            int seg = Math.min(k - 2, (int) Math.floor(segFloat));
            float localT = segFloat - seg;

            int[] c0 = colors.get(stopIndices.get(seg));
            int[] c1 = colors.get(stopIndices.get(seg + 1));

            for (int ch = 0; ch < 3; ch++) {
                int expected = Math.round(c0[ch] + (c1[ch] - c0[ch]) * localT);
                int actual = colors.get(i)[ch];
                if (Math.abs(expected - actual) > TOLERANCE) {
                    return false;
                }
            }
        }
        return true;
    }

    private static int[] hexToRgb(String hex) {
        return new int[]{
                Integer.parseInt(hex.substring(0, 2), 16),
                Integer.parseInt(hex.substring(2, 4), 16),
                Integer.parseInt(hex.substring(4, 6), 16)
        };
    }

    private static String rgbToHex(int[] rgb) {
        return String.format("%02X%02X%02X", rgb[0], rgb[1], rgb[2]);
    }
}