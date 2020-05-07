package dev.vsuite.bungee.listeners.punishments;

import dev.vsuite.bungee.Main;
import dev.vsuite.bungee.base.BaseListener;
import dev.vsuite.bungee.models.Player;
import dev.vsuite.bungee.models.punishments.Punishment;
import dev.vsuite.bungee.utils.DateUtil;
import dev.vsuite.bungee.utils.Messages;
import io.sentry.Sentry;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerMutedListener extends BaseListener implements Listener {

    @EventHandler
    public void onChat(ChatEvent e) {
        Player player = Player.get(e.getSender());

        if (!e.getMessage().startsWith("/") && player.isMuted()) {
            try {
                Punishment mute = player.getMutes().stream().filter(Punishment::isActive)
                        .findFirst()
                        .orElseThrow(NullPointerException::new);
                String muteMessage = Messages.get(Messages.MUTE_CHAT_MESSAGE)
                        .replaceAll("%reason%", mute.getReason())
                        .replaceAll("%expiry_date%", mute.getDateEnded() == 0 ? "never" : DateUtil.format(mute.getDateEnded()))
                        .replaceAll("%id%", mute.getId());

                e.setCancelled(true);
                sendMessage(player, muteMessage, true);
            }
            catch (NullPointerException ex) {
                if(Main.loggingEnabled()) {
                    Sentry.capture(ex);
                }
            }
        }
    }
}
