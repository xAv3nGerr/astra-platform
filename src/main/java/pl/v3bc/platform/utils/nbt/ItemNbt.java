package pl.v3bc.platform.utils.nbt;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import pl.v3bc.platform.Main;

/**
 * @Author: v3bc_
 * @Date: 8/26/26
 * @Project: astra-platform
 */

public final class ItemNbt {

    public static boolean hasCustomData(ItemStack itemStack, String string, PersistentDataType persistentDataType) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return false;
        }
        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        return persistentDataContainer.has(new NamespacedKey(Main.getInstance(), string), persistentDataType);
    }

    public static Object getCustomData(ItemStack itemStack, String string, PersistentDataType persistentDataType) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return null;
        }
        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        return persistentDataContainer.get(new NamespacedKey(Main.getInstance(), string), persistentDataType);
    }

    public static ItemStack withCustomData(ItemStack itemStack, String string, Object object, PersistentDataType persistentDataType) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
            persistentDataContainer.set(new NamespacedKey(Main.getInstance(), string), persistentDataType, object);
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }

    private ItemNbt() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}