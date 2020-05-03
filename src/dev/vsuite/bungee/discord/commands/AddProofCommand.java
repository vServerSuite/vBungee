package dev.vsuite.bungee.discord.commands;

import java.awt.*;

import dev.vsuite.bungee.Main;
import dev.vsuite.bungee.discord.base.BaseCommand;
import dev.vsuite.bungee.models.punishments.Punishment;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class AddProofCommand extends BaseCommand {

    public AddProofCommand() {
        super("Adds proof to a punishments message", "addproof", Main.getInstance().getConfig().getString("Discord.Permissions.AddProof"));
    }

    @Override
    public void run(GuildMessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().trim().split(" ");

        if (args.length != 3) {
            event.getChannel()
                    .sendMessage(generateEmbed(event.getChannel(), "Invalid Usage. Please type `" + Main.getInstance().getConfig().getString("Discord.Prefix") + "addproof <banId> <proofUrl>` to add proof to a punishment log", Color.RED).build())
                    .queue(m -> deleteMessageTimed(m, 5));
        }
        else {
            String punishmentId = Punishment.getDiscordMessageId(args[1]);
            String proofLink = args[2];

            if (punishmentId != null) {
                event.getChannel().retrieveMessageById(punishmentId).queue(message -> {
                    EmbedBuilder newEmbed = new EmbedBuilder(message.getEmbeds().get(0));
                    newEmbed.setDescription("Proof Link: " + proofLink);
                    newEmbed.setColor(Color.GREEN);
                    message.editMessage(newEmbed.build()).queue();
                    event.getChannel().sendMessage(generateEmbed(event.getChannel(), "Added proof for punishment Id " + args[1], Color.CYAN).build()).queue(m -> deleteMessageTimed(m, 5));
                });
            }
            else {
                event.getChannel().sendMessage(generateEmbed(event.getChannel(), "Could not find a punishment with the Id '" + args[1] + "'", Color.ORANGE).build()).queue(m -> deleteMessageTimed(m, 5));
            }
        }
        event.getMessage().delete().queue();
    }
}
