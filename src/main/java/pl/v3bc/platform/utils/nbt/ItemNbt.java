package pl.v3bc.platform.utils.nbt;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public final class ItemNbt {

    private static final String NAMESPACE = "astra";

    public static <T, Z> boolean hasCustomData(ItemStack itemStack, String key, PersistentDataType<T, Z> dataType) {
        if (itemStack == null || !itemStack.hasItemMeta()) return false;
        return itemStack.getItemMeta().getPersistentDataContainer()
                .has(new NamespacedKey(NAMESPACE, key), dataType);
    }

    public static <T, Z> Z getCustomData(ItemStack itemStack, String key, PersistentDataType<T, Z> dataType) {
        if (itemStack == null || !itemStack.hasItemMeta()) return null;
        return itemStack.getItemMeta().getPersistentDataContainer()
                .get(new NamespacedKey(NAMESPACE, key), dataType);
    }

    public static <T, Z> ItemStack withCustomData(ItemStack itemStack, String key, Z value, PersistentDataType<T, Z> dataType) {
        if (itemStack == null) return null;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.getPersistentDataContainer().set(new NamespacedKey(NAMESPACE, key), dataType, value);
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }

    private ItemNbt() {
        throw new UnsupportedOperationException("Utility class");
    }
}