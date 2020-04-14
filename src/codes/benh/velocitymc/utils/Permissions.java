package codes.benh.velocitymc.utils;

public class Permissions {

    // Prefixes
    private static final String COMMAND_PREFIX = "bungeecord.command.";

    // Command Permissions
    public static final String BUNGEE_STATS = COMMAND_PREFIX + "stats";
    public static final String STAFF_CHAT = COMMAND_PREFIX + "staffchat";
    public static final String REPORT = COMMAND_PREFIX + "report";
    public static final String LOBBY = COMMAND_PREFIX + "lobby";

    // Receive Permissions
    public static final String STAFF_CHAT_RECEIVE = COMMAND_PREFIX + "staffchat.receive";
    public static final String REPORT_RECEIVE = COMMAND_PREFIX + "report.receive";

    //Punishments Permissions
    public static final String KICK = COMMAND_PREFIX + "kick";
    public static final String KICK_EXEMPT = KICK + ".exempt";
    public static final String KICK_RECEIVE = KICK + ".receive";


    public static final String BAN = COMMAND_PREFIX + "ban";
    public static final String BAN_EXEMPT = KICK + ".exempt";
    public static final String BAN_RECEIVE = KICK + ".receive";

    public static final String LOOKUP = COMMAND_PREFIX + "lookup";

}
