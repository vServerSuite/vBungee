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
            sql.query("SHOW TABLES LIKE 'DiscordVerificationTokens'", resultSet -> {
                if (resultSet == null || !resultSet.next()) {
                    sql.update("CREATE TABLE `DiscordVerificationTokens` (" +
                            "`verification_token` VARCHAR(5) NOT NULL, " +
                            "`verification_uuid` VARCHAR(36) NOT NULL, " +
                            "`verification_date` BIGINT NOT NULL, " +
                            "UNIQUE KEY `Token` (`verification_token`) USING BTREE, " +
                            "PRIMARY KEY (`verification_token`));");
                    System.out.println("vSuite > Initialising table: Discord Verification Tokens");
                }
            });
            if(Main.getInstance().getConfig().getBoolean("WebAPI.Enabled")) {
                sql.query("SHOW TABLES LIKE 'Notifications'", resultSet -> {
                    if (resultSet == null || !resultSet.next()) {
                        sql.update("CREATE TABLE `Notifications` (" +
                                "`notification_id` VARCHAR(36) NOT NULL, " +
                                "`notification_type` VARCHAR(15) NOT NULL, " +
                                "`notification_message` VARCHAR(200) NOT NULL, " +
                                "`notification_processed` BOOLEAN NOT NULL DEFAULT '0', " +
                                "PRIMARY KEY (`notification_id`));");
                        System.out.println("vSuite > Initialising table: Notifications");
                    }
                });
            }
            System.out.println("vSuite > Database Initialisation Complete");
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
