package dev.vsuite.bungee.commands.punishments;

import dev.vsuite.bungee.base.punishments.BasePunishmentCommand;
import dev.vsuite.bungee.models.punishments.PunishmentType;

public class MuteCommand extends BasePunishmentCommand {
    public MuteCommand() {
        super(PunishmentType.MUTE, "mute", null, "tempmute", "muteplayer", "temporarymute");
    }
}