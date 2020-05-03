package codes.benh.velocitymc.commands;

import java.util.ArrayList;

import codes.benh.velocitymc.base.BaseCommand;
import codes.benh.velocitymc.utils.Messages;
import codes.benh.velocitymc.utils.Permissions;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
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

        ProxiedPlayer player = (ProxiedPlayer) commandSender;

        if (args.length < 2) {
            sendMessage(player, Messages.get(Messages.REPORT_INVALID_USAGE), true);
        }
        else {
            if (ProxyServer.getInstance().getPlayer(args[0]) == null) {
                sendMessage(player, Messages.get(Messages.PLAYER_NOT_FOUND).replaceAll("%player%", args[0]), true);
            }
            else {
                ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);

                ProxyServer.getInstance().getPlayers().forEach(proxiedPlayer -> {
                    if (proxiedPlayer.hasPermission(Permissions.REPORT_RECEIVE)) {
                        args[0] = "";
                        String reportMessage = translateColorCodes(Messages.get(Messages.REPORT_FORMAT)
                                .replaceAll("%prefix%", getPrefix())
                                .replaceAll("%reporter%", player.getName())
                                .replaceAll("%reported%", target.getName())
                                .replaceAll("%reason%", String.join(" &e", args).trim()));

                        BaseComponent[] component = TextComponent.fromLegacyText(reportMessage);
                        TextComponent textComponent = new TextComponent(component);
                        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                                translateColorCodes(Messages.get(Messages.REPORT_FORMAT_HOVER)
                                        .replaceAll("%server%", target.getServer().getInfo().getName()))
                        ).create()));
                        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/server " + target.getServer().getInfo().getName()));

                        sendMessage(proxiedPlayer, textComponent);
                    }
                });
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
