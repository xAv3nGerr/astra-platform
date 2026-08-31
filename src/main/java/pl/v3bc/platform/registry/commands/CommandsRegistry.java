package pl.v3bc.platform.registry.commands;

import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.argument.ArgumentKey;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.LiteBukkitMessages;
import dev.rollczi.litecommands.bukkit.LiteBukkitSettings;
import dev.rollczi.litecommands.context.ContextProvider;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import pl.v3bc.platform.registry.commands.entry.ArgumentEntry;
import pl.v3bc.platform.registry.commands.entry.ContextEntry;
import pl.v3bc.platform.registry.commands.resolvers.InvalidUsageResolver;
import pl.v3bc.platform.registry.commands.resolvers.MissingPermissionResolver;
import pl.v3bc.platform.service.NoticeService;
import pl.v3bc.platform.utils.adventure.NekoChat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class CommandsRegistry {

    private LiteCommands<CommandSender> liteCommands;
    private final NoticeService noticeService;
    private final List<Object> commands = new ArrayList<>();
    private final List<ContextEntry<?>> contexts = new ArrayList<>();
    private final List<ArgumentEntry<?>> arguments = new ArrayList<>();

    public static CommandsRegistry create(NoticeService noticeService) {
        return new CommandsRegistry(noticeService);
    }

    public CommandsRegistry implementCommand(@NonNull Object command) {
        if (!this.commands.contains(command)) {
            this.commands.add(command);
        }
        return this;
    }

    public CommandsRegistry implementCommands(@NonNull Object... commands) {
        for (Object command : commands) {
            this.implementCommand(command);
        }
        return this;
    }

    public CommandsRegistry implementCommands(@NonNull Collection<Object> commands) {
        for (Object command : commands) {
            if (command != null) {
                this.implementCommand(command);
            }
        }
        return this;
    }

    public <T> CommandsRegistry implementArgument(@NonNull Class<T> type, @NonNull ArgumentResolver<CommandSender, T> argument) {
        this.arguments.add(new ArgumentEntry<>(type, null, argument));
        return this;
    }

    public <T> CommandsRegistry implementArgument(@NonNull Class<T> type, @NonNull ArgumentKey key, @NonNull ArgumentResolver<CommandSender, T> argument) {
        this.arguments.add(new ArgumentEntry<>(type, key, argument));
        return this;
    }

    public <T> CommandsRegistry implementContext(@NonNull Class<T> type, @NonNull ContextProvider<CommandSender, T> context) {
        this.contexts.add(new ContextEntry<>(type, context));
        return this;
    }

    public LiteCommands<CommandSender> build(Plugin plugin) {
        LiteCommandsBuilder<CommandSender, LiteBukkitSettings, ?> builder = LiteBukkitFactory.builder("astra", plugin)
                .invalidUsage(new InvalidUsageResolver(this.noticeService))
                .missingPermission(new MissingPermissionResolver(this.noticeService))
                .message(LiteBukkitMessages.PLAYER_NOT_FOUND, (invocation, input) -> NekoChat.component("&cGracz &d" + input + "&c jest offline"))
                .message(LiteBukkitMessages.PLAYER_ONLY, "&cTylko gracz może użyć &4tej komendy!");


        this.contexts.forEach(context -> this.addContext(builder, context));
        this.arguments.forEach(argument -> this.addArgument(builder, argument));
        this.commands.forEach(builder::commands);

        this.liteCommands = builder.build();
        plugin.getLogger().info("Zaladowano " + this.commands.size() + " komend, " + this.arguments.size() + " argumentow oraz " + this.contexts.size() + " kontekstow!");

        return this.liteCommands;
    }

    private <T> void addContext(LiteCommandsBuilder<CommandSender, LiteBukkitSettings, ?> builder, ContextEntry<T> entry) {
        builder.context(entry.getType(), entry.getContext());
    }

    @SuppressWarnings("unchecked")
    private <T> void addArgument(LiteCommandsBuilder<CommandSender, LiteBukkitSettings, ?> builder, ArgumentEntry<T> entry) {
        if (entry.getKey() != null) {
            builder.argument(entry.getType(), entry.getKey(), entry.getArgument());
        } else {
            builder.argument(entry.getType(), entry.getArgument());
        }
    }
}