package pl.v3bc.platform.registry.configs.serdes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuoteEnforcer {


    private static final Pattern SCALAR_LINE = Pattern.compile(
            "^(\\s*(?:-\\s+|[\\w.\\-]+:\\s+))(.+?)\\s*$"
    );

    public static void enforceDoubleQuotes(Path yamlFile) throws IOException {
        List<String> lines = Files.readAllLines(yamlFile, StandardCharsets.UTF_8);
        List<String> out = new ArrayList<>();

        for (String line : lines) {
            String trimmed = line.stripLeading();
            if (trimmed.startsWith("#") || trimmed.isEmpty() || trimmed.endsWith(":")) {
                out.add(line);
                continue;
            }
            Matcher m = SCALAR_LINE.matcher(line);
            if (m.matches()) {
                String prefix = m.group(1);
                String value = m.group(2);
                out.add(prefix + toDoubleQuoted(value));
            } else {
                out.add(line);
            }
        }
        Files.write(yamlFile, out, StandardCharsets.UTF_8);
    }

    private static String toDoubleQuoted(String value) {
        String raw = unquote(value);
        String escaped = raw.replace("\\", "\\\\").replace("\"", "\\\"");
        return "\"" + escaped + "\"";
    }

    private static String unquote(String value) {
        if (value.length() >= 2 && value.startsWith("'") && value.endsWith("'")) {
            return value.substring(1, value.length() - 1).replace("''", "'");
        }
        if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }
}