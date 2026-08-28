package pl.v3bc.platform.service;


import com.eternalcode.multification.adventure.AudienceConverter;
import com.eternalcode.multification.paper.PaperMultification;
import com.eternalcode.multification.translation.TranslationProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class NoticeService extends PaperMultification<String> {
    private final MiniMessage miniMessage;

    public NoticeService(MiniMessage miniMessage) {
        this.miniMessage = miniMessage;
    }

    @Override
    @NotNull
    protected TranslationProvider<String> translationProvider() {
        return locale -> "";
    }

    @Override
    @NotNull
    protected ComponentSerializer<Component, Component, String> serializer() {
        return this.miniMessage;
    }

    @Override
    @NotNull
    protected AudienceConverter<CommandSender> audienceConverter() {
        return sender -> sender;
    }
}