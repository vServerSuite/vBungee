package dev.vsuite.bungee.models.logs;

import java.net.SocketAddress;
import java.sql.SQLException;
import java.util.UUID;

import dev.vsuite.bungee.Main;
import io.sentry.Sentry;
import net.md_5.bungee.api.connection.Server;

public class Log {

    private LogType Type;

    private UUID Player;

    private long DateTime;

    private String Message;

    public static void addLog(LogType type, UUID player, SocketAddress address, long dateTime, Server server, String message) {
        String serverName = server != null ? server.getInfo().getName() : "N/A";
        try {
            Main.getMySQL().update(
                    "INSERT INTO `Logs` " +
                            "(`log_type`, `log_player`, `log_ip_address`, `log_datetime`, `log_server`, `log_message`) " +
                            "VALUES ('" + type.toString() + "', '" + player + "', '" + fixIPString(address) + "', '" + dateTime + "', '" + serverName + "','" + message + "')");
        }
        catch (SQLException e) {
            if(Main.loggingEnabled()) {
                Sentry.capture(e);
            }
        }
    }


    private static String fixIPString(SocketAddress address) {
        return address.toString().replaceAll("/", "").split(":")[0];
    }
}
