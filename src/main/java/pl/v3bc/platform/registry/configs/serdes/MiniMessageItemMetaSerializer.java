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
import org.jetbrains.annotations.NotNull;

import java.util.*;


public class MiniMessageItemMetaSerializer implements ObjectSerializer<ItemMeta> {
    private final MiniMessage MINI = MiniMessage.builder().emitVirtuals(false).build();

    @Override
    public boolean supports(@NotNull Class<?> type) {
        return ItemMeta.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(ItemMeta itemMeta, @NotNull SerializationData data, @NotNull GenericsDeclaration generics) {
        if (itemMeta.hasDisplayName()) {
            Component name = itemMeta.displayName();
            if (name != null) {
                data.set("display", serializeComponent(name));
            }
        }
        if (itemMeta.hasLore()) {
            List<Component> lore = itemMeta.lore();
            if (lore != null) {
                List<String> serializedLore = lore.stream().map(this::serializeComponent).toList();
                data.setCollection("lore", serializedLore, String.class);
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
    public ItemMeta deserialize(DeserializationData data, @NotNull GenericsDeclaration generics) {
        String display = data.get("display", String.class);
        if (display == null) {
            display = data.get("display-name", String.class);
        }
        List<String> lore = data.containsKey("lore") ? data.getAsList("lore", String.class) : Collections.emptyList();
        Map<Enchantment, Integer> enchantments = data.containsKey("enchantments") ? data.getAsMap("enchantments", Enchantment.class, Integer.class) : Collections.emptyMap();
        List<ItemFlag> itemFlags = new ArrayList<>(data.containsKey("flags") ? data.getAsList("flags", ItemFlag.class) : Collections.emptyList());
        ItemMeta itemMeta = Objects.requireNonNull(new ItemStack(Material.COBBLESTONE).getItemMeta());
        if (display != null) {
            itemMeta.displayName(applyDefaultItalic(display));
        }
        if (!lore.isEmpty()) {
            itemMeta.lore(lore.stream().map(this::applyDefaultItalic).toList());
        }
        enchantments.forEach((enchantment, level) -> itemMeta.addEnchant(enchantment, level, true));
        itemMeta.addItemFlags(itemFlags.toArray(new ItemFlag[0]));
        if (data.containsKey("custom-model-data")) {
            itemMeta.setCustomModelData(data.get("custom-model-data", Integer.class));
        }
        return itemMeta;
    }

    private String serializeComponent(Component component) {
        Component stripped = stripItalicFalse(component);
        String serialized = this.MINI.serialize(stripped);
        serialized = serialized.replace("<bold>", "<b>").replace("</bold>", "</b>");
        return GradientCollapser.collapse(serialized);
    }

    private Component applyDefaultItalic(String text) {
        if (text == null || text.isEmpty()) {
            return Component.empty();
        }
        String cleanText = text.replace("<!italic>", "").replace("<!i>", "").replace("<italic:false>", "").replace("<i:false>", "");
        Component component = this.MINI.deserialize(cleanText);
        return component.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

    private Component stripItalicFalse(Component component) {
        if (component.decoration(TextDecoration.ITALIC) == TextDecoration.State.FALSE) {
            component = component.decoration(TextDecoration.ITALIC, TextDecoration.State.NOT_SET);
        }
        return component.children(component.children().stream().map(this::stripItalicFalse).toList());
    }
}