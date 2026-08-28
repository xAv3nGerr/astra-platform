package pl.v3bc.platform.registry.configs;

import com.eternalcode.multification.notice.resolver.NoticeResolverRegistry;
import com.eternalcode.multification.okaeri.MultificationNoticeSerializer;
import com.eternalcode.multification.okaeri.MultificationSerdesPack;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.serdes.commons.SerdesCommons;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import eu.okaeri.configs.yaml.bukkit.serdes.SerdesBukkit;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class ConfigRegistry {
    private final Set<OkaeriConfig> configs = new HashSet<>();
    private final NoticeResolverRegistry noticeRegistry;

    public void reload() {
        this.configs.forEach(OkaeriConfig::load);
    }

    public <T extends OkaeriConfig> T create(Class<T> clazz, File file) {
        T configFile = ConfigManager.create(clazz, it -> {
            it.withConfigurer(new YamlBukkitConfigurer(), new SerdesBukkit(), new MultificationSerdesPack(noticeRegistry));
            it.withBindFile(file);
            it.withRemoveOrphans(true);
            it.saveDefaults();
            it.load(true);
        });
        this.configs.add(configFile);
        return configFile;
    }

    public <T extends OkaeriConfig> T register(Class<T> clazz, Plugin plugin, String fileName) {
        return this.create(clazz, new File(plugin.getDataFolder(), fileName));
    }
}
