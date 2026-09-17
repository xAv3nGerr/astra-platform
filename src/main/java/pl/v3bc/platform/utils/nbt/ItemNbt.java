package pl.v3bc.platform.utils.nbt;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import pl.v3bc.platform.Main;

public final class ItemNbt {

    public static <T, Z> boolean hasCustomData(ItemStack itemStack, String key, PersistentDataType<T, Z> persistentDataType) {
        if (itemStack == null || !itemStack.hasItemMeta()) {
            return false;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        return container.has(new NamespacedKey(Main.getInstance(), key), persistentDataType);
    }

    public static <T, Z> Z getCustomData(ItemStack itemStack, String key, PersistentDataType<T, Z> persistentDataType) {
        if (itemStack == null || !itemStack.hasItemMeta()) {
            return null;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        return container.get(new NamespacedKey(Main.getInstance(), key), persistentDataType);
    }

    public static <T, Z> ItemStack withCustomData(ItemStack itemStack, String key, Z value, PersistentDataType<T, Z> persistentDataType) {
        if (itemStack == null) {
            return null;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            PersistentDataContainer container = itemMeta.getPersistentDataContainer();
            container.set(new NamespacedKey(Main.getInstance(), key), persistentDataType, value);
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }

    private ItemNbt() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}