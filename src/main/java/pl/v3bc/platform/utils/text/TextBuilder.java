package pl.v3bc.platform.utils.text;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.stream.Collectors;

public final class TextBuilder {
    private final List<String> text = new ArrayList<>();
    private final Map<String, Object> placeholders = new HashMap<>();

    public static TextBuilder builder() {
        return new TextBuilder();
    }

    public TextBuilder text(final String message) {
        text.add(message);
        return this;
    }

    public TextBuilder text(final List<String> message) {
        text.addAll(message);
        return this;
    }

    public TextBuilder text(final String... message) {
        text.addAll(List.of(message));
        return this;
    }

    public TextBuilder placeholder(final String from, final Object to) {
        final String formattedKey = (from.startsWith("{") && from.endsWith("}")) ? from : "{" + from + "}";
        if (to == null) {
            this.placeholders.put(formattedKey, formattedKey + "=null");
            return this;
        }
        this.placeholders.put(formattedKey, to);
        return this;
    }

    public List<String> build() {
        if (!this.placeholders.isEmpty()) {
            final List<String> replacedMessages = new ArrayList<>();
            for (final String message : text) {
                String messageToReplace = message;
                for (final Map.Entry<String, Object> entry : this.placeholders.entrySet()) {
                    final Object value = entry.getValue();
                    final String key = entry.getKey();
                    messageToReplace = messageToReplace.replace(key, value.toString());
                }
                replacedMessages.add(messageToReplace);
            }
            return replacedMessages;
        }
        return text;
    }

    public List<Component> buildAsComponents() {
        return build().stream().map(TextUtil::parse).collect(Collectors.toList());
    }

    public void sendLegacy(final CommandSender commandSender) {
        send(Collections.singletonList(commandSender), true);
    }

    public void send(final CommandSender commandSender) {
        send(Collections.singletonList(commandSender), false);
    }

    public void send(final CommandSender commandSender, final boolean legacy) {
        send(Collections.singletonList(commandSender), legacy);
    }

    public void send(final Collection<CommandSender> receivers, final boolean legacy) {
        final List<String> messages = build();
        if (receivers.isEmpty() || messages.isEmpty()) {
            return;
        }
        for (final CommandSender commandSender : receivers) {
            for (final String message : messages) {
                if (legacy) {
                    commandSender.sendMessage(TextUtil.legacyColor(message));
                    continue;
                }
                commandSender.sendMessage(TextUtil.parse(message));
            }
        }
    }

    public String firstLine() {
        return build().getFirst();
    }
}

