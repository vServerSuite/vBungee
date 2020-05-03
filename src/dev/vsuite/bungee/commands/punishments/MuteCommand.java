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
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class MuteCommand extends BaseCommand {

    public MuteCommand() {
        super("mute", null, "tempmute", "muteplayer", "temporarymute");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (commandSender.hasPermission(Permissions.BAN) || commandSender.hasPermission(Permissions.MUTE_TEMPORARY)) {
            if (args.length < 2) {
                sendMessage(commandSender, Messages.get(Messages.MUTE_INVALID_USAGE), true);
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
                    sendMessage(commandSender, Messages.get(Messages.MUTE_INVALID_USAGE), true);
                }
                else {
                    if (expiresCheck == -1 && !commandSender.hasPermission(Permissions.MUTE)) {
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
                        String muteMessage = Messages.get(Messages.MUTE)
                                .replaceAll("%reason%", reason)
                                .replaceAll("%staff%", commandSender.getName())
                                .replaceAll("%expiry_date%", expiresCheck == -1 ? "never" : new SimpleDateFormat("dd-MM-yyyy '&7@&e' HH:mm:ss").format(new Date(expiresCheck)));

                        if (player != null && player.exists()) {
                            long finalExpiresCheck = expiresCheck;
                            player.hasPermission(Permissions.MUTE_EXEMPT)
                                    .thenAcceptAsync(result -> {
                                        if (result) {
                                            sendMessage(commandSender, Messages.get(Messages.MUTE_EXEMPT).replaceAll("%player%", player.getUsername()), true);
                                        }
                                        else {
                                            if (proxiedPlayer != null) {
                                                sendMessage(proxiedPlayer, muteMessage, true);
                                            }
                                            String newMuteId = player.logPunishment(PunishmentType.MUTE, commandSender, reason, finalExpiresCheck);

                                            DiscordUtils.logPunishment(PunishmentType.MUTE, newMuteId, player, commandSender instanceof ProxiedPlayer ? Player.get(commandSender) : commandSender, reason, finalExpiresCheck);
                                            ProxyServer.getInstance().getPlayers().forEach(p -> {
                                                if (p.hasPermission(Permissions.MUTE_RECEIVE)) {
                                                    String muteAlert = Messages.get(Messages.MUTE_ALERT)
                                                            .replaceAll("%staff%", commandSender.getName())
                                                            .replaceAll("%player%", player.getUsername())
                                                            .replaceAll("%reason%", reason)
                                                            .replaceAll("%expiry_date%", finalExpiresCheck == -1 ? "never" : new SimpleDateFormat("dd-MM-yyyy '&7@&e' HH:mm:ss").format(new Date(finalExpiresCheck)));
                                                    sendMessage(p, muteAlert, true);
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