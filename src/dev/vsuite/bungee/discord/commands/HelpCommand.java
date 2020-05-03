package dev.vsuite.bungee.discord.commands;

import java.awt.*;
import java.util.Collection;

import dev.vsuite.bungee.Main;
import dev.vsuite.bungee.discord.base.BaseCommand;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class HelpCommand extends BaseCommand {
    private Collection<BaseCommand> commands;

    public HelpCommand(Collection<BaseCommand> commands) {
        super("Opens this menu", "help", "everyone");
        this.commands = commands;
    }

    @Override
    public void run(GuildMessageReceivedEvent event) {
        StringBuilder helpMessages = new StringBuilder();

        for (BaseCommand command : commands) {
            if (hasPermission(event.getGuild(), event.getMember(), command.roles)) {
                helpMessages.append("**").append(Main.getInstance().getConfig().getString("Discord.Prefix")).append(command.command).append("** - ").append(command.helpMessage).append("\n");
            }
        }

        event.getChannel().sendMessage(generateEmbed(event.getChannel(), helpMessages.toString(), Color.CYAN).build()).queue();
        event.getMessage().delete().queue();
    }
}
