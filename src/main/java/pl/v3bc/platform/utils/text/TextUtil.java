package pl.v3bc.platform.utils.text;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public final class TextUtil {

    public static final TextComponent RESET = Component.text().decoration(TextDecoration.ITALIC, false).build();
    public static final LegacyComponentSerializer LEGACY_COMPONENT_SERIALIZER = LegacyComponentSerializer.builder().character('&').hexColors().build();
    public static final MiniMessage MINI_MESSAGE;
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final TextReplacementConfig LEGACY_REPLACEMENT_CONFIG;

    static {
        LEGACY_REPLACEMENT_CONFIG = TextReplacementConfig.builder()
                .match(Pattern.compile(".*"))
                .replacement((matchResult, build) -> LEGACY_COMPONENT_SERIALIZER.deserialize(matchResult.group()))
                .build();

        MINI_MESSAGE = MiniMessage.builder()
                .postProcessor(component -> component.replaceText(LEGACY_REPLACEMENT_CONFIG))
                .build();
    }


    public static Component parse(final String text) {
        if (text == null || text.isEmpty()) {
            return Component.empty();
        }
        return RESET.append(MINI_MESSAGE.deserialize(text));
    }

    public static Component parse(String text, Map<String, String> placeholders) {
        if (text == null || text.isEmpty()) {
            return Component.empty();
        }
        if (placeholders != null && !placeholders.isEmpty()) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                text = text.replace("{" + entry.getKey() + "}", entry.getValue())
                        .replace("%" + entry.getKey() + "%", entry.getValue());
            }
        }
        return parse(text);
    }

    public static List<Component> parse(final List<String> text) {
        if (text == null || text.isEmpty()) {
            return List.of();
        }
        final List<Component> list = new ArrayList<>();
        text.forEach(it -> list.add(parse(it)));
        return list;
    }

    public static List<Component> parse(final List<String> text, Map<String, String> placeholders) {
        if (text == null || text.isEmpty()) {
            return List.of();
        }
        final List<Component> list = new ArrayList<>();
        text.forEach(it -> list.add(parse(it, placeholders)));
        return list;
    }

    public static String serialize(final Component component) {
        if (component == null) {
            return "";
        }
        return LEGACY_COMPONENT_SERIALIZER.serialize(component);
    }

    public static String legacyColor(final String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        final char colorChar = '§';
        final Matcher matcher = HEX_PATTERN.matcher(text);
        final StringBuilder buffer = new StringBuilder(text.length() + 32);
        while (matcher.find()) {
            final String group = matcher.group(1);
            matcher.appendReplacement(buffer, colorChar + "x" + colorChar + colorChar + group.charAt(0) + colorChar + group.charAt(1) + colorChar + group.charAt(2) + colorChar + group.charAt(3) + colorChar + group.charAt(4) + colorChar + group.charAt(5));
        }
        return color(matcher.appendTail(buffer).toString());
    }

    public static List<String> legacyColor(final List<String> text) {
        if (text == null || text.isEmpty()) {
            return List.of();
        }
        final List<String> colored = new ArrayList<>();
        text.forEach(it -> colored.add(legacyColor(it)));
        return colored;
    }

    public static String color(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        return ChatColor.translateAlternateColorCodes('&', text.replace(">>", "»").replace("<<", "«"));
    }

    public static List<String> color(List<String> text) {
        if (text == null || text.isEmpty()) {
            return List.of();
        }
        List<String> colored = new ArrayList<>();
        text.forEach(it -> colored.add(color(it)));
        return colored;
    }

    public static String color(String text, Map<String, String> placeholders) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        if (placeholders != null && !placeholders.isEmpty()) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                text = text.replace("{" + entry.getKey() + "}", entry.getValue())
                        .replace("%" + entry.getKey() + "%", entry.getValue());
            }
        }
        return color(text);
    }

    public static List<String> color(List<String> text, Map<String, String> placeholders) {
        if (text == null || text.isEmpty()) {
            return List.of();
        }
        List<String> colored = new ArrayList<>();
        text.forEach(it -> colored.add(color(it, placeholders)));
        return colored;
    }

    public static String tpsWithFormat(final double tps) {
        return (tps > 20.0D ? "&a*" : tps > 18.0D ? "&e*" : "&c*");
    }
}