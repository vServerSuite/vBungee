package codes.benh.velocitymc.commands.punishments.bans;

import codes.benh.velocitymc.Main;
import codes.benh.velocitymc.base.BaseCommand;
import codes.benh.velocitymc.models.Player;
import codes.benh.velocitymc.utils.Messages;
import codes.benh.velocitymc.utils.Permissions;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BanCommand extends BaseCommand {

    public BanCommand() {
        super("ban", Permissions.BAN);
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (args.length < 2) {
            sendMessage(commandSender, Main.getInstance().getConfig().getString(Messages.BAN_INVALID_USAGE), true);
        }
        else {
            ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(args[0]);
            Player player = proxiedPlayer == null ? Player.get(args[0]) : Player.get(proxiedPlayer);
            args[0] = "";
            String reason = String.join(" ", args).trim();
            String banMessage = Main.getInstance().getConfig().getString(Messages.BAN)
                    .replaceAll("%reason%", reason)
                    .replaceAll("%staff%", commandSender.getName());

            if (player != null && player.exists()) {
                player.hasPermission(Permissions.BAN_EXEMPT)
                        .thenAcceptAsync(result -> {
                            System.out.println(result);
                            if (result) {
                                sendMessage(player, Main.getInstance().getConfig().getString(Messages.BAN_EXEMPT).replaceAll("%player%", player.getProxiedPlayer().getName()), true);
                            }
                            else {
                                if (proxiedPlayer != null) {
                                    proxiedPlayer.disconnect(new TextComponent(translateColorCodes(banMessage)));
                                }
                                player.logBan(commandSender, reason);
                                ProxyServer.getInstance().getPlayers().forEach(p -> {
                                    if (p.hasPermission(Permissions.BAN_RECEIVE)) {
                                        String banAlert = Main.getInstance().getConfig().getString(Messages.BAN_ALERT)
                                                .replaceAll("%staff%", commandSender.getName())
                                                .replaceAll("%player%", player.getProxiedPlayer().getName())
                                                .replaceAll("%reason%", reason);
                                        sendMessage(p, banAlert, true);
                                    }
                                });
                            }
                        });
            }
            else {
                sendMessage(commandSender, Main.getInstance().getConfig().getString(Messages.PLAYER_NOT_FOUND).replaceAll("%player%", args[0]), true);
            }
        }
    }
}
