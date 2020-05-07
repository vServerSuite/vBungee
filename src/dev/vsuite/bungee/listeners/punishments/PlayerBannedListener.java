package dev.vsuite.bungee.listeners.punishments;

import java.util.ArrayList;
import java.util.List;

import dev.vsuite.bungee.Main;
import dev.vsuite.bungee.base.BaseListener;
import dev.vsuite.bungee.models.Player;
import dev.vsuite.bungee.models.punishments.Punishment;
import dev.vsuite.bungee.utils.DateUtil;
import dev.vsuite.bungee.utils.Messages;
import io.sentry.Sentry;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerBannedListener extends BaseListener implements Listener {

    @EventHandler
    public void onLogin(final LoginEvent e) {
        e.registerIntent(Main.getInstance());
        ProxyServer.getInstance().getScheduler().runAsync(Main.getInstance(), () -> {
            Player player = Player.get(e.getConnection().getUniqueId());

            if (player.isBanned()) {
                try {
                    List<String> banLines = Main.getInstance().getConfig().getStringList(Messages.BAN_JOIN_MESSAGE);
                    List<String> convertedBanLines = new ArrayList<>();
                    Punishment ban = player.getBans().stream().filter(Punishment::isActive)
                            .findFirst()
                            .orElseThrow(NullPointerException::new);
                    banLines.forEach(s -> convertedBanLines.add(translateColorCodes(s)
                            .replaceAll("%expiry_date%", ban.getDateEnded() == 0 ? "never" : DateUtil.format(ban.getDateEnded()))
                            .replaceAll("%reason%", ban.getReason())
                            .replaceAll("%id%", String.valueOf(ban.getId()))));
                    BaseComponent[] message = TextComponent.fromLegacyText(String.join("\n", convertedBanLines));
                    e.setCancelReason(message);
                    e.setCancelled(true);
                }
                catch (NullPointerException ex) {
                    if(Main.loggingEnabled()) {
                        Sentry.capture(ex);
                    }
                }
            }
        });
        e.completeIntent(Main.getInstance());
    }
}
