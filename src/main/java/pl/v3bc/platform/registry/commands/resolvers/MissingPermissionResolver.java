package pl.v3bc.platform.registry.commands.resolvers;

import com.eternalcode.multification.notice.Notice;
import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.permission.MissingPermissions;
import dev.rollczi.litecommands.permission.MissingPermissionsHandler;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.v3bc.platform.service.NoticeService;

@RequiredArgsConstructor
public class MissingPermissionResolver implements MissingPermissionsHandler<CommandSender> {
    private final NoticeService noticeService;

    @Override
    public void handle(Invocation<CommandSender> invocation, MissingPermissions missingPermissions, ResultHandlerChain<CommandSender> chain) {
        final CommandSender sender = invocation.sender();
        String permissionsText = String.join(", ", missingPermissions.getPermissions());

        if (sender instanceof Player player) {
            this.noticeService.create()
                    .viewer(player)
                    .notice(Notice.builder()
                            .title("","<red>Brak permisji: <dark_red>" + permissionsText)
                            .sound("minecraft:entity.villager.no")
                            .build())
                    .sendAsync();
        }
    }
}
