package pl.v3bc.platform;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;
    }

    public static Main getInstance() {
        return instance;
    }
}