package dev.vsuite.bungee.listeners;

import dev.vsuite.bungee.models.logs.Log;
import dev.vsuite.bungee.models.logs.LogType;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class LogsListener implements Listener {

    @EventHandler
    public void onJoin(PostLoginEvent ev) {
        Log.addLog(LogType.JOIN, ev.getPlayer().getUniqueId(), ev.getPlayer().getSocketAddress(), System.currentTimeMillis(), ev.getPlayer().getServer(), ev.getPlayer().getName() + " Logged in");
    }

    @EventHandler
    public void onLeave(PlayerDisconnectEvent ev) {
        Log.addLog(LogType.LEAVE, ev.getPlayer().getUniqueId(), ev.getPlayer().getSocketAddress(), System.currentTimeMillis(), ev.getPlayer().getServer(), ev.getPlayer().getName() + " Disconnected");
    }

    @EventHandler
    public void onChat(ChatEvent ev) {
        Log.addLog(ev.getMessage().startsWith("/") ? LogType.COMMAND : LogType.CHAT, ((ProxiedPlayer) ev.getSender()).getUniqueId(), ev.getSender().getSocketAddress(), System.currentTimeMillis(), ((ProxiedPlayer) ev.getSender()).getServer(), ev.getMessage());
    }

    @EventHandler
    public void onServerSwitch(ServerSwitchEvent ev) {
        Log.addLog(LogType.SERVER_SWITCH, ev.getPlayer().getUniqueId(), ev.getPlayer().getSocketAddress(), System.currentTimeMillis(), ev.getPlayer().getServer(), "Server switched from " + (ev.getFrom() != null ? ev.getFrom().getName() : "N/A") + " to " + ev.getPlayer().getServer().getInfo().getName());
    }
}
