package pl.v3bc.platform.utils.adventure;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 * @Author: v3bc_
 * @Date: 8/29/26
 * @Project: astra-platform
 */

public class AdventureLegacyColorPostProcessor implements UnaryOperator<Component> {
    private static final TextReplacementConfig LEGACY_REPLACEMENT_CONFIG = TextReplacementConfig.builder().match(Pattern.compile(".*")).replacement((matchResult, build) -> NekoChat.component(matchResult.group())).build();

    @Override
    public Component apply(Component component) {
        return component.replaceText(LEGACY_REPLACEMENT_CONFIG);
    }
}
