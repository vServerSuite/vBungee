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
                            "PRIMARY KEY (`player_uuid`))");
                    System.out.println("vSuite > Initialising table: Players");
                }
            });
            sql.query("SHOW TABLES LIKE 'Bans'", resultSet -> {
                if (resultSet == null || !resultSet.next()) {
                    sql.update("CREATE TABLE `Bans` (" +
                            "`ban_id` INT NOT NULL AUTO_INCREMENT, " +
                            "`ban_uuid` VARCHAR(36) NOT NULL, " +
                            "`ban_start_date` BIGINT NOT NULL, " +
                            "`ban_staff` VARCHAR(36) NOT NULL, " +
                            "`ban_reason` VARCHAR(200) NOT NULL, " +
                            "`ban_temporary` BOOLEAN NOT NULL DEFAULT '0', " +
                            "`ban_end_date` BIGINT, " +
                            "`ban_is_active` BOOLEAN NOT NULL DEFAULT '1', " +
                            "PRIMARY KEY (`ban_id`));");
                    System.out.println("vSuite > Initialising table: Bans");
                }
            });
            sql.query("SHOW TABLES LIKE 'Mutes'", resultSet -> {
                if (resultSet == null || !resultSet.next()) {
                    sql.update("CREATE TABLE `Mutes` (" +
                            "`mute_id` INT NOT NULL AUTO_INCREMENT, " +
                            "`mute_uuid` VARCHAR(36) NOT NULL, " +
                            "`mute_start_date` BIGINT NOT NULL, " +
                            "`mute_staff` VARCHAR(36) NOT NULL, " +
                            "`mute_reason` VARCHAR(200) NOT NULL, " +
                            "`mute_temporary` BOOLEAN NOT NULL DEFAULT '0', " +
                            "`mute_end_date` BIGINT, " +
                            "`mute_is_active` BOOLEAN NOT NULL DEFAULT '1', " +
                            "PRIMARY KEY (`mute_id`));");
                    System.out.println("vSuite > Initialising table: Mutes");
                }
            });
            sql.query("SHOW TABLES LIKE 'Kicks'", resultSet -> {
                if (resultSet == null || !resultSet.next()) {
                    sql.update("CREATE TABLE `Kicks` (" +
                            "`kick_id` INT NOT NULL AUTO_INCREMENT, " +
                            "`kick_uuid` VARCHAR(36) NOT NULL, " +
                            "`kick_date` BIGINT NOT NULL, " +
                            "`kick_staff` VARCHAR(36) NOT NULL, " +
                            "`kick_reason` VARCHAR(200) NOT NULL, " +
                            "PRIMARY KEY (`kick_id`));");
                    System.out.println("vSuite > Initialising table: Kicks");
                }
            });
            System.out.println("vSuite > Database Initialisation Complete");
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
