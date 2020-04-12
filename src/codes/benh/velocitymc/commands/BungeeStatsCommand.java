package codes.benh.velocitymc.commands;

import java.lang.management.ManagementFactory;
import java.text.NumberFormat;

import codes.benh.velocitymc.base.BaseCommand;
import codes.benh.velocitymc.runnables.TpsRunnable;
import codes.benh.velocitymc.utils.DateUtil;
import codes.benh.velocitymc.utils.Permissions;
import net.md_5.bungee.api.CommandSender;

public class BungeeStatsCommand extends BaseCommand {

    public BungeeStatsCommand() {
        super("bstats", Permissions.BUNGEE_STATS, "bgc");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        sendMessage(commandSender, "&eBungeeCord Resource Usage:", false);
        sendMessage(commandSender, "&eUptime &8» &e" + DateUtil.formatDateDiff(ManagementFactory.getRuntimeMXBean().getStartTime()), false);
        sendMessage(commandSender, "&eCurrent TPS &8» &e" + (Math.round(TpsRunnable.getTPS() * 100.0) / 100.0) + "&7 TPS.", false);
        sendMessage(commandSender, "&eMaximum Memory &8» &e" + NumberFormat.getInstance().format((Runtime.getRuntime().maxMemory() / 1024 / 1024)) + " &7MB.", false);
        sendMessage(commandSender, "&eAllocated Memory &8» &e" + NumberFormat.getInstance().format((Runtime.getRuntime().totalMemory() / 1024 / 1024)) + " &7MB.", false);
        sendMessage(commandSender, "&eFree Memory&7: &e" + NumberFormat.getInstance().format((Runtime.getRuntime().freeMemory() / 1024 / 1024)) + " &7MB.", false);
    }
}
