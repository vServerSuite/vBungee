package dev.vsuite.bungee.discord.base;

import java.awt.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import dev.vsuite.bungee.Main;
import dev.vsuite.bungee.models.Player;
import dev.vsuite.bungee.utils.Messages;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BaseCommand {

    public String helpMessage;
    public String command;
    public String[] roles;
    protected JDA jda;

    public BaseCommand(String helpMessage, String command, String... rolesRequired) {
        this.helpMessage = helpMessage;
        this.command = command;
        this.roles = rolesRequired;
        this.jda = Main.getJda();
    }

    protected boolean hasPermission(Guild guild, Member member) {
        return hasPermission(guild, member, roles);
    }

    protected boolean hasPermission(Guild guild, Member member, String... specificRoles) {
        boolean returnValue = false;
        for (String role : specificRoles) {
            if (role.equalsIgnoreCase("everyone")) {
                returnValue = true;
            }
            else {
                List<Role> gRoles = guild.getRolesByName(role, true);
                for (Role gRole : gRoles) {
                    if (member.getRoles().contains(gRole)) {
                        returnValue = true;
                    }
                }
            }
        }

        return returnValue;
    }

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

    public void run(GuildMessageReceivedEvent event) {
        // Blank for template
    }

    protected void sendMessage(Player player, String message, boolean prefixUsed) {
        BaseComponent[] component = net.md_5.bungee.api.chat.TextComponent.fromLegacyText(prefixUsed ? getPrefix() + translateColorCodes(message) : translateColorCodes(message));
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
