package codes.benh.velocitymc.discord.base;

import java.awt.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import codes.benh.velocitymc.Main;
import codes.benh.velocitymc.models.Player;
import codes.benh.velocitymc.utils.Messages;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public abstract class BaseListener extends ListenerAdapter {

    protected void accessDenied(Member member, TextChannel channel) {
        EmbedBuilder builder = generateEmbed(channel, member.getAsMention() + ", You do not have permission to use this command.", Color.RED);
        channel.sendMessage(builder.build()).queue(m -> deleteMessageTimed(m, 5));
    }

    protected void deleteMessageTimed(Message m, int seconds) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                m.delete().queue();
            }
        }, 1000 * seconds);
    }

    protected EmbedBuilder generateEmbed(PrivateChannel channel, String description, Color color) {
        EmbedBuilder builder = new EmbedBuilder();

        builder.setAuthor("vBungee", "https://vSuite.dev/?ref=vBungee", channel.getUser().getAvatarUrl());
        builder.setDescription(description);
        builder.setColor(color);

        return builder;
    }

    protected EmbedBuilder generateEmbed(TextChannel channel, String description, Color color) {
        EmbedBuilder builder = new EmbedBuilder();

        builder.setAuthor("vBungee", "https://vSuite.dev/?ref=vBungee", channel.getGuild().getIconUrl());
        builder.setDescription(description);
        builder.setColor(color);

        return builder;
    }

    protected EmbedBuilder generateEmbed(TextChannel channel, String description, Color color, String title) {
        EmbedBuilder builder = generateEmbed(channel, description, color);

        builder.setTitle(title);

        return builder;
    }

    protected void sendMessage(Player player, String message, boolean prefixUsed) {
        BaseComponent[] component = TextComponent.fromLegacyText(prefixUsed ? getPrefix() + translateColorCodes(message) : translateColorCodes(message));
        player.getProxiedPlayer().sendMessage(component);
    }

    protected void sendMessage(ProxiedPlayer player, String message, boolean prefixUsed) {
        BaseComponent[] component = TextComponent.fromLegacyText(prefixUsed ? getPrefix() + translateColorCodes(message) : translateColorCodes(message));
        player.sendMessage(component);
    }

    protected String translateColorCodes(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    protected String getPrefix() {
        return translateColorCodes(Messages.get(Messages.PREFIX));
    }
}
