package dev.vsuite.bungee.utils;

import java.sql.SQLException;
import java.util.UUID;

import dev.vsuite.bungee.Main;
import io.sentry.Sentry;

public class PlayerUtils {

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
            if (Main.loggingEnabled()) {
                Sentry.capture(e);
            }
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
            if (Main.loggingEnabled()) {
                Sentry.capture(e);
            }
        }
        if (returnValue[0] != null) {
            return UUID.fromString(returnValue[0]);
        }
        else {
            return null;
        }
    }
}
