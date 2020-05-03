package codes.benh.velocitymc.discord.commands;

import java.awt.*;

import codes.benh.velocitymc.Main;
import codes.benh.velocitymc.discord.base.BaseCommand;
import codes.benh.velocitymc.utils.DiscordUtils;
import codes.benh.velocitymc.utils.Messages;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class LinkCommand extends BaseCommand {

    public LinkCommand() {
        super("Links a Minecraft account to a discord account", "link", Main.getInstance().getConfig().getString("Discord.Permissions.Link"));
    }

    @Override
    public void run(GuildMessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().trim().split(" ");

        if (args.length != 2) {
            event.getChannel()
                    .sendMessage(generateEmbed(event.getChannel(), "Invalid Usage. Please type `" + Main.getInstance().getConfig().getString("Discord.Prefix") + "link <username>` to link your account", Color.RED).build())
                    .queue(m -> deleteMessageTimed(m, 5));
        }
        else {
            String username = args[1];
            ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(username);
            if (proxiedPlayer == null) {
                event.getChannel()
                        .sendMessage(generateEmbed(event.getChannel(), "Could not find a user with the username `" + username + "` on the network. Please make sure you are online!", Color.RED).build())
                        .queue(m -> deleteMessageTimed(m, 5));
            }
            else {
                String token = DiscordUtils.generateDiscordVerificationCode(proxiedPlayer);
                if (token != null) {
                    sendMessage(proxiedPlayer, Messages.get(Messages.DISCORD_LINK_NOTIFICATION).replaceAll("%token%", token), true);
                    event.getChannel()
                            .sendMessage(generateEmbed(event.getChannel(), "Please check in-game for your verification code.", Color.CYAN).build())
                            .queue(m -> deleteMessageTimed(m, 10));
                }
            }
        }
        event.getMessage().delete().queue();
    }
}
