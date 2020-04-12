package codes.benh.velocitymc.listeners;

import codes.benh.velocitymc.Main;
import codes.benh.velocitymc.base.BaseListener;
import codes.benh.velocitymc.utils.Messages;
import codes.benh.velocitymc.utils.Permissions;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class StaffChatListener extends BaseListener implements Listener {

    @EventHandler
    public void onChat(ChatEvent e) {
        ProxiedPlayer player = (ProxiedPlayer) e.getSender();

        if (!e.getMessage().startsWith("/")) {
            if (Main.getInstance().staffChatToggled.contains(player)) {
                e.setCancelled(true);
                ProxyServer.getInstance().getPlayers().forEach(proxiedPlayer -> {
                    if (proxiedPlayer.hasPermission(Permissions.STAFF_CHAT_RECEIVE)) {
                        String messageToSend = Main.getInstance().getConfig().getString(Messages.STAFF_CHAT_FORMAT)
                                .replaceAll("%server%", player.getServer().getInfo().getName())
                                .replaceAll("%player%", player.getName())
                                .replaceAll("%message%", e.getMessage());
                        sendMessage(proxiedPlayer, messageToSend, false);
                    }
                });
            }
        }
    }

}
