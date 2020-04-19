package codes.benh.velocitymc.utils;

public class Permissions {
    private static final String PREFIX = "vSuite.";

    public static final String BUNGEE_STATS = PREFIX + "stats";
    public static final String LOBBY = PREFIX + "lobby";

    public static final String STAFF_CHAT = PREFIX + "staffchat";
    public static final String STAFF_CHAT_RECEIVE = STAFF_CHAT + "receive";

    public static final String REPORT = PREFIX + "report";
    public static final String REPORT_RECEIVE = REPORT + "receive";

    public static final String BAN = PREFIX + "ban";
    public static final String BAN_TEMPORARY = BAN + "temporary";
    public static final String BAN_EXEMPT = BAN + ".exempt";
    public static final String BAN_RECEIVE = BAN + ".receive";

    public static final String MUTE = PREFIX + "mute";
    public static final String MUTE_TEMPORARY = MUTE + "temporary";
    public static final String MUTE_EXEMPT = MUTE + ".exempt";
    public static final String MUTE_RECEIVE = MUTE + ".receive";
}
