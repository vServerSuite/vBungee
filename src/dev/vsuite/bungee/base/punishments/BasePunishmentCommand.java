package dev.vsuite.bungee.base.punishments;

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

public abstract class BasePunishmentCommand extends BaseCommand {

    private final PunishmentType type;

    public BasePunishmentCommand(PunishmentType type, String name, String permission, String... aliases) {
        super(name, permission, aliases);
        this.type = type;
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (type == PunishmentType.BAN) {
            if (!(commandSender.hasPermission(Permissions.BAN) || commandSender.hasPermission(Permissions.BAN_TEMPORARY))) {
                sendMessage(commandSender, "You do not have access to this command", true);
                return;
            }
        }
        else {
            if (!(commandSender.hasPermission(Permissions.MUTE) || commandSender.hasPermission(Permissions.MUTE_TEMPORARY))) {
                sendMessage(commandSender, "You do not have access to this command", true);
                return;
            }
        }

        if (args.length < 3) {
            sendMessage(commandSender, Messages.get(type == PunishmentType.BAN ? Messages.BAN_INVALID_USAGE : Messages.MUTE_INVALID_USAGE), true);
            return;
        }

        Player target = Player.get(args[0]);
        if (target == null || !target.exists()) {
            sendMessage(commandSender, Messages.get(Messages.PLAYER_NOT_FOUND).replaceAll("%player%", args[0]), true);
            return;
        }

        long expiryDate = DateUtil.parseDateDiff(args[1], true);
        if (expiryDate == -1 && !commandSender.hasPermission(type == PunishmentType.BAN ? Permissions.BAN : Permissions.MUTE)) {
            sendMessage(commandSender, "You do not have access to this command", true);
            return;
        }

        target.hasPermission(type == PunishmentType.BAN ? Permissions.BAN_EXEMPT : Permissions.MUTE_EXEMPT).thenAcceptAsync(permissionCheck -> {
            if (permissionCheck) {
                sendMessage(commandSender, Messages.get(type == PunishmentType.BAN ? Messages.BAN_EXEMPT : Messages.MUTE_EXEMPT).replaceAll("%player%", target.getUsername()), true);
                return;
            }

            args[0] = "";
            args[1] = expiryDate != -1 ? "" : args[1];

            String punishmentReason = String.join(" ", args).trim();
            String expiryDateFormat = expiryDate == -1 ? "never" : DateUtil.format(expiryDate);

            if (target.getProxiedPlayer() != null) {
                String message = Messages.get(type == PunishmentType.BAN ? Messages.BAN : Messages.MUTE)
                        .replaceAll("%staff%", commandSender.getName())
                        .replaceAll("%reason%", punishmentReason)
                        .replaceAll("%expiry_date%", expiryDateFormat);
                if (type == PunishmentType.BAN) {
                    target.getProxiedPlayer().disconnect(TextComponent.fromLegacyText(translateColorCodes(message)));
                }
                else {
                    target.sendMessage(message, true);
                }
            }

            String punishmentId = target.punish(type, commandSender, punishmentReason, args[1]);
            DiscordUtils.logPunishment(type, punishmentId, target, commandSender, punishmentReason, expiryDate);

            String alertMessage = Messages.get(type == PunishmentType.BAN ? Messages.BAN_ALERT : Messages.MUTE_ALERT)
                    .replace("%player%", target.getUsername())
                    .replace("%staff%", commandSender.getName())
                    .replace("%reason%", punishmentReason)
                    .replace("%expiry_date%", expiryDateFormat);

            ProxyServer.getInstance().getPlayers().stream()
                    .filter(p -> p.hasPermission(type == PunishmentType.BAN ? Permissions.BAN_RECEIVE : Permissions.MUTE_RECEIVE))
                    .forEach(p -> p.sendMessage(TextComponent.fromLegacyText(translateColorCodes(alertMessage))));
        });
    }
}
