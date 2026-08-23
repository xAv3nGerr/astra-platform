package pl.v3bc.platform.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * @Author: v3bc_
 * @Date: 8/23/26
 * @Project: astra-platform
 */
public class VectorUtil {

    private VectorUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static void push(Player player, double pushStrength) {
        if (pushStrength == 0.0) {
            pushStrength = 1.0;
        }
        Vector forwardDirection = player.getLocation().getDirection().normalize();
        Vector pushVector = forwardDirection.multiply(pushStrength);
        player.setVelocity(player.getVelocity().add(pushVector));
    }

    public static void knockBack(Player player, Location location) {
        Vector vector = location.toVector().subtract(player.getLocation().toVector()).normalize().multiply(1.4).setY(0.1);
        if (player.isInsideVehicle()) {
            player.leaveVehicle();
        }
        player.setVelocity(vector);
    }
}