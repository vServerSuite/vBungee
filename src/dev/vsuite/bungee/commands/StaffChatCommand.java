package dev.vsuite.bungee.commands;

import dev.vsuite.bungee.Main;
import dev.vsuite.bungee.base.BaseCommand;
import dev.vsuite.bungee.utils.Messages;
import dev.vsuite.bungee.utils.Permissions;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class StaffChatCommand extends BaseCommand {

    public StaffChatCommand() {
        super("sc", Permissions.STAFF_CHAT, "staffchat", "staffc", "schat");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof ProxiedPlayer))
            return;

        ProxiedPlayer player = (ProxiedPlayer) commandSender;

        if (args.length == 0) {
            if (Main.getInstance().staffChatToggled.contains(player)) {
                Main.getInstance().staffChatToggled.remove(player);
                sendMessage(player, Messages.get(Messages.STAFF_CHAT_TOGGLE).replaceAll("%status%", "&cdisabled"), true);
            }
            else {
                Main.getInstance().staffChatToggled.add(player);
                sendMessage(player, Messages.get(Messages.STAFF_CHAT_TOGGLE).replaceAll("%status%", "&aenabled"), true);
            }
        }
        else {
            String message = String.join(" &b", args).trim();
            ProxyServer.getInstance().getPlayers().forEach(proxiedPlayer -> {
                if (proxiedPlayer.hasPermission(Permissions.STAFF_CHAT_RECEIVE)) {
                    String messageToSend = Messages.get(Messages.STAFF_CHAT_FORMAT)
                            .replaceAll("%server%", player.getServer().getInfo().getName())
                            .replaceAll("%player%", player.getName())
                            .replaceAll("%message%", message);
                    sendMessage(proxiedPlayer, messageToSend, false);
                }
            });
        }
    }
}
