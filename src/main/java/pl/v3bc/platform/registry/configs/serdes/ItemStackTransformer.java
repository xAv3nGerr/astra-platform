package pl.v3bc.platform.registry.configs.serdes;

import eu.okaeri.configs.schema.GenericsPair;
import eu.okaeri.configs.serdes.BidirectionalTransformer;
import eu.okaeri.configs.serdes.SerdesContext;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import pl.v3bc.platform.utils.NekoChat;

import java.util.*;

public class ItemStackTransformer extends BidirectionalTransformer<ItemStack, Map> {

    @Override
    public GenericsPair<ItemStack, Map> getPair() {
        return this.genericsPair(ItemStack.class, Map.class);
    }

    @Override
    public Map<String, Object> leftToRight(ItemStack itemStack, SerdesContext context) {
        Map<String, Object> map = new LinkedHashMap<>();
        if (itemStack == null) return map;

        map.put("material", itemStack.getType().name());

        if (itemStack.getAmount() > 1) {
            map.put("amount", itemStack.getAmount());
        }

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return map;

        if (meta.hasDisplayName() && meta.displayName() != null) {
            String serializedName = MiniMessage.miniMessage().serialize(meta.displayName())
                    .replace("<!italic>", "")
                    .replace("<italic:false>", "");
            map.put("name", serializedName);
        }

        if (meta.hasLore() && meta.lore() != null) {
            List<String> serializedLore = meta.lore().stream()
                    .map(line -> MiniMessage.miniMessage().serialize(line)
                            .replace("<!italic>", "")
                            .replace("<italic:false>", ""))
                    .toList();
            map.put("lore", serializedLore);
        }

        if (meta.hasCustomModelData()) {
            map.put("custom-model-data", meta.getCustomModelData());
        }

        if (meta.hasEnchants()) {
            Map<String, Integer> enchantsMap = new LinkedHashMap<>();
            meta.getEnchants().forEach((enchant, level) ->
                    enchantsMap.put(enchant.getKey().getKey(), level)
            );
            map.put("enchants", enchantsMap);
        }

        Set<ItemFlag> flags = meta.getItemFlags();
        if (!flags.isEmpty()) {
            List<String> flagNames = flags.stream().map(Enum::name).toList();
            map.put("flags", flagNames);
        }

        if (meta instanceof SkullMeta skullMeta && skullMeta.getPlayerProfile() != null) {
            skullMeta.getPlayerProfile().getProperties().stream()
                    .filter(prop -> "textures".equals(prop.getName()))
                    .findFirst()
                    .ifPresent(prop -> map.put("texture", prop.getValue()));
        }

        return map;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ItemStack rightToLeft(Map map, SerdesContext context) {
        String materialName = (String) map.get("material");
        Material material = materialName != null ? Material.matchMaterial(materialName) : Material.STONE;
        if (material == null) material = Material.STONE;

        int amount = map.containsKey("amount") && map.get("amount") instanceof Number num ? num.intValue() : 1;

        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            if (map.containsKey("name") && map.get("name") instanceof String name) {
                meta.displayName(NekoChat.translate("<!italic>" + name));
            }

            if (map.containsKey("lore") && map.get("lore") instanceof List<?> loreList) {
                List<Component> translatedLore = loreList.stream()
                        .map(Object::toString)
                        .map(line -> NekoChat.translate("<!italic>" + line))
                        .toList();
                meta.lore(translatedLore);
            }

            if (map.containsKey("custom-model-data") && map.get("custom-model-data") instanceof Number cmd) {
                meta.setCustomModelData(cmd.intValue());
            }

            if (map.containsKey("enchants") && map.get("enchants") instanceof Map<?, ?> enchantsMap) {
                enchantsMap.forEach((key, val) -> {
                    String enchantKey = String.valueOf(key);
                    int level = val instanceof Number num ? num.intValue() : 1;
                    Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantKey.toLowerCase()));
                    if (enchantment != null) {
                        meta.addEnchant(enchantment, level, true);
                    }
                });
            }

            if (map.containsKey("flags") && map.get("flags") instanceof List<?> flagsList) {
                for (Object flagObj : flagsList) {
                    try {
                        meta.addItemFlags(ItemFlag.valueOf(flagObj.toString().toUpperCase()));
                    } catch (IllegalArgumentException ignored) {}
                }
            }

            if (map.containsKey("texture") && map.get("texture") instanceof String texture && meta instanceof SkullMeta skullMeta) {
                if (!texture.isEmpty()) {
                    var profile = Bukkit.createProfile(UUID.randomUUID());
                    profile.setProperty(new com.destroystokyo.paper.profile.ProfileProperty("textures", texture));
                    skullMeta.setPlayerProfile(profile);
                }
            }

            item.setItemMeta(meta);
        }

        return item;
    }
}