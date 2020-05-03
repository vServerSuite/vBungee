package dev.vsuite.bungee.discord.base;

import java.util.HashMap;
import java.util.Map;

import dev.vsuite.bungee.Main;
import dev.vsuite.bungee.discord.commands.AddProofCommand;
import dev.vsuite.bungee.discord.commands.HelpCommand;
import dev.vsuite.bungee.discord.commands.LinkCommand;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandHandler extends ListenerAdapter {

    private Map<String, BaseCommand> commandList = new HashMap<>();

    public CommandHandler() {
        commandList.put("addproof", new AddProofCommand());
        commandList.put("link", new LinkCommand());
        commandList.put("help", new HelpCommand(commandList.values()));
    }

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().startsWith(Main.getInstance().getConfig().getString("Discord.Prefix"))) {
            String[] args = event.getMessage().getContentRaw().split(" ");
            String command = args[0].replace(Main.getInstance().getConfig().getString("Discord.Prefix"), "");
            if (commandList.get(command) != null) {
                commandList.get(command).run(event);
            }
        }
    }
}
