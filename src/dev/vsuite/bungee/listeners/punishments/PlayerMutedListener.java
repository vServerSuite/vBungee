package dev.vsuite.bungee.listeners.punishments;

import java.text.SimpleDateFormat;

import dev.vsuite.bungee.base.BaseListener;
import dev.vsuite.bungee.models.Player;
import dev.vsuite.bungee.models.punishments.Punishment;
import dev.vsuite.bungee.utils.Messages;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerMutedListener extends BaseListener implements Listener {

    @EventHandler
    public void onChat(ChatEvent e) {
        Player player = Player.get(e.getSender());

        if (!e.getMessage().startsWith("/")) {
            if (player.isMuted()) {
                try {
                    Punishment mute = player.getMutes().stream().filter(Punishment::isActive)
                            .findFirst()
                            .orElseThrow(NullPointerException::new);
                    String muteMessage = Messages.get(Messages.MUTE_CHAT_MESSAGE)
                            .replaceAll("%reason%", mute.getReason())
                            .replaceAll("%expiry_date%", mute.getDateEnded() == 0 ? "never" : new SimpleDateFormat("dd-MM-yyyy '&7@&e' HH:mm").format(mute.getDateEnded()))
                            .replaceAll("%id%", mute.getId());

                    e.setCancelled(true);
                    sendMessage(player, muteMessage, true);
                }
                catch (NullPointerException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
