package codes.benh.velocitymc.models;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import codes.benh.velocitymc.Main;
import codes.benh.velocitymc.models.punishments.Ban;
import codes.benh.velocitymc.models.punishments.Kick;
import codes.benh.velocitymc.models.punishments.Mute;
import net.luckperms.api.cacheddata.CachedPermissionData;
import net.luckperms.api.context.ContextManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Player {

    private ProxiedPlayer player;
    private UUID uuid;
    private String username;

    private long _firstLogin = 0;
    private long _lastLogin = 0;
    private List<Ban> _bans = new ArrayList<>();
    private List<Mute> _mutes = new ArrayList<>();
    private List<Kick> _kicks = new ArrayList<>();

    private Player(ProxiedPlayer player) {
        this.player = player;
        this.uuid = player.getUniqueId();
        fillDetails(uuid);
    }

    private Player(UUID uuid) {
        this.uuid = uuid;
        fillDetails(uuid);
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
        UUID uid = getUUIDFromUsername(username);
        if (uid != null) {
            return new Player(uid);
        }
        else {
            return null;
        }
    }

    public static String getUsernameFromUUID(UUID uuid) {
        String[] returnValue = new String[1];
        try {
            Main.getMySQL().query("SELECT player_username FROM Players WHERE player_uuid='" + uuid + "'", resultSet -> {
                if (resultSet != null) {
                    while (resultSet.next()) {
                        returnValue[0] = resultSet.getString("player_username");
                    }
                }
            });
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return returnValue[0];
    }

    public static UUID getUUIDFromUsername(String username) {
        String[] returnValue = new String[1];
        returnValue[0] = null;
        try {
            Main.getMySQL().query("SELECT player_uuid FROM Players WHERE player_username='" + username + "'", resultSet -> {
                if (resultSet != null) {
                    while (resultSet.next()) {
                        returnValue[0] = resultSet.getString("player_uuid");
                    }
                }
            });
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        if (returnValue[0] != null) {
            return UUID.fromString(returnValue[0]);
        }
        else {
            return null;
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UUID getUUID() {
        return uuid;
    }

    private void fillDetails(UUID uuid) {
        try {
            if (exists()) {
                Main.getMySQL().query("SELECT * FROM Players WHERE player_uuid='" + uuid + "'", resultSet -> {
                    if (resultSet != null) {
                        while (resultSet.next()) {
                            _firstLogin = resultSet.getLong("player_first_login");
                            _lastLogin = resultSet.getLong("player_last_login");
                            username = resultSet.getString("player_username");
                        }
                    }
                });
                Main.getMySQL().query("SELECT * FROM Bans WHERE ban_uuid='" + uuid + "'", resultSet -> {
                    if (resultSet != null) {
                        while (resultSet.next()) {
                            Ban ban = new Ban();
                            ban.setId(resultSet.getInt("ban_id"));
                            ban.setPlayer(getUsernameFromUUID(uuid));
                            ban.setDateStarted(resultSet.getLong("ban_start_date"));
                            ban.setStaff(getUsernameFromUUID(UUID.fromString(resultSet.getString("ban_staff"))));
                            ban.setReason(resultSet.getString("ban_reason"));
                            ban.setTemporary(resultSet.getBoolean("ban_temporary"));
                            ban.setBanEndDate(resultSet.getLong("ban_end_date"));
                            ban.setActive(resultSet.getBoolean("ban_is_active"));

                            _bans.add(ban);
                        }
                    }
                });
                Main.getMySQL().query("SELECT * FROM Mutes WHERE mute_uuid='" + uuid + "'", resultSet -> {
                    if (resultSet != null) {
                        while (resultSet.next()) {
                            Mute mute = new Mute();
                            mute.setId(resultSet.getInt("mute_id"));
                            mute.setPlayer(getUsernameFromUUID(uuid));
                            mute.setDateStarted(resultSet.getLong("mute_start_date"));
                            mute.setStaff(getUsernameFromUUID(UUID.fromString(resultSet.getString("mute_staff"))));
                            mute.setReason(resultSet.getString("mute_reason"));
                            mute.setTemporary(resultSet.getBoolean("mute_temporary"));
                            mute.setMuteEndDate(resultSet.getLong("mute_end_date"));
                            mute.setActive(resultSet.getBoolean("mute_is_active"));

                            _mutes.add(mute);
                        }
                    }
                });
                Main.getMySQL().query("SELECT * FROM Kicks WHERE kick_uuid='" + uuid + "'", resultSet -> {
                    if (resultSet != null) {
                        while (resultSet.next()) {
                            Kick kick = new Kick();
                            kick.setId(resultSet.getInt("kick_id"));
                            kick.setPlayer(getUsernameFromUUID(uuid));
                            kick.setDate(resultSet.getLong("kick_date"));
                            kick.setStaff(getUsernameFromUUID(UUID.fromString(resultSet.getString("kick_staff"))));
                            kick.setReason(resultSet.getString("kick_reason"));

                            _kicks.add(kick);
                        }
                    }
                });
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ProxiedPlayer getProxiedPlayer() {
        return player;
    }

    public long getFirstLogin() {
        return _firstLogin;
    }

    public long getLastLogin() {
        return _lastLogin;
    }

    public List<Ban> getBans() {
        return _bans;
    }

    public List<Mute> getMutes() {
        return _mutes;
    }

    public List<Kick> getKicks() {
        return _kicks;
    }

    public boolean exists() {
        final boolean[] returnValue = {false};
        try {
            Main.getMySQL().query("SELECT 1 FROM Players WHERE player_uuid='" + uuid + "'", resultSet -> {
                if (resultSet != null) {
                    while (resultSet.next()) {
                        returnValue[0] = true;
                    }
                }
            });
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return returnValue[0];
    }

    public CompletableFuture<Boolean> hasPermission(String permission) {
        return Main.getLuckPermsAPI().getUserManager().loadUser(uuid)
                .thenApplyAsync(user -> {
                    ContextManager contextManager = Main.getLuckPermsAPI().getContextManager();
                    CachedPermissionData permissionData = user.getCachedData().getPermissionData(contextManager.getQueryOptions(user).orElseGet(contextManager::getStaticQueryOptions));
                    return permissionData.checkPermission(permission).asBoolean();
                });
    }

    public void generate() {
        try {
            Main.getMySQL().update("INSERT INTO " +
                    "`Players` (`player_uuid`, `player_username`, `player_first_login`, `player_last_login`) " +
                    "VALUES ('" + uuid + "', '" + player.getName() + "', '" + System.currentTimeMillis() + "', '" + System.currentTimeMillis() + "')");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateLastLogin() {
        try {
            Main.getMySQL().update("UPDATE `Players` SET player_last_login='" + System.currentTimeMillis() + "' WHERE player_uuid='" + uuid + "'");
            Main.getMySQL().update("UPDATE `Players` SET player_username='" + player.getName() + "' WHERE player_uuid='" + uuid + "'");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void logKick(CommandSender staffMember, String reason) {

        String staffUuid = (staffMember instanceof ProxiedPlayer) ? ((ProxiedPlayer) staffMember).getUniqueId().toString() : "CONSOLE";

        try {
            Main.getMySQL().update("INSERT INTO " +
                    "`Kicks` (`kick_uuid`, `kick_date`, `kick_staff`, `kick_reason`) " +
                    "VALUES ('" + uuid + "', '" + System.currentTimeMillis() + "', '" + staffUuid + "', '" + reason + "')");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void logBan(CommandSender staffMember, String reason, long expiryDate) {

        String staffUuid = (staffMember instanceof ProxiedPlayer) ? ((ProxiedPlayer) staffMember).getUniqueId().toString() : "CONSOLE";

        try {
            Main.getMySQL().update("INSERT INTO " +
                    "`Bans` (`ban_uuid`, `ban_start_date`, `ban_staff`, `ban_reason`, `ban_temporary`, `ban_end_date`) " +
                    "VALUES ('" + uuid + "', '" + System.currentTimeMillis() + "', '" + staffUuid + "', '" + reason + "', '" + (expiryDate == -1 ? 0 : 1) + "', '" + (expiryDate == -1 ? 0 : expiryDate) + "')");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void logMute(CommandSender staffMember, String reason, long expiryDate) {

        String staffUuid = (staffMember instanceof ProxiedPlayer) ? ((ProxiedPlayer) staffMember).getUniqueId().toString() : "CONSOLE";

        try {
            Main.getMySQL().update("INSERT INTO " +
                    "`Mutes` (`mute_uuid`, `mute_start_date`, `mute_staff`, `mute_reason`, `mute_temporary`, `mute_end_date`) " +
                    "VALUES ('" + uuid + "', '" + System.currentTimeMillis() + "', '" + staffUuid + "', '" + reason + "', '" + (expiryDate == -1 ? 0 : 1) + "', '" + (expiryDate == -1 ? 0 : expiryDate) + "')");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isBanned() {
        final boolean[] returnValue = {false};
        try {
            Main.getMySQL().query("SELECT 1 FROM Bans WHERE ban_uuid='" + uuid + "' AND ban_is_active", resultSet -> {
                if (resultSet != null) {
                    while (resultSet.next()) {
                        returnValue[0] = true;
                    }
                }
            });
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return returnValue[0];
    }

    public boolean isMuted() {
        final boolean[] returnValue = {false};
        try {
            Main.getMySQL().query("SELECT 1 FROM Mutes WHERE mute_uuid='" + uuid + "' AND mute_is_active", resultSet -> {
                if (resultSet != null) {
                    while (resultSet.next()) {
                        returnValue[0] = true;
                    }
                }
            });
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return returnValue[0];
    }
}
