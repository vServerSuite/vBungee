package dev.vsuite.bungee.commands.punishments;

import dev.vsuite.bungee.base.punishments.BaseReversePunishmentCommand;
import dev.vsuite.bungee.models.punishments.PunishmentType;

public class UnmuteCommand extends BaseReversePunishmentCommand {
    public UnmuteCommand() {
        super(PunishmentType.MUTE, "unmute", null, "removemute", "reversemute");
    }
}