package dev.vsuite.bungee.listeners;

import dev.vsuite.bungee.Main;
import dev.vsuite.bungee.base.BaseListener;
import dev.vsuite.bungee.models.Player;
import dev.vsuite.bungee.utils.Messages;
import dev.vsuite.bungee.utils.Permissions;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class StaffChatListener extends BaseListener implements Listener {

    @EventHandler
    public void onChat(ChatEvent e) {
        Player player = Player.get(e.getSender());

        if (!e.getMessage().startsWith("/")) {
            if (Main.getInstance().staffChatToggled.contains(player)) {
                e.setCancelled(true);
                ProxyServer.getInstance().getPlayers().forEach(proxiedPlayers -> {
                    if (proxiedPlayers.hasPermission(Permissions.STAFF_CHAT_RECEIVE)) {
                        String messageToSend = Messages.get(Messages.STAFF_CHAT_FORMAT)
                                .replaceAll("%server%", player.getProxiedPlayer().getServer().getInfo().getName())
                                .replaceAll("%player%", player.getProxiedPlayer().getName())
                                .replaceAll("%message%", e.getMessage());
                        sendMessage(proxiedPlayers, messageToSend, false);
                    }
                });
            }
        }
    }

}