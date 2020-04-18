package codes.benh.velocitymc.listeners;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import codes.benh.velocitymc.Main;
import codes.benh.velocitymc.base.BaseListener;
import codes.benh.velocitymc.models.Player;
import codes.benh.velocitymc.models.punishments.Punishment;
import codes.benh.velocitymc.utils.Messages;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class LoginListener extends BaseListener implements Listener {

    @EventHandler
    public void onLogin(LoginEvent e) {
        Player player = Player.get(e.getConnection().getUniqueId());

        if (player.isBanned()) {

            try {
                List<String> banLines = Main.getInstance().getConfig().getStringList(Messages.BAN_JOIN_MESSAGE);
                List<String> convertedBanLines = new ArrayList<>();
                Punishment ban = player.getBans().stream().filter(Punishment::isActive)
                        .findFirst()
                        .orElseThrow(NullPointerException::new);
                banLines.forEach(s -> convertedBanLines.add(translateColorCodes(s)
                        .replaceAll("%expiry_date%", ban.getDateEnded() == 0 ? "never" : new SimpleDateFormat("dd-MM-yyyy '&7@&e' HH:mm").format(ban.getDateEnded()))
                        .replaceAll("%reason%", ban.getReason())
                        .replaceAll("%id%", String.valueOf(ban.getId()))));

                e.setCancelled(true);
                e.setCancelReason(new ComponentBuilder(String.join("\n", convertedBanLines)).create());
            }
            catch (NullPointerException ex) {
                ex.printStackTrace();
            }
        }
    }
}
