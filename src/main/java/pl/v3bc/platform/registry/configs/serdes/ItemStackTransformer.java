package pl.v3bc.platform.registry.configs.serdes;

import eu.okaeri.configs.schema.GenericsPair;
import eu.okaeri.configs.serdes.BidirectionalTransformer;
import eu.okaeri.configs.serdes.SerdesContext;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.v3bc.platform.utils.menu.ItemBuilder;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

        System.out.println("[DEBUG] ItemStackTransformer.leftToRight called for " + itemStack.getType());

        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            if (meta.hasDisplayName() && meta.displayName() != null) {
                // Serializujemy komponent z powrotem do czystego tekstu MiniMessage
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

        // Tworzymy przedmiot przy użyciu Twojego ItemBuilder
        ItemBuilder builder = ItemBuilder.of(new ItemStack(material, amount));

        if (map.containsKey("name") && map.get("name") instanceof String name) {
            builder.name("<!italic>" + name);
        }

        if (map.containsKey("lore") && map.get("lore") instanceof List<?> loreList) {
            List<String> lore = loreList.stream()
                    .map(Object::toString)
                    .map(line -> "<!italic>" + line)
                    .toList();
            builder.lore(lore);
        }

        if (map.containsKey("custom-model-data") && map.get("custom-model-data") instanceof Number cmd) {
            builder.setCustomModelData(cmd.intValue());
        }

        return builder.asItemStack();
    }
}