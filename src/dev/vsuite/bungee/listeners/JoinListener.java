package dev.vsuite.bungee.listeners;

import java.text.SimpleDateFormat;
import java.util.Date;

import dev.vsuite.bungee.base.BaseListener;
import dev.vsuite.bungee.models.Player;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class JoinListener extends BaseListener implements Listener {

    @EventHandler
    public void onLogin(PostLoginEvent e) {
        Player player = Player.get(e.getPlayer());

        if (player.exists()) {
            player.updateLastLogin();
        }
        else {
            player.generate();
        }

        Date date = new Date(player.getFirstLogin());
        player.sendMessage("You first logged in on &e" +
                new SimpleDateFormat("EEEE, d").format(date) +
                getDayOfMonthSuffix(date.getDate()) +
                new SimpleDateFormat(" MMMM, yyyy").format(date) + "&7 at &e" +
                new SimpleDateFormat("hh:mm aaa z").format(date) + "&7.", true);
    }
}
