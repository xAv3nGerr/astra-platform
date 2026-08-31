package pl.v3bc.platform.registry.commands.resolvers;

import com.eternalcode.multification.notice.Notice;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.v3bc.platform.service.NoticeService;
import pl.v3bc.platform.utils.TimeUtil;

import java.time.Duration;

@RequiredArgsConstructor
public class DurationArgumentResolver extends ArgumentResolver<CommandSender, Duration> {

    private final NoticeService noticeService;

    @Override
    protected ParseResult<Duration> parse(Invocation<CommandSender> invocation, Argument<Duration> argument, String input) {
        if (input.equalsIgnoreCase("0")) {
            return ParseResult.success(Duration.ZERO);
        }

        try {
            Duration duration = TimeUtil.parseTime(input);

            if (duration != null && !duration.isNegative()) {
                return ParseResult.success(duration);
            }
        } catch (Exception ignored) {}

        CommandSender sender = invocation.sender();
        if (sender instanceof Player player) {
            this.noticeService.create()
                    .viewer(player)
                    .notice(Notice.builder()
                            .title("", "<red>Niepoprawny format czasu!")
                            .sound("minecraft:entity.villager.no")
                            .build())
                    .sendAsync();
        }

        return ParseResult.failure(Component.empty());
    }

    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<Duration> argument, SuggestionContext context) {
        return SuggestionResult.of("0", "1m", "5m", "15m", "30m", "1h", "1d", "7d", "1mo");
    }
}