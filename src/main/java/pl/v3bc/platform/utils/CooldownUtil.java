package pl.v3bc.platform.utils;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.v3bc.platform.utils.time.TimeUtil;

import java.time.Duration;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@UtilityClass
public class CooldownUtil {

    private static final long NOTIFY_INTERVAL_MILLIS = 1000L;
    private static final Map<UUID, Map<Material, Long>> LAST_NOTIFY = new ConcurrentHashMap<>();

    public static void applyCooldown(@NonNull Player player, @NonNull ItemStack itemStack, int seconds) {
        if (itemStack.getType() == Material.AIR) return;
        applyCooldown(player, itemStack.getType(), seconds);
    }

    public static void applyCooldown(@NonNull Player player, @NonNull Material material, int seconds) {
        if (material == Material.AIR) return;
        player.setCooldown(material, seconds * 20);
    }

    public static void resetCooldown(@NonNull Player player, @NonNull ItemStack itemStack) {
        if (itemStack.getType() == Material.AIR) return;
        resetCooldown(player, itemStack.getType());
    }

    public static void resetCooldown(@NonNull Player player, @NonNull Material material) {
        player.setCooldown(material, 0);
    }

    public static boolean hasCooldownAndNotify(@NonNull Player player, @NonNull ItemStack itemStack, @NonNull String message) {
        if (itemStack.getType() == Material.AIR) return false;
        return hasCooldownAndNotify(player, itemStack.getType(), message);
    }

    public static boolean hasCooldownAndNotify(@NonNull Player player, @NonNull Material material, @NonNull String message) {
        if (material == Material.AIR) return false;
        if (!player.hasCooldown(material)) return false;

        long now = System.currentTimeMillis();
        Map<Material, Long> playerNotifications = LAST_NOTIFY.computeIfAbsent(
                player.getUniqueId(), k -> new EnumMap<>(Material.class));

        Long lastNotify = playerNotifications.get(material);
        if (lastNotify == null || now - lastNotify >= NOTIFY_INTERVAL_MILLIS) {
            int remainingTicks = player.getCooldown(material);
            long remainingMillis = (remainingTicks / 20L) * 1000L;
            String formattedTime = TimeUtil.formatTimeSimple(Duration.ofMillis(remainingMillis));

            player.sendMessage(ChatUtil.component(message, Map.of("TIME", formattedTime)));
            playerNotifications.put(material, now);
        }

        return true;
    }

    public static void clearNotifyCache(@NonNull Player player) {
        LAST_NOTIFY.remove(player.getUniqueId());
    }
}