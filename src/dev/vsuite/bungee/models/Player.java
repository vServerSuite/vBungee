package dev.vsuite.bungee.models;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import dev.vsuite.bungee.Main;
import dev.vsuite.bungee.base.models.BasePlayer;
import dev.vsuite.bungee.managers.PunishmentManager;
import dev.vsuite.bungee.models.punishments.Punishment;
import dev.vsuite.bungee.models.punishments.PunishmentType;
import dev.vsuite.bungee.utils.PlayerUtils;
import io.sentry.Sentry;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Player extends BasePlayer {

    private PunishmentManager punishmentManager;

    private Player(ProxiedPlayer player) {
        setProxiedPlayer(player);
        setUuid(player.getUniqueId());
        initialise(getUuid());
        punishmentManager = new PunishmentManager(this);
    }

    private Player(UUID uuid) {
        ProxiedPlayer proxPlayer = ProxyServer.getInstance().getPlayer(uuid);
        if (proxPlayer != null) {
            setProxiedPlayer(ProxyServer.getInstance().getPlayer(uuid));
        }
        setUuid(uuid);
        initialise(getUuid());
        punishmentManager = new PunishmentManager(this);
    }

    public static Player get(ProxiedPlayer player) {
        return new Player(player);
    }

    public static Player get(CommandSender commandSender) {
        return new Player((ProxiedPlayer) commandSender);
    }

    public static Player get(Connection connection) {
        return new Player((ProxiedPlayer) connection);
    }

    public static Player get(UUID uuid) {
        return new Player(uuid);
    }

    public static Player get(String username) {
        UUID uid = PlayerUtils.getUUIDFromUsername(username);
        if (uid != null) {
            return new Player(uid);
        }
        else {
            return null;
        }
    }

    private void initialise(UUID uuid) {
        try {
            if (exists()) {
                Main.getMySQL().query("SELECT * FROM Players WHERE player_uuid='" + uuid + "'", resultSet -> {
                    if (resultSet != null) {
                        while (resultSet.next()) {
                            setFirstLogin(resultSet.getLong("player_first_login"));
                            setLastLogin(resultSet.getLong("player_last_login"));
                            setUsername(resultSet.getString("player_username"));
                            setDiscordId(resultSet.getString("player_discord_id"));
                        }
                    }
                });
                setBans(getPunishments(PunishmentType.BAN));
                setMutes(getPunishments(PunishmentType.MUTE));
            }
        }
        catch (SQLException e) {
            if(Main.loggingEnabled()) {
                Sentry.capture(e);
            }
        }
    }

    private List<Punishment> getPunishments(PunishmentType type) {
        List<Punishment> punishments = new ArrayList<>();
        try {
            Main.getMySQL().query("SELECT * FROM Punishments WHERE punishment_uuid='" + getUuid() + "' AND punishment_type='" + type + "'", resultSet -> {
                if (resultSet != null) {
                    while (resultSet.next()) {
                        Punishment punishment = new Punishment();
                        punishment.setId(resultSet.getString("punishment_id"));
                        punishment.setType(PunishmentType.valueOf(resultSet.getString("punishment_type")));
                        punishment.setPlayer(PlayerUtils.getUsernameFromUUID(getUuid()));
                        punishment.setDateIssued(resultSet.getLong("punishment_issue_date"));
                        punishment.setStaff(Objects.equals(resultSet.getString("punishment_staff"), "CONSOLE") ? "CONSOLE" : PlayerUtils.getUsernameFromUUID(UUID.fromString(resultSet.getString("punishment_staff"))));
                        punishment.setReason(resultSet.getString("punishment_reason"));
                        punishment.setTemporary(resultSet.getBoolean("punishment_temporary"));
                        punishment.setDateEnded(resultSet.getLong("punishment_end_date"));
                        punishment.setActive(resultSet.getBoolean("punishment_is_active"));
                        punishment.setDiscordMessageId(resultSet.getLong("punishment_discord_message_id"));
                        punishments.add(punishment);
                    }
                }
            });
        }
        catch (SQLException e) {
            if(Main.loggingEnabled()) {
                Sentry.capture(e);
            }
        }
        return punishments;
    }

    public boolean exists() {
        final boolean[] returnValue = {false};
        try {
            Main.getMySQL().query("SELECT 1 FROM Players WHERE player_uuid='" + getUuid() + "'", resultSet -> {
                if (resultSet != null) {
                    while (resultSet.next()) {
                        returnValue[0] = true;
                    }
                }
            });
        }
        catch (SQLException e) {
            if(Main.loggingEnabled()) {
                Sentry.capture(e);
            }
        }
        return returnValue[0];
    }

    public void generate() {
        try {
            Main.getMySQL().update("INSERT INTO " +
                    "`Players` (`player_uuid`, `player_username`, `player_first_login`, `player_last_login`) " +
                    "VALUES ('" + getUuid() + "', '" + getProxiedPlayer().getName() + "', '" + System.currentTimeMillis() + "', '" + System.currentTimeMillis() + "')");
        }
        catch (SQLException e) {
            if(Main.loggingEnabled()) {
                Sentry.capture(e);
            }
        }
    }

    public void updateLastLogin() {
        try {
            Main.getMySQL().update("UPDATE `Players` SET player_last_login='" + System.currentTimeMillis() + "' WHERE player_uuid='" + getUuid() + "'");
            Main.getMySQL().update("UPDATE `Players` SET player_username='" + getProxiedPlayer().getName() + "' WHERE player_uuid='" + getUuid() + "'");
            setLastLogin(System.currentTimeMillis());
        }
        catch (SQLException e) {
            if(Main.loggingEnabled()) {
                Sentry.capture(e);
            }
        }
    }

    public String punish(PunishmentType type, String staffMember, String message) {
        return punish(type, staffMember, message, "N/A");
    }

    public String punish(PunishmentType type, String staffMember, String message, String expiryDate) {
        return punishmentManager.logPunishment(type, staffMember, message, expiryDate);
    }

    public String punish(PunishmentType punishmentType, CommandSender staffMember, String message) {
        return punish(punishmentType, staffMember, message, "N/A");
    }

    public String punish(PunishmentType punishmentType, CommandSender staffMember, String message, String expiryDate) {
        return punishmentManager.logPunishment(punishmentType, staffMember, message, expiryDate);
    }
}
