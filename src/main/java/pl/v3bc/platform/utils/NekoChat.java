package pl.v3bc.platform.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

/**
 * @Author: v3bc_
 * @Date: 8/22/26
 * @Project: astra-platform
 */

public final class NekoChat {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.builder()
            .postProcessor(new AdventureLegacyColorPostProcessor())
            .preProcessor(new AdventureLegacyColorPreProcessor())
            .build();

    private static final LegacyComponentSerializer AMPERSAND_SERIALIZER = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();

    private NekoChat() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static Component translate(String text) {
        return MINI_MESSAGE.deserialize(text).decoration(TextDecoration.ITALIC, false);
    }

    public static List<Component> translate(List<String> lines) {
        if (lines == null) return new ArrayList<>();
        List<Component> components = new ArrayList<>();
        for (String line : lines) {
            components.add(translate(line));
        }
        return components;
    }

    public static Component component(String text) {
        return AMPERSAND_SERIALIZER.deserialize(text);
    }

    public static MiniMessage miniMessage() {
        return MINI_MESSAGE;
    }

    public static class AdventureLegacyColorPostProcessor implements UnaryOperator<Component> {
        private static final TextReplacementConfig LEGACY_REPLACEMENT_CONFIG = TextReplacementConfig.builder()
                .match(Pattern.compile(".*"))
                .replacement((matchResult, build) -> NekoChat.component(matchResult.group()))
                .build();

        @Override
        public Component apply(Component component) {
            return component.replaceText(LEGACY_REPLACEMENT_CONFIG);
        }
    }

    public static class AdventureLegacyColorPreProcessor implements UnaryOperator<String> {
        @Override
        public String apply(String component) {
            return component.replace("\u00a7", "&");
        }
    }
}
