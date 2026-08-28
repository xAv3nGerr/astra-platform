package pl.v3bc.platform.registry.configs.serdes;

import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import eu.okaeri.configs.serdes.SerdesRegistry;
import eu.okaeri.configs.yaml.bukkit.serdes.itemstack.ItemStackFailsafe;
import eu.okaeri.configs.yaml.bukkit.serdes.serializer.ItemStackSerializer;

public class ItemsSerdesPack implements OkaeriSerdesPack {
    @Override
    public void register(SerdesRegistry registry) {
        registry.register(new ItemStackSerializer(ItemStackFailsafe.NONE));
        registry.register(new MiniMessageItemMetaSerializer());
    }
}