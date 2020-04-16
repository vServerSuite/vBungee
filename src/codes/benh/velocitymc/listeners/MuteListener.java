package codes.benh.velocitymc.listeners;

import java.text.SimpleDateFormat;

import codes.benh.velocitymc.Main;
import codes.benh.velocitymc.base.BaseListener;
import codes.benh.velocitymc.models.Player;
import codes.benh.velocitymc.models.punishments.Mute;
import codes.benh.velocitymc.utils.Messages;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class MuteListener extends BaseListener implements Listener {

    @EventHandler
    public void onChat(ChatEvent e) {
        Player player = Player.get(e.getSender());

        if (!e.getMessage().startsWith("/")) {
            if (player.isMuted()) {
                try {
                    Mute mute = player.getMutes().stream().filter(Mute::isActive)
                            .findFirst()
                            .orElseThrow(NullPointerException::new);
                    String messageToSend = Messages.get(Messages.MUTE_JOIN_MESSAGE)
                            .replaceAll("%expiry_date%", mute.getMuteEndDate() == 0 ? "never" : new SimpleDateFormat("dd-MM-yyyy '&7@&e' HH:mm").format(mute.getMuteEndDate()))
                            .replaceAll("%reason%", mute.getReason())
                            .replaceAll("%id%", String.valueOf(mute.getId()));

                    e.setCancelled(true);
                    sendMessage(player, messageToSend, true);
                }
                catch (NullPointerException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
