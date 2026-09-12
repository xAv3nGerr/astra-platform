package pl.v3bc.platform.utils;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: v3bc_
 * @Date: 9/12/26
 * @Project: astra-platform
 */

@UtilityClass
public final class ChatUtil {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final LegacyComponentSerializer LEGACY_AMPERSAND = LegacyComponentSerializer.builder().character('&').hexColors().useUnusualXRepeatedCharacterHexFormat().build();
    private static final MiniMessage MINI = MiniMessage.builder().preProcessor(text -> text.replace('§', '&')).postProcessor(component -> component.decoration(TextDecoration.ITALIC, false)).build();

    public static String color(@NonNull String text, Map<String, ?> placeholders) {
        String processed = text;
        if (placeholders != null && !placeholders.isEmpty()) {
            for (Map.Entry<String, ?> entry : placeholders.entrySet()) {
                processed = processed.replace("{" + entry.getKey() + "}", String.valueOf(entry.getValue()));
            }
        }
        return LEGACY_AMPERSAND.serialize(component(processed));
    }

    public static String color(@NonNull String text) {
        return color(text, Collections.emptyMap());
    }

    public static String toLegacy(Component component) {
        if (component == null) {
            return "";
        }
        return LEGACY_AMPERSAND.serialize(component);
    }

    public static Component component(@NonNull String text, Map<String, ?> placeholders) {
        String processed = text;
        if (placeholders != null && !placeholders.isEmpty()) {
            for (Map.Entry<String, ?> entry : placeholders.entrySet()) {
                processed = processed.replace("{" + entry.getKey() + "}", String.valueOf(entry.getValue()));
            }
        }
        String converted = processed.replace("&0", "<black>").replace("&1", "<dark_blue>").replace("&2", "<dark_green>").replace("&3", "<dark_aqua>").replace("&4", "<dark_red>").replace("&5", "<dark_purple>").replace("&6", "<gold>").replace("&7", "<gray>").replace("&8", "<dark_gray>").replace("&9", "<blue>").replace("&a", "<green>").replace("&b", "<aqua>").replace("&c", "<red>").replace("&d", "<light_purple>").replace("&e", "<yellow>").replace("&f", "<white>").replace("&l", "<bold>").replace("&m", "<strikethrough>").replace("&n", "<underlined>").replace("&o", "<italic>").replace("&r", "<reset>");
        return MINI.deserialize(converted, ChatUtil.buildResolver(placeholders)).decoration(TextDecoration.ITALIC, false);
    }
    public static Component component(@NonNull String text) {
        return component(text, Collections.emptyMap());
    }

    public static List<Component> component(@NonNull List<String> lines, Map<String, ?> placeholders) {
        return lines.stream().map(line -> component(line, placeholders)).collect(Collectors.toList());
    }

    public static String buildDate(long time) {
        return Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault()).toLocalDateTime().format(DATE_FORMATTER);
    }

    private static TagResolver buildResolver(Map<String, ?> placeholders) {
        if (placeholders == null || placeholders.isEmpty()) {
            return TagResolver.empty();
        }
        TagResolver.Builder builder = TagResolver.builder();
        placeholders.forEach((key, value) -> {
            if (value instanceof Component component) {
                builder.resolver(Placeholder.component(key, component));
            } else {
                builder.resolver(Placeholder.parsed(key, String.valueOf(value)));
            }
        });
        return builder.build();
    }

    public static String formatTps(double tps) {
        return (tps > 20.0 ? "*" : "") + Math.min((float) Math.round(tps * 100.0) / 100.0f, 20.0f);
    }
}