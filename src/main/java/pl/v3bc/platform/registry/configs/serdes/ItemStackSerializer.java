package pl.v3bc.platform.registry.configs.serdes;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import pl.v3bc.platform.utils.NekoChat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ItemStackSerializer implements ObjectSerializer<ItemStack> {

    @Override
    public boolean supports(Class<? super ItemStack> type) {
        return ItemStack.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(ItemStack itemStack, SerializationData data, GenericsDeclaration generics) {
        if (itemStack == null) {
            return;
        }

        data.add("material", itemStack.getType());

        if (itemStack.getAmount() > 1) {
            data.add("amount", itemStack.getAmount());
        }

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return;
        }

        if (meta.hasDisplayName()) {
            Component displayName = meta.displayName();
            if (displayName != null) {
                data.add("name", MiniMessage.miniMessage().serialize(displayName));
            }
        }

        if (meta.hasLore()) {
            List<Component> lore = meta.lore();
            if (lore != null) {
                List<String> serializedLore = lore.stream()
                        .map(MiniMessage.miniMessage()::serialize)
                        .toList();
                data.addCollection("lore", serializedLore, String.class);
            }
        }

        if (meta.hasCustomModelData()) {
            data.add("custom-model-data", meta.getCustomModelData());
        }

        if (meta.hasEnchants()) {
            Map<String, Integer> enchantsMap = new HashMap<>();
            meta.getEnchants().forEach((enchant, level) ->
                    enchantsMap.put(enchant.getKey().getKey(), level)
            );
            data.add("enchants", enchantsMap);
        }

        Set<ItemFlag> flags = meta.getItemFlags();
        if (!flags.isEmpty()) {
            List<String> flagNames = flags.stream().map(Enum::name).toList();
            data.addCollection("flags", flagNames, String.class);
        }

        if (meta instanceof SkullMeta skullMeta && skullMeta.getPlayerProfile() != null) {
            skullMeta.getPlayerProfile().getProperties().stream()
                    .filter(prop -> "textures".equals(prop.getName()))
                    .findFirst()
                    .ifPresent(prop -> data.add("texture", prop.getValue()));
        }
    }

    @Override
    public ItemStack deserialize(DeserializationData data, GenericsDeclaration generics) {
        Material material = data.containsKey("material")
                ? data.get("material", Material.class)
                : Material.STONE;
        int amount = data.containsKey("amount") ? data.get("amount", Integer.class) : 1;

        ItemStack item = new ItemStack(material != null ? material : Material.STONE, amount);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return item;
        }

        if (data.containsKey("name")) {
            String name = data.get("name", String.class);
            if (name != null) {
                meta.displayName(NekoChat.translate(name));
            }
        }

        if (data.containsKey("lore")) {
            List<String> lore = data.getAsList("lore", String.class);
            if (lore != null) {
                meta.lore(NekoChat.translate(lore));
            }
        }

        if (data.containsKey("custom-model-data")) {
            meta.setCustomModelData(data.get("custom-model-data", Integer.class));
        }

        if (data.containsKey("enchants")) {
            Map<?, ?> enchants = data.get("enchants", Map.class);
            if (enchants != null) {
                enchants.forEach((key, val) -> {
                    String enchantKey = String.valueOf(key);
                    int level = val instanceof Number number ? number.intValue() : 1;

                    Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantKey.toLowerCase()));
                    if (enchantment != null) {
                        meta.addEnchant(enchantment, level, true);
                    }
                });
            }
        }
        if (data.containsKey("flags")) {
            List<String> flags = data.getAsList("flags", String.class);
            if (flags != null) {
                for (String flagName : flags) {
                    try {
                        meta.addItemFlags(ItemFlag.valueOf(flagName.toUpperCase()));
                    } catch (IllegalArgumentException ignored) {}
                }
            }
        }

        if (data.containsKey("texture") && meta instanceof SkullMeta skullMeta) {
            String texture = data.get("texture", String.class);
            if (texture != null && !texture.isEmpty()) {
                com.destroystokyo.paper.profile.PlayerProfile profile = org.bukkit.Bukkit.createProfile(java.util.UUID.randomUUID());
                profile.setProperty(new com.destroystokyo.paper.profile.ProfileProperty("textures", texture));
                skullMeta.setPlayerProfile(profile);
            }
        }

        item.setItemMeta(meta);
        return item;
    }
}