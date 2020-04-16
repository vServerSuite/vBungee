package codes.benh.velocitymc.utils;

public class Permissions {

    private static final String COMMAND_PREFIX = "vSuite.";

    public static final String BUNGEE_STATS = COMMAND_PREFIX + "stats";
    public static final String LOBBY = COMMAND_PREFIX + "lobby";

    public static final String STAFF_CHAT = COMMAND_PREFIX + "staffchat";
    public static final String STAFF_CHAT_RECEIVE = STAFF_CHAT + "receive";

    public static final String REPORT = COMMAND_PREFIX + "report";
    public static final String REPORT_RECEIVE = REPORT + "receive";

    public static final String KICK = COMMAND_PREFIX + "kick";
    public static final String KICK_EXEMPT = KICK + ".exempt";
    public static final String KICK_RECEIVE = KICK + ".receive";

    public static final String BAN = COMMAND_PREFIX + "ban";
    public static final String BAN_TEMPORARY = BAN + "temporary";
    public static final String BAN_EXEMPT = BAN + ".exempt";
    public static final String BAN_RECEIVE = BAN + ".receive";

    public static final String MUTE = COMMAND_PREFIX + "mute";
    public static final String MUTE_TEMPORARY = BAN + "temporary";
    public static final String MUTE_EXEMPT = MUTE + ".exempt";
    public static final String MUTE_RECEIVE = MUTE + ".receive";

    public static final String LOOKUP = COMMAND_PREFIX + "lookup";

}
