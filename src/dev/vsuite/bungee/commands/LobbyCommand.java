package dev.vsuite.bungee.commands;

import java.util.List;
import java.util.Random;

import dev.vsuite.bungee.Main;
import dev.vsuite.bungee.base.BaseCommand;
import dev.vsuite.bungee.utils.Messages;
import dev.vsuite.bungee.utils.Permissions;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class LobbyCommand extends BaseCommand {

    public LobbyCommand() {
        super("lobby", Permissions.LOBBY, "hub");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof ProxiedPlayer))
            return;

        ProxiedPlayer player = (ProxiedPlayer) commandSender;
        List<String> lobbies = Main.getInstance().getConfig().getStringList("Lobbies");

        if (args.length == 0) {
            Random rand = new Random();
            ServerInfo chosenLobby = ProxyServer.getInstance().getServerInfo(lobbies.get(rand.nextInt(lobbies.size())));
            player.connect(chosenLobby);
            String messageToSend = Messages.get(Messages.SERVER_CONNECT)
                    .replaceAll("%server%", chosenLobby.getName());
            sendMessage(player, messageToSend, true);
        }
        else {
            if (ProxyServer.getInstance().getServerInfo(args[0]) == null) {
                String messageToSend = Messages.get(Messages.SERVER_NOT_FOUND)
                        .replaceAll("%server%", args[0]);
                sendMessage(player, messageToSend, true);
            }
            else {
                player.connect(Main.getInstance().getProxy().getServerInfo(args[0]));
                String messageToSend = Messages.get(Messages.SERVER_CONNECT)
                        .replaceAll("%server%", args[0]);
                sendMessage(player, messageToSend, true);
            }
        }
    }
}
