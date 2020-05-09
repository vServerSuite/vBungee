package dev.vsuite.bungee.commands.punishments;

import dev.vsuite.bungee.base.BaseCommand;
import dev.vsuite.bungee.models.Player;
import dev.vsuite.bungee.models.punishments.PunishmentType;
import dev.vsuite.bungee.utils.DiscordUtils;
import dev.vsuite.bungee.utils.Messages;
import dev.vsuite.bungee.utils.Permissions;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class KickCommand extends BaseCommand {

    public KickCommand() {
        super("kick", Permissions.KICK, "kickplayer");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (args.length < 2) {
            sendMessage(commandSender, Messages.get(Messages.KICK_INVALID_USAGE), true);
            return;
        }

        Player player = Player.get(args[0]);

        if (player == null || !player.exists() || player.getProxiedPlayer() == null) {
            sendMessage(commandSender, Messages.get(Messages.PLAYER_NOT_FOUND).replace("%player%", args[0]), true);
            return;
        }

        if (player.getProxiedPlayer().hasPermission(Permissions.KICK_EXEMPT)) {
            sendMessage(commandSender, Messages.get(Messages.KICK_EXEMPT).replace("%player%", player.getUsername()), true);
            return;
        }

        args[0] = "";
        String reason = String.join(" ", args);
        player.getProxiedPlayer().disconnect(TextComponent.fromLegacyText(translateColorCodes(Messages.get(Messages.KICK).replace("%staff%", commandSender.getName()).replace("%reason%", reason))));

        BaseComponent[] alertMessage = TextComponent.fromLegacyText(translateColorCodes(Messages.get(Messages.KICK_ALERT)
                .replace("%player%", player.getUsername())
                .replace("%staff%", commandSender.getName())
                .replace("%reason%", reason)));

        ProxyServer.getInstance().getPlayers().stream()
                .filter(proxiedPlayer -> proxiedPlayer.hasPermission(Permissions.KICK_RECEIVE))
                .forEach(proxiedPlayer -> proxiedPlayer.sendMessage(alertMessage));

        String punishmentId = player.punish(PunishmentType.KICK, commandSender, reason);
        DiscordUtils.logPunishment(PunishmentType.KICK, punishmentId, player, commandSender, reason, -1);
    }
}
