package codes.benh.velocitymc.base;

import codes.benh.velocitymc.Main;
import codes.benh.velocitymc.utils.Messages;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public abstract class BaseCommand extends Command {
    public BaseCommand(String name) {
        super(name);
    }

    public BaseCommand(String name, String permission, String... aliases) {
        super(name, permission, aliases);
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
        return translateColorCodes(Main.getInstance().getConfig().getString(Messages.PREFIX));
    }
}
