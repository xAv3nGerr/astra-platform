package pl.v3bc.platform.registry.configs;

import eu.okaeri.configs.configurer.Configurer;
import eu.okaeri.configs.format.yaml.YamlSourceWalker;
import eu.okaeri.configs.postprocessor.ConfigPostprocessor;
import eu.okaeri.configs.schema.ConfigDeclaration;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Accessors(chain = true)
public class DoubleQuotedYamlConfigurer extends Configurer {

    @Setter private String commentPrefix = "# ";
    private final Yaml yaml = buildYaml();

    @Override
    public List<String> getExtensions() {
        return Arrays.asList("yml", "yaml");
    }

    @Override
    public boolean isCommentLine(String line) {
        return line.trim().startsWith("#");
    }

    @Override
    public Map<String, Object> load(@NonNull InputStream inputStream, @NonNull ConfigDeclaration declaration) throws Exception {
        String content = ConfigPostprocessor.of(inputStream).getContext();
        Object loaded = this.yaml.load(content);
        if (loaded == null) {
            return new LinkedHashMap<>();
        }
        return (Map<String, Object>) loaded;
    }

    @Override
    public void write(@NonNull OutputStream outputStream, @NonNull Map<String, Object> data, @NonNull ConfigDeclaration declaration) throws Exception {
        Object wrapped = wrapValues(data);
        String dump = this.yaml.dump(wrapped);
        ConfigPostprocessor.of(dump)
                .removeLines(line -> line.startsWith(this.commentPrefix.trim()))
                .removeLinesUntil(line -> line.chars().anyMatch(x -> !Character.isWhitespace(x)))
                .updateContext(ctx -> YamlSourceWalker.of(ctx).insertComments(declaration, this.commentPrefix))
                .write(outputStream);
    }


    private static Object wrapValues(Object value) {
        if (value instanceof String) {
            return new QuotedString((String) value);
        }
        if (value instanceof Map<?, ?> map) {
            Map<Object, Object> result = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                result.put(entry.getKey(), wrapValues(entry.getValue()));
            }
            return result;
        }
        if (value instanceof List<?> list) {
            List<Object> result = new ArrayList<>(list.size());
            for (Object item : list) {
                result.add(wrapValues(item));
            }
            return result;
        }
        return value;
    }

    private record QuotedString(String value) {
    }

    private static Yaml buildYaml() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setIndent(2);
        options.setAllowUnicode(true);
        options.setWidth(Integer.MAX_VALUE);

        Representer representer = new Representer(options) {
            {
                this.representers.put(QuotedString.class, data ->
                        representScalar(Tag.STR, ((QuotedString) data).value(), DumperOptions.ScalarStyle.DOUBLE_QUOTED));
            }
        };
        representer.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        return new Yaml(representer, options);
    }
}