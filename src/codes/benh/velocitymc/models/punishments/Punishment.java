package codes.benh.velocitymc.models.punishments;

import java.sql.SQLException;

import codes.benh.velocitymc.Main;

public class Punishment {

    private String Id;

    private PunishmentType type;
    private String Player;
    private long DateIssued;
    private String Staff;
    private String Reason;
    private boolean IsTemporary;
    private long DateEnded;
    private boolean IsActive;
    private long DiscordMessageId;

    public static String getDiscordMessageId(String banId) {
        try {
            final String[] returnValue = {null};
            Main.getMySQL().query("SELECT punishment_discord_message_id FROM Punishments WHERE punishment_id='" + banId + "'", resultSet -> {
                if (resultSet != null) {
                    while (resultSet.next()) {
                        returnValue[0] = resultSet.getString("punishment_discord_message_id");
                    }
                }
            });
            return returnValue[0];
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public PunishmentType getType() {
        return type;
    }

    public void setType(PunishmentType type) {
        this.type = type;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getPlayer() {
        return Player;
    }

    public void setPlayer(String player) {
        Player = player;
    }

    public long getDateIssued() {
        return DateIssued;
    }

    public void setDateIssued(long dateIssued) {
        DateIssued = dateIssued;
    }

    public String getStaff() {
        return Staff;
    }

    public void setStaff(String staff) {
        Staff = staff;
    }

    public String getReason() {
        return Reason;
    }

    public void setReason(String reason) {
        Reason = reason;
    }

    public boolean isTemporary() {
        return IsTemporary;
    }

    public void setTemporary(boolean temporary) {
        IsTemporary = temporary;
    }

    public long getDateEnded() {
        return DateEnded;
    }

    public void setDateEnded(long dateEnded) {
        DateEnded = dateEnded;
    }

    public boolean isActive() {
        return IsActive;
    }

    public void setActive(boolean active) {
        IsActive = active;
    }

    public long getDiscordMessageId() {
        return DiscordMessageId;
    }

    public void setDiscordMessageId(long discordMessageId) {
        DiscordMessageId = discordMessageId;
    }
}
