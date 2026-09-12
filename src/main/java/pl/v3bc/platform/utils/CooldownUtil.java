package pl.v3bc.platform.utils;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.v3bc.platform.utils.time.TimeUtil;

import java.time.Duration;
import java.util.Map;

/**
 * @Author: v3bc_
 * @Date: 9/12/26
 * @Project: astra-platform
 */
@UtilityClass
public class CooldownUtil {

    public static void applyCooldown(@NonNull Player player, @NonNull ItemStack itemStack, int seconds) {
        if (itemStack.getType() == Material.AIR) {
            return;
        }
        applyCooldown(player, itemStack.getType(), seconds);
    }

    public static void applyCooldown(@NonNull Player player, @NonNull Material material, int seconds) {
        if (material == Material.AIR) {
            return;
        }
        player.setCooldown(material, seconds * 20);
    }

    public static void resetCooldown(@NonNull Player player, @NonNull ItemStack itemStack) {
        if (itemStack.getType() == Material.AIR) {
            return;
        }
        resetCooldown(player, itemStack.getType());
    }

    public static void resetCooldown(@NonNull Player player, @NonNull Material material) {
        player.setCooldown(material, 0);
    }

    public static boolean hasCooldownAndNotify(@NonNull Player player, @NonNull ItemStack itemStack, @NonNull String message) {
        if (itemStack.getType() == Material.AIR) {
            return false;
        }
        return hasCooldownAndNotify(player, itemStack.getType(), message);
    }

    public static boolean hasCooldownAndNotify(@NonNull Player player, @NonNull Material material, @NonNull String message) {
        if (material == Material.AIR) {
            return false;
        }

        if (player.hasCooldown(material)) {
            int remainingTicks = player.getCooldown(material);
            long remainingMillis = (remainingTicks / 20L) * 1000L;

            String formattedTime = TimeUtil.formatTimeSimple(Duration.ofMillis(remainingMillis));

            player.sendMessage(ChatUtil.component(message, Map.of("TIME", formattedTime)));
            return true;
        }

        return false;
    }
}