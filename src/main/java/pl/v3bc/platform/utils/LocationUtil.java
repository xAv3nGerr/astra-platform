package pl.v3bc.platform.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;

import java.util.List;

/**
 * @Author: v3bc_
 * @Date: 8/23/26
 * @Project: astra-platform
 */

public class LocationUtil {
    public static boolean isInRadius(Location location, int n) {
        return Math.abs(location.getBlockX()) <= n && Math.abs(location.getBlockZ()) <= n;
    }

    public static boolean isInRadius(String string, Location location, int n) {
        String[] stringArray = string.split(";");
        if (!stringArray[0].equals(location.getWorld().getName())) {
            return false;
        }
        int n2 = Math.abs(location.getBlockX() - LocationUtil.cordsToBlock(Double.parseDouble(stringArray[1])));
        int n3 = Math.abs(location.getBlockZ() - LocationUtil.cordsToBlock(Double.parseDouble(stringArray[2])));
        return n2 <= n && n3 <= n;
    }

    public static boolean isInRadius(Location location, Location location2, int n) {
        if (!location.getWorld().getUID().equals(location2.getWorld().getUID())) {
            return false;
        }
        int n2 = Math.abs(location2.getBlockX() - location.getBlockX());
        int n3 = Math.abs(location2.getBlockZ() - location.getBlockZ());
        return n2 <= n && n3 <= n;
    }

    public static boolean isInRadius(Location location, Location location2, int n, int n2) {
        if (!location.getWorld().getUID().equals(location2.getWorld().getUID())) {
            return false;
        }
        int n3 = Math.abs(location2.getBlockY() - location.getBlockY());
        int n4 = Math.abs(location2.getBlockX() - location.getBlockX());
        int n5 = Math.abs(location2.getBlockZ() - location.getBlockZ());
        return n4 <= n && n5 <= n && n3 <= n2;
    }

    public static boolean isSame(Location location, Location location2) {
        return location.getBlockX() == location2.getBlockX() && location.getBlockY() == location2.getBlockY() && location.getBlockZ() == location2.getBlockZ();
    }

    public static Location deserialize(String string) {
        String[] stringArray = string.split(";");
        World world = Bukkit.getWorld(stringArray[0]);
        if (world == null) {
            throw new IllegalArgumentException("World not found: " + stringArray[0]);
        }
        return new Location(world, Double.parseDouble(stringArray[1]), Double.parseDouble(stringArray[2]), Double.parseDouble(stringArray[3]), Float.parseFloat(stringArray[4]), Float.parseFloat(stringArray[5]));
    }

    public static String serialize(Location location) {
        return location.getWorld().getName() + ";" + location.getX() + ";" + location.getY() + ";" + location.getZ() + ";" + location.getYaw() + ";" + location.getPitch();
    }

    public static String serializeCenter(Location location) {
        return location.getWorld().getName() + ";" + ((double) location.getBlockX() + 0.5) + ";" + location.getY() + ";" + ((double) location.getBlockZ() + 0.5) + ";" + location.getYaw() + ";" + location.getPitch();
    }

    public static String locationToString(Location location) {
        return location.getWorld().getName() + ";" + location.getBlockX() + ";" + location.getBlockY() + ";" + location.getBlockZ();
    }

    public static Location locationFromString(String string) {
        String[] stringArray = string.split(";");
        return new Location(Bukkit.getWorld(stringArray[0]), Double.parseDouble(stringArray[1]), Double.parseDouble(stringArray[2]), Double.parseDouble(stringArray[3]));
    }

    public static String customLocationToString(String string) {
        return string;
    }

    public static Location closestLocation(List<Location> list, Location location) {
        double d = -1.0;
        Location location2 = null;
        for (Location location3 : list) {
            if (location2 != null && Math.pow(location.getBlockX() - location3.getBlockX(), 2.0) + Math.pow(location.getBlockZ() - location3.getBlockZ(), 2.0) > d)
                continue;
            location2 = location3;
            d = Math.pow(location.getBlockX() - location3.getBlockX(), 2.0) + Math.pow(location.getBlockZ() - location3.getBlockZ(), 2.0);
        }
        return location2;
    }

    public static Location farthestLocation(List<Location> list, Location location) {
        double d = -1.0;
        Location location2 = null;
        for (Location location3 : list) {
            double d2 = Math.pow(location.getBlockZ() - location3.getBlockZ(), 2.0);
            double d3 = Math.pow(location.getBlockX() - location3.getBlockX(), 2.0);
            if (location2 != null && d3 + d2 < d) continue;
            location2 = location3;
            d = Math.pow(location.getBlockX() - location3.getBlockX(), 2.0) + Math.pow(location.getBlockZ() - location3.getBlockZ(), 2.0);
        }
        return location2;
    }

    public static Block findHighestBlock(World world, int n, int n2, int n3, int n4) {
        int n5 = 1;
        while (n5 >= n3) {
            if (n5 > n4) {
                return null;
            }
            Block block = new Location(world, n, n5, n2).getBlock();
            if (block.getType().equals(Material.AIR)) {
                return block;
            }
            ++n5;
        }
        return null;
    }

    public static int cordsToBlock(double d) {
        if (d >= 0.0) {
            return (int) d;
        }
        return (int) d - 1;
    }

    public static String toStringClaim(Location location) {
        return location.getWorld().getName() + ";" + (location.getBlockX() >> 4);
    }

    public static String toStringClaim(String string) {
        String[] stringArray = string.split(";");
        return stringArray[0] + ";" + (LocationUtil.cordsToBlock(Double.parseDouble(stringArray[1])) >> 4);
    }

    public static void clearItemsInWorld(World world) {
        world.getEntities().stream().filter(entity -> entity instanceof Item).forEach(Entity::remove);
    }

    public static boolean isWithinBounds(Location location, Location location2, Location location3) {
        if (!location.getWorld().equals(location2.getWorld()) || !location.getWorld().equals(location3.getWorld())) {
            return false;
        }
        double d = Math.min(location2.getX(), location3.getX());
        double d2 = Math.max(location2.getX(), location3.getX());
        double d3 = Math.min(location2.getY(), location3.getY());
        double d4 = Math.max(location2.getY(), location3.getY());
        double d5 = Math.min(location2.getZ(), location3.getZ());
        double d6 = Math.max(location2.getZ(), location3.getZ());
        return location.getX() >= d && location.getX() <= d2 && location.getY() >= d3 && location.getY() <= d4 && location.getZ() >= d5 && location.getZ() <= d6;
    }
}