package codes.benh.velocitymc.models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import codes.benh.velocitymc.Main;
import codes.benh.velocitymc.models.punishments.Punishment;
import codes.benh.velocitymc.models.punishments.PunishmentType;
import codes.benh.velocitymc.utils.PlayerUtils;
import net.luckperms.api.cacheddata.CachedPermissionData;
import net.luckperms.api.context.ContextManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Player {

    private ProxiedPlayer player;
    private UUID uuid;
    private String username;
    private String discordId;

    private long _firstLogin = 0;
    private long _lastLogin = 0;
    private List<Punishment> _bans = new ArrayList<>();

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
        UUID uid = PlayerUtils.getUUIDFromUsername(username);
        if (uid != null) {
            return new Player(uid);
        }
        else {
            return null;
        }
    }

    public String getDiscordId() {
        return discordId;
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
                            discordId = resultSet.getString("player_discord_id");
                        }
                    }
                });
                _bans = getPunishments(PunishmentType.BAN);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<Punishment> getPunishments(PunishmentType type) {
        List<Punishment> punishments = new ArrayList<>();
        try {
            Main.getMySQL().query("SELECT * FROM Punishments WHERE punishment_uuid='" + uuid + "' AND punishment_type='" + type + "'", resultSet -> {
                if (resultSet != null) {
                    while (resultSet.next()) {
                        Punishment punishment = new Punishment();
                        punishment.setId(resultSet.getString("punishment_id"));
                        punishment.setType(PunishmentType.valueOf(resultSet.getString("punishment_type")));
                        punishment.setPlayer(PlayerUtils.getUsernameFromUUID(uuid));
                        punishment.setDateIssued(resultSet.getLong("punishment_issue_date"));
                        punishment.setStaff(PlayerUtils.getUsernameFromUUID(UUID.fromString(resultSet.getString("punishment_staff"))));
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
            e.printStackTrace();
        }
        return punishments;
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

    public List<Punishment> getBans() {
        return _bans;
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

    public String logPunishment(PunishmentType type, CommandSender staffMember, String reason, long expiryDate) {
        String punishmentId = getUniqueId();
        System.out.println(punishmentId);
        String staffUuid = (staffMember instanceof ProxiedPlayer) ? ((ProxiedPlayer) staffMember).getUniqueId().toString() : "CONSOLE";

        try {
            Main.getMySQL().update("INSERT INTO " +
                    "`Punishments` (`punishment_id`, `punishment_type`, `punishment_uuid`, `punishment_issue_date`, `punishment_staff`, `punishment_reason`, `punishment_temporary`, `punishment_end_date`) " +
                    "VALUES ('" + punishmentId + "', '" + type + "', '" + uuid + "', '" + System.currentTimeMillis() + "', '" + staffUuid + "', '" + reason + "', '" + (expiryDate == -1 ? 0 : 1) + "', '" + (expiryDate == -1 ? 0 : expiryDate) + "')");
            return punishmentId;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isBanned() {
        final boolean[] returnValue = {false};
        try {
            Main.getMySQL().query("SELECT 1 FROM Punishments WHERE punishment_uuid='" + uuid + "' AND punishment_is_active='1' AND punishment_type='" + PunishmentType.BAN + "'", resultSet -> {
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

    private String getUniqueId() {
        String returnValue = generateId();

        while (Objects.requireNonNull(getIds()).contains(returnValue)) {
            returnValue = generateId();
        }

        return generateId();
    }

    private List<String> getIds() {

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
            e.printStackTrace();
            return null;
        }
    }

    ;

    private String generateId() {
        final String ALPHA_NUMERIC_STRING = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        int count = 5;
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }

        return builder.toString();
    }
}
