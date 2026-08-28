package pl.v3bc.platform.utils;

import lombok.Generated;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;
import pl.v3bc.platform.utils.nbt.ItemNbt;

import java.util.List;
import java.util.Objects;

public final class ItemUtil {

    public static boolean isSimilar(@NonNull ItemStack item1, @NonNull ItemStack item2) {
        return item1.isSimilar(item2);
    }

    public static void giveItem(@NonNull Player player, @NonNull ItemStack itemStack) {
        player.getInventory().addItem(itemStack);
    }

    public static void giveItemOrDrop(@NonNull Player player, @NonNull ItemStack itemStack) {
        if (ItemUtil.hasSpace(player.getInventory(), itemStack)) {
            player.getInventory().addItem(itemStack);
            return;
        }
        player.getLocation().getWorld().dropItemNaturally(player.getLocation(), itemStack);
    }

    public static void giveItems(@NonNull Player player, @NonNull List<ItemStack> itemStacks) {
        for (ItemStack itemStack : itemStacks) {
            if (itemStack == null || itemStack.getType().isAir()) {
                continue;
            }
            ItemUtil.giveItemOrDrop(player, itemStack);
        }
    }

    public static int countItems(@NonNull Player player, @NonNull ItemStack itemStack) {
        int n = 0;
        for (ItemStack itemStack2 : player.getInventory().getContents()) {
            if (itemStack2 == null || !itemStack2.isSimilar(itemStack)) {
                continue;
            }
            n += itemStack2.getAmount();
        }
        return n;
    }

    public static int countItems(@NonNull Player player, @NonNull String string, @NonNull String string2, @NonNull PersistentDataType persistentDataType) {
        int n = 0;
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack == null || !itemStack.hasItemMeta() || !ItemNbt.hasCustomData(itemStack, string, persistentDataType) || !Objects.equals(ItemNbt.getCustomData(itemStack, string, persistentDataType), string2)) {
                continue;
            }
            n += itemStack.getAmount();
        }
        return n;
    }

    public static int countItems(@NonNull Player player, @NonNull Material material) {
        int n = 0;
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack == null || !itemStack.getType().equals(material)) {
                continue;
            }
            n += itemStack.getAmount();
        }
        return n;
    }

    public static boolean haveItem(@NonNull Player player, @NonNull ItemStack itemStack) {
        for (ItemStack itemStack2 : player.getInventory().getContents()) {
            if (itemStack2 == null || !itemStack2.isSimilar(itemStack)) {
                continue;
            }
            return true;
        }
        return false;
    }

    public static boolean hasEmptyInventory(Player player) {
        for (ItemStack item : player.getInventory().getStorageContents()) {
            if (item != null && !item.getType().isAir()) {
                return false;
            }
        }
        return true;
    }

    public static boolean hasFullEmptyInventory(@NonNull Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && !item.getType().isAir()) {
                return false;
            }
        }
        return true;
    }

    public static boolean haveItem(@NonNull Player player, @NonNull String string, @NonNull String string2, @NonNull PersistentDataType persistentDataType) {
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack == null || !itemStack.hasItemMeta() || !ItemNbt.hasCustomData(itemStack, string, persistentDataType) || !Objects.equals(ItemNbt.getCustomData(itemStack, string, persistentDataType), string2)) {
                continue;
            }
            return true;
        }
        return false;
    }

    public static boolean haveItem(List<ItemStack> list, @NonNull String string, @NonNull String string2, @NonNull PersistentDataType persistentDataType) {
        if (list == null) {
            return false;
        }
        if (list.isEmpty()) {
            return false;
        }
        for (ItemStack itemStack : list) {
            if (itemStack == null || !itemStack.hasItemMeta() || !ItemNbt.hasCustomData(itemStack, string, persistentDataType) || !Objects.equals(ItemNbt.getCustomData(itemStack, string, persistentDataType), string2)) {
                continue;
            }
            return true;
        }
        return false;
    }

    public static void takeItem(@NonNull Player player, @NonNull ItemStack itemStack, int n) {
        PlayerInventory playerInventory = player.getInventory();
        int n2 = n;
        for (ItemStack itemStack2 : playerInventory.getContents()) {
            if (itemStack2 == null || !itemStack2.isSimilar(itemStack)) {
                continue;
            }
            if (itemStack2.getAmount() > n2) {
                itemStack2.setAmount(itemStack2.getAmount() - n2);
                return;
            }
            itemStack2.setAmount(0);
            if ((n2 -= itemStack2.getAmount()) > 0) {
                continue;
            }
            return;
        }
    }

    public static void takeItem(@NonNull Player player, @NonNull Material material, int n) {
        PlayerInventory playerInventory = player.getInventory();
        int n2 = n;
        for (ItemStack itemStack : playerInventory.getContents()) {
            if (itemStack == null || itemStack.getType() != material) {
                continue;
            }
            if (itemStack.getAmount() > n2) {
                itemStack.setAmount(itemStack.getAmount() - n2);
                return;
            }
            itemStack.setAmount(0);
            if ((n2 -= itemStack.getAmount()) > 0) {
                continue;
            }
            return;
        }
    }

    public static void takeItem(@NonNull Player player, @NonNull String string, @NonNull String string2, @NonNull PersistentDataType persistentDataType, int n) {
        PlayerInventory playerInventory = player.getInventory();
        int n2 = n;
        for (ItemStack itemStack : playerInventory.getContents()) {
            if (itemStack == null || !ItemNbt.hasCustomData(itemStack, string, persistentDataType) || !Objects.equals(ItemNbt.getCustomData(itemStack, string, persistentDataType), string2)) {
                continue;
            }
            if (itemStack.getAmount() > n2) {
                itemStack.setAmount(itemStack.getAmount() - n2);
                return;
            }
            itemStack.setAmount(0);
            if ((n2 -= itemStack.getAmount()) > 0) continue;
            return;
        }
    }

    public static ItemStack getItem(@NonNull Player player, @NonNull NamespacedKey namespacedKey, @NonNull PersistentDataType persistentDataType) {
        PlayerInventory playerInventory = player.getInventory();
        for (ItemStack itemStack : playerInventory.getContents()) {
            if (itemStack == null || !itemStack.hasItemMeta() || !itemStack.getItemMeta().getPersistentDataContainer().has(namespacedKey, persistentDataType)) {
                continue;
            }
            return itemStack;
        }
        return null;
    }

    public static boolean hasSpace(@NonNull Inventory inventory, @NonNull ItemStack itemStack) {
        if (inventory.firstEmpty() != -1) {
            return true;
        }
        for (ItemStack itemStack2 : inventory.getContents()) {
            if (itemStack2 == null || !itemStack2.isSimilar(itemStack) || itemStack2.getMaxStackSize() <= itemStack2.getAmount()) {
                continue;
            }
            return true;
        }
        return false;
    }

    @Generated
    private ItemUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
