package codes.benh.velocitymc.base;

import codes.benh.velocitymc.Main;
import codes.benh.velocitymc.models.Player;
import codes.benh.velocitymc.utils.Messages;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import static com.google.common.base.Preconditions.checkArgument;

public abstract class BaseCommand extends Command {
    public BaseCommand(String name) {
        super(name);
    }

    public BaseCommand(String name, String permission) {
        super(name, permission);
    }

    public BaseCommand(String name, String permission, String... aliases) {
        super(name, permission, aliases);
    }

    protected void sendMessage(Player player, String message, boolean prefixUsed) {
        TextComponent textComponent = new TextComponent(prefixUsed ? getPrefix() + translateColorCodes(message) : translateColorCodes(message));
        player.getProxiedPlayer().sendMessage(textComponent);
    }

    protected void sendMessage(CommandSender player, String message, boolean prefixUsed) {
        TextComponent textComponent = new TextComponent(prefixUsed ? getPrefix() + translateColorCodes(message) : translateColorCodes(message));
        player.sendMessage(textComponent);
    }

    protected void sendMessage(CommandSender player, TextComponent message) {
        player.sendMessage(message);
    }

    protected String translateColorCodes(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    protected String getPrefix() {
        return translateColorCodes(Messages.get(Messages.PREFIX));
    }

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
