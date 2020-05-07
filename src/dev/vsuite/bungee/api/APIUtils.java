package dev.vsuite.bungee.api;

import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

import dev.vsuite.bungee.Main;
import dev.vsuite.bungee.api.responses.PunishmentApiResponse;
import dev.vsuite.bungee.api.responses.base.BaseApiResponse;
import dev.vsuite.bungee.models.Player;
import dev.vsuite.bungee.models.punishments.PunishmentType;
import dev.vsuite.bungee.utils.DateUtil;
import dev.vsuite.bungee.utils.DiscordUtils;
import dev.vsuite.bungee.utils.Messages;
import dev.vsuite.bungee.utils.Permissions;
import io.javalin.Javalin;
import io.javalin.core.JavalinConfig;
import io.javalin.http.Context;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.apache.commons.lang3.RandomStringUtils;

public class APIUtils {
    /**
     * Sets up the API and enables all of the settings that need to be enabled
     */
    public static void setupAPI() {
        if (Main.getInstance().getConfig().getString("WebAPI.Secret").equals("")) {
            Main.getInstance().getConfig().set("WebAPI.Secret", Base64.getEncoder().encodeToString(RandomStringUtils.random(100, true, true).getBytes()));
        }
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(Main.class.getClassLoader());
        Javalin app = Javalin.create(JavalinConfig::enableCorsForAllOrigins).start(Main.getInstance().getConfig().getInt("WebAPI.Port"));
        Thread.currentThread().setContextClassLoader(classLoader);

        setupBanPlayerEndpoint(app);
        setupPlayerStatusEndpoint(app);
    }

    /**
     * Runs a check to see whether the API secret is correct
     *
     * @param context - The context of the API request that was carried out
     * @return - Whether the request contained the access header or not
     */
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

    /**
     * Sets up the ban endpoint for usage on the web panel
     *
     * @param app - The Javalin instance to be passed in to tie the post request to the same url
     */
    private static void setupBanPlayerEndpoint(Javalin app) {
        app.post("/ban/:uuid", context -> {
            if (accessHeadersMatch(context)) {
                String banReason = context.formParam("reason");
                String staffMember = context.formParam("staff");
                String expiryDate = context.formParam("expiry_date");
                UUID uuid = UUID.fromString(context.pathParam("uuid"));
                Player player = Player.get(uuid);
                long parsedExpiryDate = Long.parseLong(Objects.requireNonNull(expiryDate));

                if (player.exists()) {
                    ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(uuid);
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
                                                .replaceAll("%expiry_date%", parsedExpiryDate == -1 ? "never" : DateUtil.format(parsedExpiryDate));
                                        proxiedPlayer.disconnect(new TextComponent(ChatColor.translateAlternateColorCodes('&', banMessage)));
                                    }
                                    String newBanId = player.punish(PunishmentType.BAN, staffMember, banReason, expiryDate);

                                    DiscordUtils.logPunishment(PunishmentType.BAN, newBanId, player, staffMember, banReason, parsedExpiryDate);
                                    String banAlert = Messages.get(Messages.BAN_ALERT)
                                            .replaceAll("%staff%", staffMember)
                                            .replaceAll("%player%", player.getUsername())
                                            .replaceAll("%reason%", banReason)
                                            .replaceAll("%expiry_date%", parsedExpiryDate == -1 ? "never" : DateUtil.format(parsedExpiryDate));
                                    BaseComponent[] message = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', Messages.get(Messages.PREFIX) + banAlert));
                                    ProxyServer.getInstance().getPlayers().stream()
                                            .filter(p -> p.hasPermission(Permissions.BAN_RECEIVE))
                                            .forEach(p -> p.sendMessage(message));

                                    return new PunishmentApiResponse(200, "Player has been banned", true, PunishmentType.BAN, null);
                                }
                            }));
                }
            }
        });
    }

    /**
     * Sets up the player status endpoint for usage on the web panel
     *
     * @param app - The Javalin instance to be passed in to tie the get request to the same url
     */
    private static void setupPlayerStatusEndpoint(Javalin app) {
        app.get("/playerstatus/:username", ctx -> {
            if (accessHeadersMatch(ctx)) {
                ctx.json(new BaseApiResponse(200, ProxyServer.getInstance().getPlayer(ctx.pathParam("username")) == null ? "Offline" : "Online"));
            }
        });
    }
}
