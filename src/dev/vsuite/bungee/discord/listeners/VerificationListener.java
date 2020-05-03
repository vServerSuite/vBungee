package dev.vsuite.bungee.discord.listeners;

import java.awt.*;
import java.util.Collection;

import dev.vsuite.bungee.Main;
import dev.vsuite.bungee.discord.base.BaseListener;
import dev.vsuite.bungee.discord.models.VerificationToken;
import dev.vsuite.bungee.models.Player;
import dev.vsuite.bungee.utils.Messages;
import dev.vsuite.bungee.utils.PlayerUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.luckperms.api.model.group.Group;
import net.md_5.bungee.api.ProxyServer;

public class VerificationListener extends BaseListener {

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().startsWith("verification")) {
            String[] args = event.getMessage().getContentRaw().trim().split(" ");
            if (args.length == 2) {
                VerificationToken verificationToken = VerificationToken.getVerificationToken(args[1]);
                if (verificationToken != null) {
                    Guild guild = Main.getJda().getGuildById(Main.getInstance().getConfig().getString("Discord.Ids.Guild"));
                    if (guild != null) {
                        Member member = guild.getMember(event.getAuthor());
                        if (member != null) {
                            member.modifyNickname(PlayerUtils.getUsernameFromUUID(verificationToken.getUuid())).queue();
                            Player player = Player.get(verificationToken.getUuid());
                            Collection<Group> groups = Main.getLuckPermsAPI().getGroupManager().getLoadedGroups();
                            for (Group group : groups) {
                                player.hasPermission("group." + group.getName()).thenAcceptAsync(result -> {
                                    if (result) {
                                        if (Main.getInstance().getConfig().getString("Discord.RoleSync." + group.getName()) != null) {
                                            Role role = guild.getRoleById(Main.getInstance().getConfig().getString("Discord.RoleSync." + group.getName()));
                                            if (role != null) {
                                                guild.addRoleToMember(member, role).queue();
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    }

                    verificationToken.delete();
                    if (ProxyServer.getInstance().getPlayer(verificationToken.getUuid()) != null) {
                        sendMessage(ProxyServer.getInstance().getPlayer(verificationToken.getUuid()), Messages.get(Messages.DISCORD_LINK_CONFIRMATION).replaceAll("%discord_username%", event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator()), true);
                    }
                    event.getChannel()
                            .sendMessage(generateEmbed(event.getChannel(), "Your account has been linked to the UUID `" + verificationToken.getUuid() + "`", Color.CYAN).build())
                            .queue(m -> deleteMessageTimed(m, 5));
                }
            }
            else {
                event.getChannel()
                        .sendMessage(generateEmbed(event.getChannel(), "Invalid Usage. Please type `verification <verificationToken>` to verify your account", Color.RED).build())
                        .queue(m -> deleteMessageTimed(m, 5));
            }
        }
    }
}
