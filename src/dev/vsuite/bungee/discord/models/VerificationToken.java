package dev.vsuite.bungee.discord.models;

import java.sql.SQLException;
import java.util.UUID;

import dev.vsuite.bungee.Main;

public class VerificationToken {
    private String token;
    private UUID uuid;
    private long date;

    public static VerificationToken getVerificationToken(String verificationToken) {
        final VerificationToken[] returnValue = new VerificationToken[1];
        try {
            Main.getMySQL().query("SELECT * from DiscordVerificationTokens WHERE verification_token='" + verificationToken + "'", resultSet -> {
                if (resultSet != null) {
                    while (resultSet.next()) {
                        VerificationToken token = new VerificationToken();
                        token.setToken(verificationToken);
                        token.setUuid(UUID.fromString(resultSet.getString("verification_uuid")));
                        token.setDate(resultSet.getLong("verification_date"));
                        returnValue[0] = token;
                    }
                }
            });
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return returnValue[0];
    }

    public void delete() {
        try {
            Main.getMySQL().update("DELETE FROM DiscordVerificationTokens WHERE verification_token='" + token + "'");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
