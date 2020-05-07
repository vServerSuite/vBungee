package dev.vsuite.bungee.managers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dev.vsuite.bungee.Main;
import dev.vsuite.bungee.models.Player;
import dev.vsuite.bungee.models.punishments.Punishment;
import dev.vsuite.bungee.models.punishments.PunishmentType;
import dev.vsuite.bungee.utils.DateUtil;
import io.sentry.Sentry;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.apache.commons.lang3.RandomStringUtils;

public class PunishmentManager {

    private Player player;

    public PunishmentManager() {

    }

    public PunishmentManager(Player player) {
        this.player = player;
    }

    public String logPunishment(PunishmentType type, CommandSender staffMember, String message, String expiryDate) {
        String staffUuid = staffMember instanceof ProxiedPlayer ? ((ProxiedPlayer) staffMember).getUniqueId().toString() : "CONSOLE";
        return logPunishment(type, staffUuid, message, expiryDate);
    }

    public String logPunishment(PunishmentType type, String staffMember, String message, String expiryDate) {
        try {
            String uniqueId = generateId();
            long expiry = DateUtil.parseDateDiff(expiryDate, true);
            Main.getMySQL().update("INSERT INTO `Punishments` " +
                    "(`punishment_id`, `punishment_type`, `punishment_uuid`, `punishment_issue_date`, `punishment_staff`, `punishment_reason`, `punishment_temporary`, `punishment_end_date`) VALUES " +
                    "('" + generateId() + "', '" + type + "', '" + player.getUuid() + "', '" + System.currentTimeMillis() + "', '" + staffMember + "', '" + message + "', '" + (expiry == -1 ? 0 : 1) + "', '" + (expiry == -1 ? 0 : expiry) + "')");
            return uniqueId;
        }
        catch (SQLException e) {
            if(Main.loggingEnabled()) {
                Sentry.capture(e);
            }
            return null;
        }
    }

    private String generateId() {
        String returnValue = RandomStringUtils.random(5, true, true);

        while (Objects.requireNonNull(getAllIds()).contains(returnValue)) {
            returnValue = RandomStringUtils.random(5, true, true);
        }

        return returnValue;
    }


    private List<String> getAllIds() {
        try {
            List<String> returnValue = new ArrayList<>();
            ResultSet resultSet = Main.getMySQL().query("SELECT punishment_id from Punishments");
            if (resultSet != null) {
                while (resultSet.next()) {
                    returnValue.add(resultSet.getString("punishment_id"));
                }
            }
            return returnValue;
        }
        catch (SQLException e) {
            if(Main.loggingEnabled()) {
                Sentry.capture(e);
            }
            return null;
        }
    }

    public boolean update(Punishment punishment) {
        try {
            Main.getMySQL().update("UPDATE `Punishments` SET " +
                    "punishment_type = '" + punishment.getType() + "', " +
                    "punishment_uuid = '" + punishment.getPlayer() + "', " +
                    "punishment_issue_date = '" + punishment.getDateIssued() + "', " +
                    "punishment_staff = '" + punishment.getStaff() + "', " +
                    "punishment_reason = '" + punishment.getReason() + "', " +
                    "punishment_temporary = '" + (punishment.isTemporary() ? 1 : 0) + "', " +
                    "punishment_end_date = '" + punishment.getDateEnded() + "', " +
                    "punishment_is_active = '" + (punishment.isActive() ? 1 : 0) + "', " +
                    "punishment_discord_message_id = '" + punishment.getDiscordMessageId() + "' " +
                    "WHERE punishment_id = '" + punishment.getId() + "'");
            return true;
        }
        catch (SQLException ex) {
            if(Main.loggingEnabled()) {
                Sentry.capture(ex);
            }
            return false;
        }
    }
}
