package codes.benh.velocitymc.utils;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import codes.benh.velocitymc.Main;
import codes.benh.velocitymc.models.Player;
import codes.benh.velocitymc.models.punishments.PunishmentType;
import jdk.internal.jline.internal.Nullable;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class DiscordUtils {
    public static String generateDiscordVerificationCode(ProxiedPlayer player) {
        if (Main.getJda() != null) {
            String token = getUniqueToken();
            try {
                Main.getMySQL().update("INSERT INTO DiscordVerificationTokens (`verification_token`, `verification_uuid`, `verification_date`)" +
                        "VALUES ('" + token + "', '" + player.getUniqueId() + "', '" + System.currentTimeMillis() + "')");
                return token;
            }
            catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
        else {
            return null;
        }
    }

    public static void logPunishment(PunishmentType type, String punishmentId, Player player, Object staff, String reason, long expiry) {
        if (Main.getJda() != null) {
            String staffMember = "";
            if (staff instanceof Player) {
                staffMember = ((Player) staff).getUsername().replaceAll("_", "\\_");
                if (((Player) staff).getDiscordId() != null) {
                    User u = Main.getJda().getUserById(((Player) staff).getDiscordId());
                    if (u != null) {
                        staffMember = u.getAsMention();
                    }
                }
            }
            else if (staff instanceof CommandSender) {
                staffMember = ((CommandSender) staff).getName().replaceAll("_", "\\_");
            }

            String finalStaffMember = staffMember;
            getLogsChannel().sendMessage(generateEmbed(type, punishmentId, player, staffMember, reason, expiry, null).build()).queue(message -> {
                updateDiscordPunishmentId(type, punishmentId, message.getId());
            });
        }
    }

    public static void logPunishment(PunishmentType type, String punishmentId, Player player, String staffMember, String reason, long expiry) {
        if (Main.getJda() != null) {
            getLogsChannel().sendMessage(generateEmbed(type, punishmentId, player, staffMember, reason, expiry, null).build()).queue(message -> {
                updateDiscordPunishmentId(type, punishmentId, message.getId());
            });
        }
    }

    private static EmbedBuilder generateEmbed(PunishmentType type, String punishmentId, Player player, String staff, String reason, long expiry, @Nullable String proofLink) {
        EmbedBuilder builder = new EmbedBuilder();

        String user = player.getUsername().replaceAll("_", "\\_");
        if (player.getDiscordId() != null) {
            User u = Main.getJda().getUserById(player.getDiscordId());
            if (u != null) {
                user = u.getAsMention();
            }
        }

        builder.setColor(proofLink != null ? Color.GREEN : Color.RED);
        builder.setAuthor("vBungee", "https://vsuite.dev", getLogsChannel().getGuild().getIconUrl());
        builder.setThumbnail("https://visage.surgeplay.com/bust/" + player.getUUID().toString().replaceAll("-", "") + ".png");

        if (proofLink != null) {
            builder.setDescription("Proof Link: " + proofLink);
        }
        else {
            builder.setDescription("**" + staff + "** - Please use `" + Main.getInstance().getConfig().getString("Discord.Prefix") + "addproof " + punishmentId + "` to add a link to the proof for the punishment");
        }

        builder.addField("Punishment Type", type.toString(), true);
        builder.addField("Punished User", user, true);
        builder.addField("Punishment Reason", reason, true);
        builder.addField("Expiry Date", expiry == -1 ? "Never" : new SimpleDateFormat("dd-MM-yyyy '@' HH:mm").format(new Date(expiry)), true);

        return builder;
    }

    private static TextChannel getLogsChannel() {
        return Main.getJda().getTextChannelById(Main.getInstance().getConfig().getString("Discord.Ids.Punishments"));
    }

    private static void updateDiscordPunishmentId(PunishmentType type, String id, String messageId) {
        try {
            Main.getMySQL().update("UPDATE Punishments SET punishment_discord_message_id='" + messageId + "' WHERE punishment_id='" + id + "'");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static String getUniqueToken() {
        String returnValue = generateToken();

        while (Objects.requireNonNull(getIds()).contains(returnValue)) {
            returnValue = generateToken();
        }

        return generateToken();
    }

    private static java.util.List<String> getIds() {

        try {
            List<String> returnValue = new ArrayList<>();
            ResultSet resultSet = Main.getMySQL().query("SELECT verification_token from DiscordVerificationTokens");
            if (resultSet != null) {
                while (resultSet.next()) {
                    returnValue.add(resultSet.getString("verification_token"));
                }
            }
            return returnValue;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String generateToken() {
        final String ALPHA_NUMERIC_STRING = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        int count = 5;
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }

        return builder.toString();
    }
}
