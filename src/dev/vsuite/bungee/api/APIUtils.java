package codes.benh.velocitymc.api;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import codes.benh.velocitymc.Main;
import codes.benh.velocitymc.api.responses.PunishmentApiResponse;
import codes.benh.velocitymc.api.responses.base.BaseApiResponse;
import codes.benh.velocitymc.models.Player;
import codes.benh.velocitymc.models.punishments.PunishmentType;
import codes.benh.velocitymc.utils.DiscordUtils;
import codes.benh.velocitymc.utils.Messages;
import codes.benh.velocitymc.utils.Permissions;
import io.javalin.Javalin;
import io.javalin.core.JavalinConfig;
import io.javalin.http.Context;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class APIUtils {
    public static void setupAPI() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(Main.class.getClassLoader());
        Javalin app = Javalin.create(JavalinConfig::enableCorsForAllOrigins).start(Main.getInstance().getConfig().getInt("WebAPI.Port"));
        Thread.currentThread().setContextClassLoader(classLoader);

        setupBanPlayerEndpoint(app);
        setupPlayerStatusEndpoint(app);
    }

    private static boolean accessHeadersMatch(Context context) {
        boolean returnValue = false;
        if (!Objects.equals(context.header("Authorization"), "Basic " + Main.getInstance().getConfig().getString("WebAPI.Secret"))) {
            context.json(new BaseApiResponse(401, "Incorrect Authorization Header"));
        }
        else {
            returnValue = true;
        }

        return returnValue;
    }

    private static void setupBanPlayerEndpoint(Javalin app) {
        app.post("/ban/:uuid", context -> {
            if (accessHeadersMatch(context)) {
                String banReason = context.formParam("reason");
                String staffMember = context.formParam("staff");
                long expiryDate = -1;
                if (!Objects.equals(context.formParam("expiry_date"), "-1")) {
                    expiryDate = Long.parseLong(Objects.requireNonNull(context.formParam("expiry_date")));
                }
                UUID uuid = UUID.fromString(context.pathParam("uuid"));
                Player player = Player.get(uuid);
                if (player.exists()) {
                    ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(uuid);
                    long finalExpiryDate = expiryDate;
                    context.json(player.hasPermission(Permissions.BAN_EXEMPT)
                            .thenApplyAsync(result -> {
                                if (result) {
                                    return new PunishmentApiResponse(400, "Insufficient Permissions", false, PunishmentType.BAN, player.getUsername() + " is exempt from being banned");
                                }
                                else {
                                    if (proxiedPlayer != null) {
                                        String banMessage = Messages.get(Messages.BAN)
                                                .replaceAll("%reason%", banReason)
                                                .replaceAll("%staff%", staffMember)
                                                .replaceAll("%expiry_date%", finalExpiryDate == -1 ? "never" : new SimpleDateFormat("dd-MM-yyyy '&7@&e' HH:mm:ss").format(new Date(finalExpiryDate)));
                                        proxiedPlayer.disconnect(new TextComponent(ChatColor.translateAlternateColorCodes('&', banMessage)));
                                    }

                                    String newBanId = player.logPunishment(PunishmentType.BAN, staffMember, banReason, finalExpiryDate);
                                    DiscordUtils.logPunishment(PunishmentType.BAN, newBanId, player, staffMember, banReason, finalExpiryDate);
                                    String banAlert = Messages.get(Messages.BAN_ALERT)
                                            .replaceAll("%staff%", staffMember)
                                            .replaceAll("%player%", player.getUsername())
                                            .replaceAll("%reason%", banReason)
                                            .replaceAll("%expiry_date%", finalExpiryDate == -1 ? "never" : new SimpleDateFormat("dd-MM-yyyy '&7@&e' HH:mm:ss").format(new Date(finalExpiryDate)));
                                    BaseComponent[] message = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', Messages.get(Messages.PREFIX) + banAlert));
                                    ProxyServer.getInstance().getPlayers().forEach(p -> {
                                        if (p.hasPermission(Permissions.BAN_RECEIVE)) {
                                            p.sendMessage(message);
                                        }
                                    });
                                    return new PunishmentApiResponse(200, "Player has been banned", true, PunishmentType.BAN, null);
                                }
                            }));
                }
            }
        });
    }

    private static void setupPlayerStatusEndpoint(Javalin app) {
        app.get("/playerstatus/:username", ctx -> {
            if (accessHeadersMatch(ctx)) {
                ctx.json(new BaseApiResponse(200, ProxyServer.getInstance().getPlayer(ctx.pathParam("username")) == null ? "Offline" : "Online"));
            }
        });
    }
}
