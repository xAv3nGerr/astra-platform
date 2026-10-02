package pl.v3bc.platform.service;


import com.eternalcode.multification.adventure.AudienceConverter;
import com.eternalcode.multification.paper.PaperMultification;
import com.eternalcode.multification.translation.TranslationProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import pl.v3bc.platform.utils.text.TextUtil;

public class NoticeService extends PaperMultification<String> {

    @Override
    @NotNull
    protected TranslationProvider<String> translationProvider() {
        return locale -> "";
    }

    @Override
    @NotNull
    protected ComponentSerializer<Component, Component, String> serializer() {
        return TextUtil.MINI_MESSAGE;
    }

    @Override
    @NotNull
    protected AudienceConverter<CommandSender> audienceConverter() {
        return sender -> sender;
    }
}