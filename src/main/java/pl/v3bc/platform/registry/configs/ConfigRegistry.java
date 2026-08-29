package pl.v3bc.platform.registry.configs;

import com.eternalcode.multification.notice.resolver.NoticeResolverRegistry;
import com.eternalcode.multification.okaeri.MultificationSerdesPack;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import eu.okaeri.configs.yaml.bukkit.serdes.SerdesBukkit;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.Plugin;
import pl.v3bc.platform.registry.configs.serdes.ItemsSerdesPack;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor
public class ConfigRegistry {

    private final Map<OkaeriConfig, File> configs = new LinkedHashMap<>();
    private final NoticeResolverRegistry noticeRegistry;

    public void reload() {
        this.configs.forEach((config, file) -> {
            config.load();
        });
    }

    public <T extends OkaeriConfig> T create(Class<T> clazz, File file) {
        T configFile = ConfigManager.create(clazz, it -> {
            it.withConfigurer(
                    new DoubleQuotedYamlConfigurer(),
                    new SerdesBukkit(),
                    new MultificationSerdesPack(this.noticeRegistry),
                    new ItemsSerdesPack()
            );
            it.withBindFile(file);
            it.withRemoveOrphans(true);
            it.saveDefaults();
            it.load(true);
        });

        this.configs.put(configFile, file);
        return configFile;
    }

    public <T extends OkaeriConfig> T register(Class<T> clazz, Plugin plugin, String fileName) {
        return this.create(clazz, new File(plugin.getDataFolder(), fileName));
    }
}