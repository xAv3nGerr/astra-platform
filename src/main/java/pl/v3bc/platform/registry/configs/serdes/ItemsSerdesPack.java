package pl.v3bc.platform.registry.configs.serdes;

import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import eu.okaeri.configs.serdes.SerdesRegistry;

public class ItemsSerdesPack implements OkaeriSerdesPack {
    @Override
    public void register(SerdesRegistry registry) {
        registry.register(new MiniMessageItemMetaSerializer());
    }
}