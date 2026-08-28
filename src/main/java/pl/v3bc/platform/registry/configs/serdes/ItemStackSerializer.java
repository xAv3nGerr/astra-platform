package pl.v3bc.platform.registry.configs.serdes;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.v3bc.platform.utils.NekoChat;

import java.util.List;

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

        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
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
        }
    }

    @Override
    public ItemStack deserialize(DeserializationData data, GenericsDeclaration generics) {
        Material material = data.get("material", Material.class);
        String name = data.containsKey("name") ? data.get("name", String.class) : null;
        List<String> lore = data.containsKey("lore") ? data.getAsList("lore", String.class) : null;

        ItemStack item = new ItemStack(material != null ? material : Material.STONE);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            if (name != null) {
                meta.displayName(NekoChat.translate(name));
            }
            if (lore != null) {
                meta.lore(NekoChat.translate(lore));
            }
            item.setItemMeta(meta);
        }

        return item;
    }
}