package dev.vsuite.bungee.runnables;

import java.sql.SQLException;

import dev.vsuite.bungee.Main;
import io.sentry.Sentry;

public class BanCheckRunnable implements Runnable {
    @Override
    public void run() {
        try {
            if (Main.getMySQL() != null && !Main.getMySQL().getConnection().isClosed()) {
                Main.getMySQL().update("UPDATE Punishments SET punishment_is_active=0 " +
                        "WHERE punishment_end_date <= '" + System.currentTimeMillis() + "' AND punishment_end_date <> '0'");
            }
        }
        catch (SQLException e) {
            if(Main.loggingEnabled()) {
                Sentry.capture(e);
            }
        }
    }
}
