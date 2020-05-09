package dev.vsuite.bungee.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

import com.google.common.io.ByteStreams;
import dev.vsuite.bungee.Main;
import io.sentry.Sentry;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class Messages {

    // Messages
    public static final String PREFIX = "Prefix";
    // Prefixes
    public static final String PLAYER_NOT_FOUND = "PlayerNotFound";

    public static final String STAFF_CHAT_FORMAT = "StaffChat.Format";
    public static final String STAFF_CHAT_TOGGLE = "StaffChat.Toggle";

    public static final String REPORT_CONFIRMATION = "Report.Confirmation";
    public static final String REPORT_FORMAT = "Report.Format";
    public static final String REPORT_FORMAT_HOVER = "Report.FormatHover";
    public static final String REPORT_INVALID_USAGE = "Report.Usage";

    public static final String SERVER_CONNECT = "Server.Connect";
    public static final String SERVER_NOT_FOUND = "Server.NotFound";

    public static final String PUNISHMENTS_PREFIX = "Punishments.";

    public static final String BAN = PUNISHMENTS_PREFIX + "Ban.Message";
    public static final String BAN_ALERT = PUNISHMENTS_PREFIX + "Ban.Alert";
    public static final String BAN_EXEMPT = PUNISHMENTS_PREFIX + "Ban.Exempt";
    public static final String BAN_INVALID_USAGE = PUNISHMENTS_PREFIX + "Ban.Usage";
    public static final String BAN_JOIN_MESSAGE = PUNISHMENTS_PREFIX + "Ban.Login";

    public static final String UNBAN_ALERT = PUNISHMENTS_PREFIX + "Unban.Alert";
    public static final String UNBAN_INVALID_USAGE = PUNISHMENTS_PREFIX + "Unban.Usage";
    public static final String UNBAN_NOT_BANNED = PUNISHMENTS_PREFIX + "Unban.NotBanned";

    public static final String MUTE = PUNISHMENTS_PREFIX + "Mute.Message";
    public static final String MUTE_ALERT = PUNISHMENTS_PREFIX + "Mute.Alert";
    public static final String MUTE_CHAT_MESSAGE = PUNISHMENTS_PREFIX + "Mute.Chat";
    public static final String MUTE_EXEMPT = PUNISHMENTS_PREFIX + "Mute.Exempt";
    public static final String MUTE_INVALID_USAGE = PUNISHMENTS_PREFIX + "Mute.Usage";

    public static final String UNMUTE_ALERT = PUNISHMENTS_PREFIX + "Unmute.Alert";
    public static final String UNMUTE_NOT_MUTED = PUNISHMENTS_PREFIX + "Unmute.NotMuted";
    public static final String UNMUTE_INVALID_USAGE = PUNISHMENTS_PREFIX + "Unmute.Usage";
    public static final String UNMUTE_PLAYER_NOTIFICATION = PUNISHMENTS_PREFIX + "Unmute.Notification";

    public static final String KICK = PUNISHMENTS_PREFIX + "Kick.Message";
    public static final String KICK_ALERT = PUNISHMENTS_PREFIX + "Kick.Alert";
    public static final String KICK_EXEMPT = PUNISHMENTS_PREFIX + "Kick.Exempt";
    public static final String KICK_INVALID_USAGE = PUNISHMENTS_PREFIX + "Kick.Usage";

    public static final String DISCORD_LINK_NOTIFICATION = "Discord.Link.Notification";
    public static final String DISCORD_LINK_CONFIRMATION = "Discord.Link.Confirmation";


    public static String get(String message) {
        return getMessages().getString(message);
    }

    /**
     * Gets the configuration from config.yml
     *
     * @return - The Configuration file
     */
    private static Configuration getMessages() {
        Configuration config = null;
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class)
                    .load(new File(Main.getInstance().getDataFolder(), "lang/messages_" + Main.getInstance().getConfig().getString("Lang") + ".yml"));
        }
        catch (IOException e) {
            if (Main.loggingEnabled()) {
                Sentry.capture(e);
            }
        }

        return config;
    }

    /**
     * Saves the default configuration from the template in the plugin
     */
    public static void saveDefaultMessages() {
        if (!Main.getInstance().getDataFolder().exists()) {
            Main.getInstance().getDataFolder().mkdir();
        }

        File messagesFolder =  new File(ProxyServer.getInstance().getPluginsFolder(), Main.getInstance().getDescription().getName() + "/lang");

        if(!messagesFolder.exists()) {
            boolean success = messagesFolder.mkdir();
        }

        File messagesFile = new File(Main.getInstance().getDataFolder(), "lang/messages_" + Main.getInstance().getConfig().getString("Lang") + ".yml");
        if (!messagesFile.exists()) {
            try {
                messagesFile.createNewFile();
                try (InputStream is = Main.getInstance().getResourceAsStream("messages_" + Main.getInstance().getConfig().getString("Lang") + ".yml");
                     OutputStream os = new FileOutputStream(messagesFile)) {
                    ByteStreams.copy(is, os);
                }
            }
            catch (IOException e) {
                if (Main.loggingEnabled()) {
                    Sentry.capture(e);
                }
            }
        }
    }
}
