package codes.benh.velocitymc.models.punishments;

import java.util.UUID;

public class Kick {

    private int Id;

    private String Player;

    private long Date;

    private String Staff;

    private String Reason;

    public int getId() { return Id; }

    public void setId(int id) { Id = id; }

    public String getPlayer() {
        return Player;
    }

    public void setPlayer(String player) {
        Player = player;
    }

    public long getDate() {
        return Date;
    }

    public void setDate(long date) {
        Date = date;
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
}
