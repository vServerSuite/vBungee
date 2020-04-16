package codes.benh.velocitymc.commands.punishments.core;

import java.text.SimpleDateFormat;
import java.util.Date;

import codes.benh.velocitymc.Main;
import codes.benh.velocitymc.base.BaseCommand;
import codes.benh.velocitymc.models.Player;
import codes.benh.velocitymc.utils.Messages;
import codes.benh.velocitymc.utils.Permissions;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class LookupCommand extends BaseCommand {

    public LookupCommand() {
        super("lookup", Permissions.LOOKUP);
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (args.length == 0) {
            sendMessage(commandSender, Messages.get(Messages.LOOKUP_INVALID_USAGE), true);
        }
        else {
            Player player;

            if (ProxyServer.getInstance().getPlayer(args[0]) != null) {
                player = Player.get(ProxyServer.getInstance().getPlayer(args[0]));
            }
            else {
                player = Player.get(args[0]);
            }
            if (player != null && player.exists()) {
                Date firstLogin = new Date(player.getFirstLogin());
                Date lastLogin = new Date(player.getLastLogin());

                sendMessage(commandSender, "&7&lLookup Information for &e&l" + player.getUsername() + "&7:", false);
                sendMessage(commandSender, "&7First Login &8» &e" + new SimpleDateFormat("dd-MM-yyyy '&7@&e' HH:mm").format(firstLogin), false);
                sendMessage(commandSender, "&7Last Login &8» &e" + new SimpleDateFormat("dd-MM-yyyy '&7@&e' HH:mm").format(lastLogin), false);
                if(player.getProxiedPlayer() != null) {
                    sendMessage(commandSender, "&7Current Server &8» &e" + player.getProxiedPlayer().getServer().getInfo().getName(), false);
                }else {
                    sendMessage(commandSender, "&7Current Server &8» &cOffline", false);
                }

                if (player.getBans().size() > 0) {
                    sendMessage(commandSender, "&7&l&m------------------", false);
                    sendMessage(commandSender, "&7Ban Logs:", false);
                    int[] i = new int[1];
                    i[0] = 1;
                    player.getBans().forEach(ban -> {
                        TextComponent textComponent = new TextComponent(translateColorCodes("&e" + i[0] + "&7. " + (ban.isActive() ? "&a&lACTIVE" : "&c&lINACTIVE") + "&f&l - &7Banned by &e" + ban.getStaff() + "&7 for &e" + ban.getReason() + "&7. This ban was &e" + (ban.isTemporary() ? "temporary" : "permanent") + "&7"));
                        String hoverText = translateColorCodes(
                                "&8» &7Ban Id: &e" + ban.getId()
                                        + "\n&8» &7Ban Staff: &e" + ban.getStaff()
                                        + "\n&8» &7Ban Active: &e" + (ban.isActive() ? "&atrue" : "&cfalse")
                                        + "\n&8» &7Ban Start: &e" + new SimpleDateFormat("dd-MM-yyyy '&7@&e' HH:mm").format(ban.getDateStarted())
                                        + "\n&8» &7Ban End: &e" + new SimpleDateFormat("dd-MM-yyyy '&7@&e' HH:mm").format(ban.getBanEndDate())
                        );
                        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverText).create()));
                        sendMessage(commandSender, textComponent);
                        i[0]++;
                    });
                }
                if (player.getKicks().size() > 0) {
                    sendMessage(commandSender, "&7&l&m------------------", false);
                    sendMessage(commandSender, "&7Kick Logs:", false);
                    int[] i = new int[1];
                    i[0] = 1;
                    player.getKicks().forEach(kick -> {
                        TextComponent textComponent = new TextComponent(translateColorCodes("&e" + i[0] + "&7. Kicked by &e" + kick.getStaff() + "&7 for &e" + kick.getReason() + "&7."));
                        String hoverText = translateColorCodes(
                                "&8» &7Kick Id: &e" + kick.getId()
                                        + "\n&8» &7Kick Staff: &e" + kick.getStaff()
                                        + "\n&8» &7Kick Date: &e" + new SimpleDateFormat("dd-MM-yyyy '&7@&e' HH:mm").format(kick.getDate())
                        );
                        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverText).create()));
                        sendMessage(commandSender, textComponent);
                        i[0]++;
                    });
                }
            }
            else {
                sendMessage(commandSender, Messages.get(Messages.PLAYER_NOT_FOUND).replaceAll("%player%", args[0]), true);
            }
        }
    }
}
