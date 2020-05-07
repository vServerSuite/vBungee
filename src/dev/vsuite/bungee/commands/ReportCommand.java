package dev.vsuite.bungee.commands;

import java.util.ArrayList;

import dev.vsuite.bungee.base.BaseCommand;
import dev.vsuite.bungee.models.Player;
import dev.vsuite.bungee.utils.Messages;
import dev.vsuite.bungee.utils.Permissions;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.TabExecutor;

public class ReportCommand extends BaseCommand implements TabExecutor {

    public ReportCommand() {
        super("report", Permissions.REPORT);
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof ProxiedPlayer))
            return;

        Player player = Player.get(commandSender);

        if (args.length < 2) {
            player.sendMessage(Messages.get(Messages.REPORT_INVALID_USAGE), true);
        }
        else {
            Player target = Player.get(args[0]);
            if (target == null || !target.exists() || target.getProxiedPlayer() == null) {
                player.sendMessage(Messages.get(Messages.PLAYER_NOT_FOUND).replaceAll("%player%", args[0]), true);
            }
            else {
                args[0] = "";
                String reportMessage = translateColorCodes(Messages.get(Messages.REPORT_FORMAT)
                        .replaceAll("%prefix%", getPrefix())
                        .replaceAll("%reporter%", player.getUsername())
                        .replaceAll("%reported%", target.getUsername())
                        .replaceAll("%reason%", String.join(" &e", args).trim()));

                TextComponent textComponent = new TextComponent(TextComponent.fromLegacyText(reportMessage));
                textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                        translateColorCodes(Messages.get(Messages.REPORT_FORMAT_HOVER)
                                .replaceAll("%server%", target.getProxiedPlayer().getServer().getInfo().getName()))
                ).create()));
                textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/server " + target.getProxiedPlayer().getServer().getInfo().getName()));

                ProxyServer.getInstance().getPlayers().stream()
                        .filter(proxiedPlayer -> proxiedPlayer.hasPermission(Permissions.REPORT_RECEIVE))
                        .forEach(proxiedPlayer -> sendMessage(proxiedPlayer, textComponent));
                sendMessage(player, Messages.get(Messages.REPORT_CONFIRMATION), true);
            }
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] strings) {
        ArrayList<String> matches = new ArrayList<>();
        if (strings.length == 1) {
            for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                if (p.getName().startsWith(strings[0])) {
                    matches.add(p.getName());
                }
            }
        }
        return matches;
    }
}
