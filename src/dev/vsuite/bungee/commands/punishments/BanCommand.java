package dev.vsuite.bungee.commands.punishments;

import dev.vsuite.bungee.base.punishments.BasePunishmentCommand;
import dev.vsuite.bungee.models.punishments.PunishmentType;

public class BanCommand extends BasePunishmentCommand {
    public BanCommand() {
        super(PunishmentType.BAN, "ban", null, "tempban", "bantemp", "temporaryban");
    }
}