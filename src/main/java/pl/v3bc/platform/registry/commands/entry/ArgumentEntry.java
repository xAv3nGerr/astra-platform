package pl.v3bc.platform.registry.commands.entry;

import dev.rollczi.litecommands.argument.ArgumentKey;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.command.CommandSender;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ArgumentEntry<T> {
    private Class<T> type;
    private ArgumentKey key;
    private ArgumentResolver<CommandSender, T> argument;
}
