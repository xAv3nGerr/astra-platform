package pl.v3bc.platform.service.editor;

import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import pl.v3bc.platform.service.EditorService;
import pl.v3bc.platform.utils.ChatUtil;

@RequiredArgsConstructor
public class ChatEditorListener implements Listener {
    private final Plugin plugin;

    @EventHandler(priority = EventPriority.LOWEST)
    public void handleAsyncChatEvent(AsyncChatEvent asyncChatEvent) {
        Player player = asyncChatEvent.getPlayer();
        if (!EditorService.isEditing(player.getUniqueId())) {
            return;
        }
        asyncChatEvent.setCancelled(true);
        String input = ChatUtil.toLegacy(asyncChatEvent.message());
        Bukkit.getScheduler().runTask(this.plugin, () -> EditorService.applyEdit(player, input));
    }

    @EventHandler
    public void handleQuit(PlayerQuitEvent playerQuitEvent) {
        EditorService.cancelEdit(playerQuitEvent.getPlayer().getUniqueId());
    }
}