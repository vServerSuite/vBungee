package codes.benh.velocitymc.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import codes.benh.velocitymc.Main;
import codes.benh.velocitymc.base.BaseCommand;
import codes.benh.velocitymc.utils.DateUtil;
import codes.benh.velocitymc.utils.Permissions;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

public class RestartCommand extends BaseCommand {

    private ScheduledTask task = null;

    public RestartCommand() {
        super("grestart", Permissions.RESTART);
    }

    public void execute(CommandSender cs, String[] args) {
        if (args.length < 2) {
            sendMessage(cs, "Usage: /grestart <time> <reason>", true);
        }
        else {
            if (args[0].equalsIgnoreCase("cancel")) {
                if (task != null) {
                    task.cancel();
                    task = null;
                    ProxyServer.getInstance().getPlayers().forEach(p -> sendMessage(p, "Network restart has been cancelled by &e" + cs.getName() + "&7.", true));
                }
            }
            else {
                if (task != null) {
                    sendMessage(cs, "There is already a restart scheduled for this server", true);
                }
                else {
                    try {
                        final long expiresCheck = DateUtil.parseDateDiff(args[0], true);
                        args[0] = "";
                        final String restartReason = String.join(" ", args).trim();
                        if (expiresCheck == -1) {
                            sendMessage(cs, "Cannot parse &e" + args[0] + "&7 as time.", true);
                        }
                        else {
                            ProxyServer.getInstance().getPlayers().forEach(p -> sendMessage(p, "Network will be restarting in &e" + DateUtil.getDifferenceFormat(expiresCheck).trim() + "&7 with the reason &e" + restartReason + "&7.", true));
                            task = Main.getInstance().getProxy().getScheduler().schedule(Main.getInstance(), () -> {
                                long date = DateUtil.getDifference(expiresCheck);

                                if (date <= 600 && date % 60 == 0 && date != 0
                                        || date == 15 || date == 5 || date == 4
                                        || date == 3 || date == 2 || date == 1) {
                                    ProxyServer.getInstance().getPlayers().forEach(p -> sendMessage(p, "Network will be restarting in &e" + DateUtil.getDifferenceFormat(expiresCheck).trim() + "&7 with the reason &e" + restartReason + "&7.", true));
                                }
                                if (date == 0) {
                                    if (task != null) {
                                        task.cancel();
                                    }

                                    ProxyServer.getInstance().getPlayers().forEach(p -> sendMessage(p, "Network is restarting...", true));

                                    ProxyServer.getInstance().getScheduler().schedule(Main.getInstance(), () -> ProxyServer.getInstance().getPlayers().forEach(p -> p.disconnect(new TextComponent("§eNetwork Restarting... Please Join Back"))), 2, TimeUnit.SECONDS);

                                    ProxyServer.getInstance().getScheduler().schedule(Main.getInstance(), () -> {
                                        HttpPost request = new HttpPost(Main.getInstance().getConfig().getString("Pterodactyl.BaseApiUrl") + "client/servers/" + Main.getInstance().getConfig().getString("Pterodactyl.ServerId") + "/power");
                                        request.addHeader("Authorization", "Bearer " + Main.getInstance().getConfig().getString("Pterodactyl.ApiKey"));

                                        List<NameValuePair> urlParameters = new ArrayList<>();
                                        urlParameters.add(new BasicNameValuePair("signal", "restart"));

                                        try {
                                            request.setEntity(new UrlEncodedFormEntity(urlParameters));

                                            CloseableHttpClient httpClient = HttpClients.createDefault();
                                            httpClient.execute(request);
                                        }
                                        catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }, 5, TimeUnit.SECONDS);
                                }
                            }, 1, 1, TimeUnit.SECONDS);
                        }
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }
}
