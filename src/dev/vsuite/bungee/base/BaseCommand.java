package dev.vsuite.bungee.base;

import dev.vsuite.bungee.models.Player;
import dev.vsuite.bungee.utils.Messages;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import static com.google.common.base.Preconditions.checkArgument;

public abstract class BaseCommand extends Command {
    /**
     * Creates a command without any permissions or aliases
     *
     * @param name - The command
     */
    public BaseCommand(String name) {
        super(name);
    }

    /**
     * Creates a command without any aliases
     *
     * @param name       - The command
     * @param permission - The permission
     */
    public BaseCommand(String name, String permission) {
        super(name, permission);
    }

    /**
     * Creates a command
     *
     * @param name       - The command
     * @param permission - The permission
     * @param aliases    - The aliases for this command
     */
    public BaseCommand(String name, String permission, String... aliases) {
        super(name, permission, aliases);
    }

    /**
     * Sends a message to a user in-game (with formatting)
     *
     * @param player     - The player to send the message to
     * @param message    - The message to send
     * @param prefixUsed - Whether the prefix should be displayed before the message
     */
    protected void sendMessage(Player player, String message, boolean prefixUsed) {
        BaseComponent[] component = TextComponent.fromLegacyText(prefixUsed ? getPrefix() + translateColorCodes(message) : translateColorCodes(message));
        player.getProxiedPlayer().sendMessage(component);
    }

    /**
     * Sends a message to a user in-game (with formatting)
     *
     * @param player     - The player to send the message to
     * @param message    - The message to send
     * @param prefixUsed - Whether the prefix should be displayed before the message
     */
    protected void sendMessage(CommandSender player, String message, boolean prefixUsed) {
        BaseComponent[] component = TextComponent.fromLegacyText(prefixUsed ? getPrefix() + translateColorCodes(message) : translateColorCodes(message));
        player.sendMessage(component);
    }

    /**
     * Sends a message to a user in-game (with formatting)
     *
     * @param player  - The command sender to send the message to
     * @param message - The message to send
     */
    protected void sendMessage(CommandSender player, TextComponent message) {
        player.sendMessage(message);
    }

    /**
     * Converts the message color codes to Minecraft colour codes
     *
     * @param message - The message to convert
     * @return A converted string
     */
    protected String translateColorCodes(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * The prefix for the plugin
     *
     * @return The prefix set in the messages.yml
     */
    protected String getPrefix() {
        return translateColorCodes(Messages.get(Messages.PREFIX));
    }

    /**
     * Gets the suffix for the day of the month
     *
     * @param n - The day of the month
     * @return The suffix for the day of the month
     */
    protected String getDayOfMonthSuffix(final int n) {
        checkArgument(n >= 1 && n <= 31, "illegal day of month: " + n);
        if (n >= 11 && n <= 13) {
            return "th";
        }
        switch (n % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }
}
