package dev.vsuite.bungee.discord.runnables;

import dev.vsuite.bungee.Main;
import net.dv8tion.jda.api.entities.Activity;
import net.md_5.bungee.api.ProxyServer;

public class ActivityRunnable implements Runnable {
    @Override
    public void run() {
        Main.getJda().getPresence().setActivity(Activity.watching("over " + ProxyServer.getInstance().getPlayers().size() + " player(s)"));
    }
}
