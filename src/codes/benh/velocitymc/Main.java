package codes.benh.velocitymc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import codes.benh.velocitymc.commands.BungeeStatsCommand;
import codes.benh.velocitymc.commands.LobbyCommand;
import codes.benh.velocitymc.commands.ReportCommand;
import codes.benh.velocitymc.commands.StaffChatCommand;
import codes.benh.velocitymc.commands.punishments.BanCommand;
import codes.benh.velocitymc.commands.punishments.KickCommand;
import codes.benh.velocitymc.commands.punishments.MuteCommand;
import codes.benh.velocitymc.commands.punishments.core.LookupCommand;
import codes.benh.velocitymc.helpers.DbHelper;
import codes.benh.velocitymc.listeners.JoinListener;
import codes.benh.velocitymc.listeners.LoginListener;
import codes.benh.velocitymc.listeners.MuteListener;
import codes.benh.velocitymc.listeners.StaffChatListener;
import codes.benh.velocitymc.runnables.TpsRunnable;
import codes.benh.velocitymc.utils.Messages;
import com.google.common.io.ByteStreams;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import pro.husk.mysql.MySQL;

public class Main extends Plugin {

    private static Main main;
    private static MySQL mySQL;
    private static LuckPerms api;

    public List<ProxiedPlayer> staffChatToggled = new ArrayList<>();

    public static Main getInstance() {
        return main;
    }

    public static MySQL getMySQL() {
        return mySQL;
    }

    public static LuckPerms getLuckPermsAPI() {
        return api;
    }

    public void onEnable() {
        main = this;
        api = LuckPermsProvider.get();

        PluginManager pluginManager = getProxy().getPluginManager();

        registerCommands(pluginManager);
        registerListeners(pluginManager);
        registerSchedulers(getProxy().getScheduler());

        saveDefaultConfig();
        Messages.saveDefaultMessages();

        mySQL = new MySQL(
                getConfig().getString("Database.Host"),
                getConfig().getString("Database.Port"),
                getConfig().getString("Database.Database"),
                getConfig().getString("Database.Username"),
                getConfig().getString("Database.Password"),
                getConfig().getString("Database.Params"));

        DbHelper.initialise();

        parseLogDeletion();
    }

    private void parseLogDeletion() {
        System.out.println("vSuite > Clearing up /logs/");
        File dirPath = new File("./logs");
        if (dirPath.isDirectory()) {
            String dateString = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            String logExtension = ".log.gz";
            Arrays.stream(Objects.requireNonNull(dirPath.listFiles())).filter(file -> !file.getName().contains(dateString) && file.getName().contains(logExtension)).forEach(this::deleteFile);
        }
        System.out.println("vSuite > Log Files Cleared");
    }

    private void deleteFile(File file) {
        boolean deleted = file.delete();
        System.out.println("vSuite > Log File Removed (Deleted? " + deleted + ")");
    }

    private void registerCommands(PluginManager pluginManager) {
        pluginManager.registerCommand(this, new BanCommand());
        pluginManager.registerCommand(this, new BungeeStatsCommand());
        pluginManager.registerCommand(this, new KickCommand());
        pluginManager.registerCommand(this, new LobbyCommand());
        pluginManager.registerCommand(this, new LookupCommand());
        pluginManager.registerCommand(this, new MuteCommand());
        pluginManager.registerCommand(this, new ReportCommand());
        pluginManager.registerCommand(this, new StaffChatCommand());
    }

    private void registerListeners(PluginManager pluginManager) {
        pluginManager.registerListener(this, new MuteListener());
        pluginManager.registerListener(this, new StaffChatListener());
        pluginManager.registerListener(this, new JoinListener());
        pluginManager.registerListener(this, new LoginListener());
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

