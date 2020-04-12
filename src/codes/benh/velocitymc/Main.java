package codes.benh.velocitymc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import codes.benh.velocitymc.commands.BungeeStatsCommand;
import codes.benh.velocitymc.commands.LobbyCommand;
import codes.benh.velocitymc.commands.ReportCommand;
import codes.benh.velocitymc.commands.StaffChatCommand;
import codes.benh.velocitymc.listeners.StaffChatListener;
import codes.benh.velocitymc.runnables.TpsRunnable;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class Main extends Plugin {

    private static Main main;

    public List<ProxiedPlayer> staffChatToggled = new ArrayList<>();

    public static Main getInstance() {
        return main;
    }

    public void onEnable() {
        this.main = this;

        PluginManager pluginManager = getProxy().getPluginManager();

        registerCommands(pluginManager);
        registerListeners(pluginManager);
        registerSchedulers(getProxy().getScheduler());
        saveDefaultConfig();
    }

    private void registerCommands(PluginManager pluginManager) {
        pluginManager.registerCommand(this, new BungeeStatsCommand());
        pluginManager.registerCommand(this, new LobbyCommand());
        pluginManager.registerCommand(this, new ReportCommand());
        pluginManager.registerCommand(this, new StaffChatCommand());
    }

    private void registerListeners(PluginManager pluginManager) {
        pluginManager.registerListener(this, new StaffChatListener());
    }

    private void registerSchedulers(TaskScheduler taskScheduler) {
        taskScheduler.schedule(main, new TpsRunnable(), 1000, 50, TimeUnit.MILLISECONDS);
    }

    /**
     * Gets the configuration from config.yml
     *
     * @return - The Configuration file
     */
    public Configuration getConfig() {
        Configuration config = null;
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class)
                    .load(new File(Main.getInstance().getDataFolder(), "config.yml"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return config;
    }

    /**
     * Saves the default configuration from the template in the plugin
     */
    public void saveDefaultConfig() {
        if (!Main.getInstance().getDataFolder().exists()) {
            Main.getInstance().getDataFolder().mkdir();
        }

        File configFile = new File(Main.getInstance().getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                try (InputStream is = Main.getInstance().getResourceAsStream("config.yml");
                     OutputStream os = new FileOutputStream(configFile)) {
                    ByteStreams.copy(is, os);
                }
            }
            catch (IOException e) {
                throw new RuntimeException("Unable to create configuration file", e);
            }
        }
    }
}
