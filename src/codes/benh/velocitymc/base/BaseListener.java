package codes.benh.velocitymc.base;

import codes.benh.velocitymc.Main;
import codes.benh.velocitymc.utils.Messages;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public abstract class BaseListener {
    protected void sendMessage(ProxiedPlayer player, String message, boolean prefixUsed) {
        TextComponent textComponent = new TextComponent(prefixUsed ? getPrefix() + translateColorCodes(message) : translateColorCodes(message));
        player.sendMessage(textComponent);
    }

    protected String translateColorCodes(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    protected String getPrefix() {
        return translateColorCodes(Main.getInstance().getConfig().getString(Messages.PREFIX));
    }
}
