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
        if (itemStack == null) return;

        data.add("material", itemStack.getType());

        if (itemStack.getAmount() > 1) {
            data.add("amount", itemStack.getAmount());
        }

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return;

        if (meta.hasDisplayName() && meta.displayName() != null) {
            String serializedName = MiniMessage.miniMessage().serialize(meta.displayName());
            serializedName = serializedName.replace("<!italic>", "").replace("<italic:false>", "");
            data.add("name", serializedName);
        }

        if (meta.hasLore() && meta.lore() != null) {
            List<String> serializedLore = meta.lore().stream()
                    .map(line -> MiniMessage.miniMessage().serialize(line)
                            .replace("<!italic>", "")
                            .replace("<italic:false>", ""))
                    .toList();
            data.addCollection("lore", serializedLore, String.class);
        }

        if (meta.hasCustomModelData()) {
            data.add("custom-model-data", meta.getCustomModelData());
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

        if (meta != null) {
            if (data.containsKey("name")) {
                String name = data.get("name", String.class);
                if (name != null) {
                    meta.displayName(NekoChat.translate("<!italic>" + name));
                }
            }

            if (data.containsKey("lore")) {
                List<String> lore = data.getAsList("lore", String.class);
                if (lore != null) {
                    List<Component> translatedLore = lore.stream()
                            .map(line -> NekoChat.translate("<!italic>" + line))
                            .toList();
                    meta.lore(translatedLore);
                }
            }

            item.setItemMeta(meta);
        }

        return item;
    }
}