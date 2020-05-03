package codes.benh.velocitymc.listeners.punishments;

import java.text.SimpleDateFormat;

import codes.benh.velocitymc.base.BaseListener;
import codes.benh.velocitymc.models.Player;
import codes.benh.velocitymc.models.punishments.Punishment;
import codes.benh.velocitymc.utils.Messages;
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
