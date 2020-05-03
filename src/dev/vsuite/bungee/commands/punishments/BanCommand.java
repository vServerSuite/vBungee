package dev.vsuite.bungee.commands.punishments;

import java.text.SimpleDateFormat;
import java.util.Date;

import dev.vsuite.bungee.base.BaseCommand;
import dev.vsuite.bungee.models.Player;
import dev.vsuite.bungee.models.punishments.PunishmentType;
import dev.vsuite.bungee.utils.DateUtil;
import dev.vsuite.bungee.utils.DiscordUtils;
import dev.vsuite.bungee.utils.Messages;
import dev.vsuite.bungee.utils.Permissions;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BanCommand extends BaseCommand {

    public BanCommand() {
        super("ban", null, "tempban", "banplayer", "temporaryban");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (commandSender.hasPermission(Permissions.BAN) || commandSender.hasPermission(Permissions.BAN_TEMPORARY)) {
            if (args.length < 2) {
                sendMessage(commandSender, Messages.get(Messages.BAN_INVALID_USAGE), true);
            }
            else {
                long expiresCheck = -1;
                try {
                    expiresCheck = DateUtil.parseDateDiff(args[1], true);
                }
                catch (Exception e) {
                    System.out.println("vSuite > Parse Date Error: " + e.getMessage());
                }

                if (expiresCheck != -1 && args.length == 2) {
                    sendMessage(commandSender, Messages.get(Messages.BAN_INVALID_USAGE), true);
                }
                else {
                    if (expiresCheck == -1 && !commandSender.hasPermission(Permissions.BAN)) {
                        sendMessage(commandSender, "You do not have access to that command", true);
                    }
                    else {
                        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(args[0]);
                        Player player = proxiedPlayer == null ? Player.get(args[0]) : Player.get(proxiedPlayer);
                        args[0] = "";
                        if (expiresCheck != -1) {
                            args[1] = "";
                        }
                        String reason = String.join(" ", args).trim();
                        String banMessage = Messages.get(Messages.BAN)
                                .replaceAll("%reason%", reason)
                                .replaceAll("%staff%", commandSender.getName())
                                .replaceAll("%expiry_date%", expiresCheck == -1 ? "never" : new SimpleDateFormat("dd-MM-yyyy '&7@&e' HH:mm:ss").format(new Date(expiresCheck)));

                        if (player != null && player.exists()) {
                            long finalExpiresCheck = expiresCheck;
                            player.hasPermission(Permissions.BAN_EXEMPT)
                                    .thenAcceptAsync(result -> {
                                        if (result) {
                                            sendMessage(commandSender, Messages.get(Messages.BAN_EXEMPT).replaceAll("%player%", player.getUsername()), true);
                                        }
                                        else {
                                            if (proxiedPlayer != null) {
                                                proxiedPlayer.disconnect(new TextComponent(translateColorCodes(banMessage)));
                                            }
                                            String newBanId = player.logPunishment(PunishmentType.BAN, commandSender, reason, finalExpiresCheck);

                                            DiscordUtils.logPunishment(PunishmentType.BAN, newBanId, player, commandSender instanceof ProxiedPlayer ? Player.get(commandSender) : commandSender, reason, finalExpiresCheck);
                                            ProxyServer.getInstance().getPlayers().forEach(p -> {
                                                if (p.hasPermission(Permissions.BAN_RECEIVE)) {
                                                    String banAlert = Messages.get(Messages.BAN_ALERT)
                                                            .replaceAll("%staff%", commandSender.getName())
                                                            .replaceAll("%player%", player.getUsername())
                                                            .replaceAll("%reason%", reason)
                                                            .replaceAll("%expiry_date%", finalExpiresCheck == -1 ? "never" : new SimpleDateFormat("dd-MM-yyyy '&7@&e' HH:mm:ss").format(new Date(finalExpiresCheck)));
                                                    sendMessage(p, banAlert, true);
                                                }
                                            });
                                        }
                                    });
                        }
                        else {
                            sendMessage(commandSender, Messages.get(Messages.PLAYER_NOT_FOUND).replaceAll("%player%", args[0]), true);
                        }
                    }
                }
            }
        }
        else {
            sendMessage(commandSender, "You do not have access to that command", true);
        }
    }
}