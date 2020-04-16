package codes.benh.velocitymc.commands.punishments;

import codes.benh.velocitymc.Main;
import codes.benh.velocitymc.base.BaseCommand;
import codes.benh.velocitymc.models.Player;
import codes.benh.velocitymc.utils.Messages;
import codes.benh.velocitymc.utils.Permissions;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;

public class KickCommand extends BaseCommand {

    public KickCommand() {
        super("kick", Permissions.KICK);
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (args.length < 2) {
            sendMessage(commandSender, Messages.get(Messages.KICK_INVALID_USAGE), true);
        }
        else {
            if (ProxyServer.getInstance().getPlayer(args[0]) == null) {
                sendMessage(commandSender, Messages.get(Messages.PLAYER_NOT_FOUND).replaceAll("%player%", args[0]), true);
            }
            else {
                Player player = Player.get(ProxyServer.getInstance().getPlayer(args[0]));
                if (player.getProxiedPlayer().hasPermission(Permissions.KICK_EXEMPT)) {
                    sendMessage(player, Messages.get(Messages.KICK_EXEMPT).replaceAll("%player%", player.getProxiedPlayer().getName()), true);
                }
                else {
                    args[0] = "";
                    String reason = String.join(" ", args).trim();
                    player.logKick(commandSender, reason);

                    String kickMessage = Messages.get(Messages.KICK)
                            .replaceAll("%reason%", reason)
                            .replaceAll("%staff%", commandSender.getName());
                    player.getProxiedPlayer().disconnect(new TextComponent(ChatColor.translateAlternateColorCodes('&', kickMessage)));

                    ProxyServer.getInstance().getPlayers().forEach(p -> {
                        if (p.hasPermission(Permissions.KICK_RECEIVE)) {
                            String kickAlert = Messages.get(Messages.KICK_ALERT)
                                    .replaceAll("%staff%", commandSender.getName())
                                    .replaceAll("%player%", player.getProxiedPlayer().getName())
                                    .replaceAll("%reason%", reason);
                            sendMessage(p, kickAlert, true);
                        }
                    });
                }
            }
        }
    }
}
