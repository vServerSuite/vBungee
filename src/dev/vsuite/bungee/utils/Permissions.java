package dev.vsuite.bungee.utils;

public class Permissions {
    private static final String PREFIX = "vSuite.";

    public static final String STAFF_CHAT = PREFIX + "staffchat";
    public static final String STAFF_CHAT_RECEIVE = STAFF_CHAT + "receive";

    public static final String REPORT = PREFIX + "report";
    public static final String REPORT_RECEIVE = REPORT + "receive";

    public static final String BAN = PREFIX + "ban";
    public static final String BAN_TEMPORARY = BAN + "temporary";
    public static final String BAN_EXEMPT = BAN + ".exempt";
    public static final String BAN_RECEIVE = BAN + ".receive";

    public static final String UNBAN = PREFIX + "unban";
    public static final String UNBAN_RECEIVE = UNBAN + ".receive";

    public static final String MUTE = PREFIX + "mute";
    public static final String MUTE_TEMPORARY = MUTE + "temporary";
    public static final String MUTE_EXEMPT = MUTE + ".exempt";
    public static final String MUTE_RECEIVE = MUTE + ".receive";

    public static final String UNMUTE = PREFIX + "unmute";
    public static final String UNMUTE_RECEIVE = UNMUTE + ".receive";

    public static final String KICK = PREFIX + "kick";
    public static final String KICK_EXEMPT = KICK + ".exempt";
    public static final String KICK_RECEIVE = KICK + ".receive";


    public static final String BUNGEE_STATS = PREFIX + "stats";

    public static final String LOBBY = PREFIX + "lobby";
}
