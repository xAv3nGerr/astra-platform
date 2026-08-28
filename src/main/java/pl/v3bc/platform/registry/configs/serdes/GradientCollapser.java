package pl.v3bc.platform.registry.configs.serdes;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class GradientCollapser {

    private static final Pattern OPEN_TAG = Pattern.compile("<#([0-9a-fA-F]{6})>");
    private static final int TOLERANCE = 2;
    private static final int MAX_STOPS = 5;

    private GradientCollapser() {
    }

    public static String collapse(String input) {
        StringBuilder result = new StringBuilder();
        int n = input.length();
        int i = 0;

        List<int[]> runColors = new ArrayList<>();
        List<String> runChars = new ArrayList<>();

        while (i < n) {
            Matcher m = OPEN_TAG.matcher(input);
            m.region(i, n);

            if (!m.lookingAt()) {
                flushRun(result, runColors, runChars);
                int nextOpen = findNextOpenTagStart(input, i);
                if (nextOpen == -1) {
                    result.append(input, i, n);
                    i = n;
                } else {
                    result.append(input, i, nextOpen);
                    i = nextOpen;
                }
                continue;
            }

            String hex = m.group(1);
            int contentStart = m.end();
            String closeTag = "</#" + hex + ">";

            Boundary boundary = scanContent(input, contentStart, closeTag);
            runColors.add(hexToRgb(hex));
            runChars.add(boundary.content());
            i = boundary.nextIndex();
        }

        flushRun(result, runColors, runChars);
        return result.toString();
    }

    private static int findNextOpenTagStart(String input, int from) {
        Matcher m = OPEN_TAG.matcher(input);
        return m.find(from) ? m.start() : -1;
    }

    private record Boundary(String content, int nextIndex) {
    }

    private static Boundary scanContent(String input, int contentStart, String closeTag) {
        int n = input.length();
        int i = contentStart;
        StringBuilder content = new StringBuilder();

        while (i < n) {
            if (input.startsWith(closeTag, i)) {
                return new Boundary(content.toString(), i + closeTag.length());
            }
            char c = input.charAt(i);
            if (c == '\\' && i + 1 < n) {
                content.append(c).append(input.charAt(i + 1));
                i += 2;
                continue;
            }
            if (c == '<') {
                return new Boundary(content.toString(), i);
            }
            content.append(c);
            i++;
        }
        return new Boundary(content.toString(), n);
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
                String hex = rgbToHex(colors.get(i));
                result.append("<#").append(hex).append('>')
                        .append(chars.get(i))
                        .append("</#").append(hex).append('>');
            }
        }
        colors.clear();
        chars.clear();
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
            float t = n == 1 ? 0 : i / (float) (n - 1);
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