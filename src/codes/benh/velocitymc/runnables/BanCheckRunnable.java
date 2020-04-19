package codes.benh.velocitymc.runnables;

import java.sql.SQLException;

import codes.benh.velocitymc.Main;

public class BanCheckRunnable implements Runnable {
    @Override
    public void run() {
        try {
            if(Main.getMySQL() != null && !Main.getMySQL().getConnection().isClosed()) {
                Main.getMySQL().update("UPDATE Punishments SET punishment_is_active=0 " +
                        "WHERE punishment_end_date <= '" + System.currentTimeMillis() + "' AND punishment_end_date <> '0'");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
