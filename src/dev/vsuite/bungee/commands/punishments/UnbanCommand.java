package dev.vsuite.bungee.commands.punishments;

import dev.vsuite.bungee.base.punishments.BaseReversePunishmentCommand;
import dev.vsuite.bungee.models.punishments.PunishmentType;

public class UnbanCommand extends BaseReversePunishmentCommand {
    public UnbanCommand() {
        super(PunishmentType.BAN, "unban", null, "removeban", "reverseban");
    }
}