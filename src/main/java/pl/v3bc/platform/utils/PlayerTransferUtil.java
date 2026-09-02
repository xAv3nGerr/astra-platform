package pl.v3bc.platform.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


public final class PlayerTransferUtil {

    private PlayerTransferUtil() {
    }

    @SuppressWarnings("UnstableApiUsage")
    public static void addToQueue(Plugin plugin, Player player, String targetServer) {
        if (plugin == null || player == null || targetServer == null || targetServer.isEmpty()) {
            return;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(targetServer);

        player.sendPluginMessage(plugin, "astra:queue", out.toByteArray());
    }

    @SuppressWarnings("UnstableApiUsage")
    public static void sendPlayerToServer(Plugin plugin, Player player, String server) {
        if (plugin == null || player == null || server == null || server.isEmpty()) {
            return;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);

        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }

    @SuppressWarnings("UnstableApiUsage")
    public static void sendPlayerToServerByName(Plugin plugin, String playerName, String targetServer) {
        if (plugin == null || playerName == null || playerName.isEmpty() || targetServer == null || targetServer.isEmpty()) {
            return;
        }

        Player messenger = Bukkit.getOnlinePlayers().stream().findFirst().orElse(null);
        if (messenger == null) {
            return;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("ConnectOther");
        out.writeUTF(playerName);
        out.writeUTF(targetServer);

        messenger.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }

    @SuppressWarnings("UnstableApiUsage")
    public static void sendAllPlayersFromServerToServer(Plugin plugin, String fromServer, String targetServer) {
        if (plugin == null || fromServer == null || fromServer.isEmpty() || targetServer == null || targetServer.isEmpty()) {
            return;
        }

        Player messenger = Bukkit.getOnlinePlayers().stream().findFirst().orElse(null);
        if (messenger == null) {
            return;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("ExecuteCommand");
        out.writeUTF("send " + fromServer + " " + targetServer);

        messenger.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }

    @SuppressWarnings("UnstableApiUsage")
    public static void requestCurrentServerName(Plugin plugin, Player player) {
        if (plugin == null || player == null) return;

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetServer");

        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }

    public static boolean isLocalServer(String currentServer, String targetServer) {
        if (targetServer == null || targetServer.isEmpty()) {
            return true;
        }
        return targetServer.equalsIgnoreCase(currentServer);
    }
}