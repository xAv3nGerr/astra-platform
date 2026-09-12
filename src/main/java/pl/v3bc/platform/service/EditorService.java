package pl.v3bc.platform.service;

import com.eternalcode.multification.notice.Notice;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.logging.Level;

@UtilityClass
public final class EditorService {
    private static final Map<UUID, Consumer<String>> activeEditors = new ConcurrentHashMap<>();
    @Setter
    private static Plugin plugin;
    @Setter
    private static NoticeService noticeService;


    public static void startEdit(Player player, Consumer<String> action) {
        activeEditors.put(player.getUniqueId(), action);
    }

    public static boolean isEditing(UUID uuid) {
        return activeEditors.containsKey(uuid);
    }

    public static void applyEdit(Player player, String string) {
        UUID uuid = player.getUniqueId();
        if (string.equalsIgnoreCase("anuluj") || string.equalsIgnoreCase("cancel")) {
            if (cancelEdit(uuid) != null) {
                noticeService.create()
                        .viewer(player)
                        .notice(Notice.title("", "<red>Edytowanie zostało <dark_red>anulowane!"))
                        .sendAsync();
            }
            return;
        }
        Consumer<String> callback = cancelEdit(uuid);
        if (callback == null) {
            return;
        }
        try {
            callback.accept(string);
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to process editor session for player " + player.getName(), e);
        }
    }

    public static Consumer<String> cancelEdit(UUID uuid) {
        return activeEditors.remove(uuid);
    }
}