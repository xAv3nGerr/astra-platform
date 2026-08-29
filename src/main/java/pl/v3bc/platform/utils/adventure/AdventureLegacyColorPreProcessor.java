package pl.v3bc.platform.utils.adventure;

import java.util.function.UnaryOperator;

/**
 * @Author: v3bc_
 * @Date: 8/29/26
 * @Project: astra-platform
 */

public class AdventureLegacyColorPreProcessor implements UnaryOperator<String> {
    @Override
    public String apply(String component) {
        return component.replace("§", "&");
    }
}
