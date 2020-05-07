package dev.vsuite.bungee.base.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import dev.vsuite.bungee.Main;
import dev.vsuite.bungee.models.punishments.Punishment;
import net.luckperms.api.cacheddata.CachedPermissionData;
import net.luckperms.api.context.ContextManager;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public abstract class BasePlayer extends BaseObject {

    private ProxiedPlayer proxiedPlayer;

    private UUID uuid;

    private String username;

    private String discordId;

    private long firstLogin;

    private long lastLogin;

    private List<Punishment> bans = new ArrayList<>();

    private List<Punishment> mutes = new ArrayList<>();

    public ProxiedPlayer getProxiedPlayer() {
        return proxiedPlayer;
    }

    public void setProxiedPlayer(ProxiedPlayer proxiedPlayer) {
        this.proxiedPlayer = proxiedPlayer;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDiscordId() {
        return discordId;
    }

    public void setDiscordId(String discordId) {
        this.discordId = discordId;
    }

    public long getFirstLogin() {
        return firstLogin;
    }

    public void setFirstLogin(long firstLogin) {
        this.firstLogin = firstLogin;
    }

    public long getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
    }

    public List<Punishment> getBans() {
        return bans;
    }

    public void setBans(List<Punishment> bans) {
        this.bans = bans;
    }

    public List<Punishment> getMutes() {
        return mutes;
    }

    public void setMutes(List<Punishment> mutes) {
        this.mutes = mutes;
    }

    public boolean isBanned() {
        return getBans().stream().anyMatch(Punishment::isActive);
    }

    public boolean isMuted() {
        return getMutes().stream().anyMatch(Punishment::isActive);
    }

    public CompletableFuture<Boolean> hasPermission(String permission) {
        return Main.getLuckPermsAPI().getUserManager().loadUser(getUuid())
                .thenApplyAsync(user -> {
                    ContextManager contextManager = Main.getLuckPermsAPI().getContextManager();
                    CachedPermissionData permissionData = user.getCachedData().getPermissionData(contextManager.getQueryOptions(user).orElseGet(contextManager::getStaticQueryOptions));
                    return permissionData.checkPermission(permission).asBoolean();
                });
    }

    /**
     * Sends a message to a user in-game (with formatting)
     *
     * @param message    - The message to send
     * @param prefixUsed - Whether the prefix should be displayed before the message
     */
    public void sendMessage(String message, boolean prefixUsed) {
        sendMessage(getProxiedPlayer(), message, prefixUsed);
    }
}
