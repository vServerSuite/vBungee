package codes.benh.velocitymc.models.punishments;

import java.util.UUID;

public class Ban {

    private int Id;

    private String Player;

    private long DateStarted;

    private String Staff;

    private String Reason;

    private boolean IsTemporary;

    private long BanEndDate;

    private boolean IsActive;

    public int getId() { return Id; }

    public void setId(int id) { Id = id; }

    public String getPlayer() {
        return Player;
    }

    public void setPlayer(String player) {
        Player = player;
    }

    public long getDateStarted() {
        return DateStarted;
    }

    public void setDateStarted(long dateStarted) {
        DateStarted = dateStarted;
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

    public long getBanEndDate() {
        return BanEndDate;
    }

    public void setBanEndDate(long banEndDate) {
        BanEndDate = banEndDate;
    }

    public boolean isActive() {
        return IsActive;
    }

    public void setActive(boolean active) {
        IsActive = active;
    }
}
