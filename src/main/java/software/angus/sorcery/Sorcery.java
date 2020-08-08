package software.angus.sorcery;

import org.bukkit.plugin.java.JavaPlugin;

public final class Sorcery extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        System.out.println("Sorcery is starting up...");
        this.getServer().getPluginManager().registerEvents(new Casting(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        System.out.println("Sorcery is shutting down...");
    }
}
