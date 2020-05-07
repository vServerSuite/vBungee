package dev.vsuite.bungee.base.punishments;

import dev.vsuite.bungee.base.BaseCommand;
import dev.vsuite.bungee.managers.PunishmentManager;
import dev.vsuite.bungee.models.Player;
import dev.vsuite.bungee.models.punishments.Punishment;
import dev.vsuite.bungee.models.punishments.PunishmentType;
import dev.vsuite.bungee.utils.Messages;
import dev.vsuite.bungee.utils.Permissions;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;

public abstract class BaseReversePunishmentCommand extends BaseCommand {

    private final PunishmentType type;
    private final PunishmentManager punishmentManager;

    public BaseReversePunishmentCommand(PunishmentType type, String name, String permission, String... aliases) {
        super(name, permission, aliases);
        this.type = type;
        this.punishmentManager = new PunishmentManager();
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if ((type == PunishmentType.BAN && !commandSender.hasPermission(Permissions.UNBAN)) || (type == PunishmentType.MUTE && !commandSender.hasPermission(Permissions.UNMUTE))) {
            sendMessage(commandSender, "You do not have access to this command", true);
            return;
        }

        if (args.length == 0) {
            sendMessage(commandSender, Messages.get(type == PunishmentType.BAN ? Messages.UNBAN_INVALID_USAGE : Messages.UNMUTE_INVALID_USAGE), true);
            return;
        }

        Player target = Player.get(args[0]);

        if (target == null || !target.exists()) {
            sendMessage(commandSender, Messages.get(Messages.PLAYER_NOT_FOUND).replace("%player%", args[0]), true);
            return;
        }

        if ((type == PunishmentType.BAN && !target.isBanned()) || (type == PunishmentType.MUTE && !target.isMuted())) {
            sendMessage(commandSender, Messages.get(type == PunishmentType.BAN ? Messages.UNBAN_NOT_BANNED : Messages.UNMUTE_NOT_MUTED).replace("%player%", target.getUsername()), true);
            return;
        }

        Punishment punishment;

        if (type == PunishmentType.BAN) {
            punishment = target.getBans().stream().filter(Punishment::isActive).findFirst().orElseThrow(NullPointerException::new);
        }
        else {
            punishment = target.getMutes().stream().filter(Punishment::isActive).findFirst().orElseThrow(NullPointerException::new);
        }

        punishment.setActive(false);
        punishment.setDateEnded(System.currentTimeMillis());
        boolean punishmentUpdated = punishmentManager.update(punishment);

        if (punishmentUpdated) {
            ProxyServer.getInstance().getPlayers().stream()
                    .filter(proxiedPlayer -> proxiedPlayer.hasPermission(type == PunishmentType.BAN ? Permissions.UNBAN_RECEIVE : Permissions.UNMUTE_RECEIVE))
                    .forEach(proxiedPlayer -> sendMessage(proxiedPlayer, Messages.get(type == PunishmentType.BAN ? Messages.UNBAN_ALERT : Messages.UNMUTE_ALERT).replace("%player%", target.getUsername()).replace("%staff%", commandSender.getName()), true));
            if (target.getProxiedPlayer() != null && type == PunishmentType.MUTE) {
                sendMessage(target, Messages.UNMUTE_PLAYER_NOTIFICATION, true);
            }
        }
        else {
            sendMessage(commandSender, "An error occurred whilst dispatching this command. Please report this to the vSuite Team.", true);
        }
    }
}
