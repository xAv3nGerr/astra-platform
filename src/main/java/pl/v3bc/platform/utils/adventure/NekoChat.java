package pl.v3bc.platform.utils.adventure;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @Author: v3bc_
 * @Date: 8/29/26
 * @Project: astra-platform
 */

public class NekoChat {
    private static final MiniMessage MINI_MESSAGE = MiniMessage.builder().postProcessor(new AdventureLegacyColorPostProcessor()).preProcessor(new AdventureLegacyColorPreProcessor()).build();
    private static final LegacyComponentSerializer AMPERSAND_SERIALIZER = LegacyComponentSerializer.builder().character('&').hexColors().useUnusualXRepeatedCharacterHexFormat().build();
    private static final LegacyComponentSerializer SECTION_SERIALIZER = LegacyComponentSerializer.builder().character('§').hexCharacter('#').hexColors().extractUrls().useUnusualXRepeatedCharacterHexFormat().build();

    private NekoChat() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static Component translate(String text) {
        return translate(text, null);
    }

    public static Component translate(String text, Map<String, String> placeholders) {
        if (text == null) {
            return Component.empty();
        }
        String parsed = text;
        if (placeholders != null && !placeholders.isEmpty()) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                parsed = parsed.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }
        return MINI_MESSAGE.deserialize(parsed).decoration(TextDecoration.ITALIC, false);
    }

    public static List<Component> translate(List<String> lines) {
        return NekoChat.translate(lines, null);
    }

    public static List<Component> translate(List<String> lines, Map<String, String> placeholders) {
        if (lines == null) {
            return new ArrayList<>();
        }
        List<Component> components = new ArrayList<>();
        for (String line : lines) {
            components.add(translate(line, placeholders));
        }
        return components;
    }

    public static List<Component> translate(Map<String, String> placeholders, String... lines) {
        return Stream.of(lines).map(line -> translate(line, placeholders)).toList();
    }

    public static String legacy(String string) {
        return SECTION_SERIALIZER.serialize(AMPERSAND_SERIALIZER.deserialize(string));
    }

    public static Component component(String text) {
        return AMPERSAND_SERIALIZER.deserialize(text);
    }


    public static MiniMessage miniMessage() {
        return MINI_MESSAGE;
    }

    public static String tpsWithFormat(double tps) {
        return (tps > 20.0F ? "*" : "") + Math.min(Math.round(tps * 100.0F) / 100.0F, 20.0F);
    }
}
