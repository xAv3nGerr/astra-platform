package pl.v3bc.platform.registry.commands.resolvers;

import com.eternalcode.multification.notice.Notice;
import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invalidusage.InvalidUsage;
import dev.rollczi.litecommands.invalidusage.InvalidUsageHandler;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.schematic.Schematic;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.v3bc.platform.service.NoticeService;
import pl.v3bc.platform.utils.adventure.NekoChat;


@RequiredArgsConstructor
public class InvalidUsageResolver implements InvalidUsageHandler<CommandSender> {
    private final NoticeService noticeService;

    @Override
    public void handle(Invocation<CommandSender> invocation, InvalidUsage<CommandSender> result, ResultHandlerChain<CommandSender> chain) {
        final CommandSender sender = invocation.sender();
        final Schematic schematic = result.getSchematic();

        sender.sendMessage(NekoChat.translate("<red>Poprawne uzycie:"));

        for (String scheme : schematic.all()) {
            sender.sendMessage(NekoChat.translate("<dark_gray>⏵ <white>Uzycie <dark_gray>- <#9bd0f7>" + scheme));
        }

        if (sender instanceof Player player) {
            this.noticeService.create()
                    .viewer(player)
                    .notice(Notice.builder()
                            .title("","<red>Nie poprawne użycie!")
                            .sound("minecraft:entity.villager.no")
                            .build())
                    .sendAsync();

        }
    }
}