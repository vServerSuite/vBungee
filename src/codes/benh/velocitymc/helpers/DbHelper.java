package codes.benh.velocitymc.helpers;

import codes.benh.velocitymc.Main;
import pro.husk.mysql.MySQL;

public class DbHelper {

    public static void initialise() {
        try {
            MySQL sql = Main.getMySQL();

            sql.query("SHOW TABLES LIKE 'Players'", resultSet -> {
                if (resultSet == null || !resultSet.next()) {
                    sql.update("CREATE TABLE `Players` (" +
                            "`player_uuid` VARCHAR(36) NOT NULL, " +
                            "`player_username` VARCHAR(16) NOT NULL, " +
                            "`player_first_login` BIGINT NOT NULL, " +
                            "`player_last_login` BIGINT NOT NULL, " +
                            "`player_discord_id` BIGINT, " +
                            "PRIMARY KEY (`player_uuid`))");
                    System.out.println("vSuite > Initialising table: Players");
                }
            });
            sql.query("SHOW TABLES LIKE 'Punishments'", resultSet -> {
                if (resultSet == null || !resultSet.next()) {
                    sql.update("CREATE TABLE `Punishments` (" +
                            "`punishment_id` VARCHAR(5) NOT NULL, " +
                            "`punishment_type` VARCHAR(50) NOT NULL, " +
                            "`punishment_uuid` VARCHAR(36) NOT NULL, " +
                            "`punishment_issue_date` BIGINT NOT NULL, " +
                            "`punishment_staff` VARCHAR(36) NOT NULL, " +
                            "`punishment_reason` VARCHAR(200) NOT NULL, " +
                            "`punishment_temporary` BOOLEAN DEFAULT '0', " +
                            "`punishment_end_date` BIGINT, " +
                            "`punishment_is_active` BOOLEAN DEFAULT '1', " +
                            "`punishment_discord_message_id` BIGINT, " +
                            "UNIQUE KEY `PunishmentId` (`punishment_id`) USING BTREE, " +
                            "PRIMARY KEY (`punishment_id`));");
                    System.out.println("vSuite > Initialising table: Punishments");
                }
            });
            System.out.println("vSuite > Database Initialisation Complete");
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
