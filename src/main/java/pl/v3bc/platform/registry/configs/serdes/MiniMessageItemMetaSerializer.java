package pl.v3bc.platform.registry.configs.serdes;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MiniMessageItemMetaSerializer implements ObjectSerializer<ItemMeta> {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    @Override
    public boolean supports(Class<?> type) {
        return ItemMeta.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(ItemMeta itemMeta, SerializationData data, GenericsDeclaration generics) {
        if (itemMeta.hasDisplayName()) {
            Component name = itemMeta.displayName();
            if (name != null) {
                data.set("display", clean(MM.serialize(name)));
            }
        }

        if (itemMeta.hasLore()) {
            List<Component> lore = itemMeta.lore();
            if (lore != null) {
                data.setCollection("lore", lore.stream()
                        .map(line -> clean(MM.serialize(line)))
                        .toList(), String.class);
            }
        }

        if (!itemMeta.getEnchants().isEmpty()) {
            data.setMap("enchantments", itemMeta.getEnchants(), Enchantment.class, Integer.class);
        }

        if (!itemMeta.getItemFlags().isEmpty()) {
            data.setCollection("flags", itemMeta.getItemFlags(), ItemFlag.class);
        }

        if (itemMeta.hasCustomModelData()) {
            data.set("custom-model-data", itemMeta.getCustomModelData());
        }
    }

    @Override
    public ItemMeta deserialize(DeserializationData data, GenericsDeclaration generics) {
        String display = data.get("display", String.class);
        if (display == null) {
            display = data.get("display-name", String.class);
        }

        List<String> lore = data.containsKey("lore")
                ? data.getAsList("lore", String.class)
                : Collections.emptyList();

        Map<Enchantment, Integer> enchantments = data.containsKey("enchantments")
                ? data.getAsMap("enchantments", Enchantment.class, Integer.class)
                : Collections.emptyMap();

        List<ItemFlag> itemFlags = new ArrayList<>(data.containsKey("flags")
                ? data.getAsList("flags", ItemFlag.class)
                : Collections.emptyList());

        ItemMeta itemMeta = new ItemStack(Material.COBBLESTONE).getItemMeta();
        if (itemMeta == null) {
            throw new IllegalStateException("Cannot extract empty ItemMeta from COBBLESTONE");
        }

        if (display != null) {
            itemMeta.displayName(noItalic(MM.deserialize(display)));
        }

        if (!lore.isEmpty()) {
            itemMeta.lore(lore.stream()
                    .map(line -> noItalic(MM.deserialize(line)))
                    .toList());
        }

        enchantments.forEach((enchantment, level) -> itemMeta.addEnchant(enchantment, level, true));
        itemMeta.addItemFlags(itemFlags.toArray(new ItemFlag[0]));

        if (data.containsKey("custom-model-data")) {
            itemMeta.setCustomModelData(data.get("custom-model-data", Integer.class));
        }

        return itemMeta;
    }

    private String clean(String miniMessage) {
        String withoutItalic = miniMessage
                .replace("<!italic>", "")
                .replace("<italic:false>", "");
        return GradientCollapser.collapse(withoutItalic);
    }

    private Component noItalic(Component component) {
        return component.decoration(TextDecoration.ITALIC, false);
    }
}