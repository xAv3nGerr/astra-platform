package pl.v3bc.platform.registry.commands.entry;

import dev.rollczi.litecommands.context.ContextProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.command.CommandSender;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContextEntry<T> {
    private Class<T> type;
    private ContextProvider<CommandSender, T> context;
}