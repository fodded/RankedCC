package me.fodded.skywarslobby.managers;

import lombok.Getter;
import me.fodded.core.Core;
import me.fodded.core.managers.stats.impl.profile.GeneralStats;
import me.fodded.core.managers.stats.impl.profile.GeneralStatsDataManager;
import me.fodded.skywarslobby.Main;
import me.fodded.skywarslobby.gameplay.guis.CosmeticsGui;
import me.fodded.skywarslobby.gameplay.guis.LobbySelectorGui;
import me.fodded.skywarslobby.gameplay.guis.MinigamesGui;
import me.fodded.skywarslobby.gameplay.guis.settings.SettingsGui;
import me.fodded.skywarslobby.gameplay.scoreboard.SkywarsLobbyScoreboard;
import me.fodded.skywarslobby.gameplay.tasks.UpdateScoreboardTask;
import me.fodded.spigotcore.SpigotCore;
import me.fodded.spigotcore.gameplay.disguise.DisguiseManager;
import me.fodded.spigotcore.gameplay.player.AbstractServerPlayer;
import me.fodded.spigotcore.languages.LanguageManager;
import me.fodded.spigotcore.utils.ItemUtils;
import me.fodded.spigotcore.utils.ServerLocations;
import me.fodded.spigotcore.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.redisson.api.RTopic;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Getter
public class SkywarsLobbyPlayer extends AbstractServerPlayer {

    public SkywarsLobbyPlayer(UUID uniqueId) {
        super(uniqueId);
        addPlayerToCache();
    }

    public void handleJoin() {
        Player player = Bukkit.getPlayer(getUniqueId());
        if(player == null || !player.isOnline()) {
            return;
        }

        updateDisguise();
        resetPlayerAbilities();
        setItemsToPlayerInventory();
        updateVisibilityForEveryone();

        UpdateScoreboardTask updateScoreboardTask = new UpdateScoreboardTask(player);
        updateScoreboardTask.runTaskTimer(SpigotCore.getInstance().getPlugin(), 0, 20L);

        GeneralStatsDataManager.getInstance().applyChangeToRedis(
                getUniqueId(),
                generalStats -> generalStats.setLastLobby(SpigotCore.getInstance().getServerName())
        );
    }

    public void handleQuit() {
        SkywarsLobbyScoreboard skywarsLobbyScoreboard = (SkywarsLobbyScoreboard) SkywarsLobbyScoreboard.getScoreboardManager(getUniqueId());
        if(skywarsLobbyScoreboard != null) {
            skywarsLobbyScoreboard.removeScoreboard();
        }

        removePlayerFromList();
    }

    public void updateDisguise() {
        Player player = Bukkit.getPlayer(getUniqueId());
        GeneralStats generalStats = GeneralStatsDataManager.getInstance().getCachedValue(player.getUniqueId());
        if(!generalStats.getDisguisedName().isEmpty()) {
            DisguiseManager.getInstance().setDisguise(
                    player,
                    generalStats.getDisguisedName(),
                    generalStats.getDisguisedSkinTexture(),
                    generalStats.getDisguisedSkinSignature()
            );
        }
    }

    public void resetPlayerAbilities() {
        Player player = Bukkit.getPlayer(getUniqueId());
        player.teleport(ServerLocations.getInstance().getLobbyLocation());
        player.setAllowFlight(true); // needed for double jump
        player.setGameMode(GameMode.ADVENTURE);
    }

    private void updateVisibilityForEveryone() {
        for(Player eachPlayer : Bukkit.getOnlinePlayers()) {
            SkywarsLobbyPlayer skywarsLobbyPlayer = getLobbyPlayer(eachPlayer.getUniqueId());
            skywarsLobbyPlayer.updateVisibility();
        }
    }

    public void setItemsToPlayerInventory() {
        Player player = Bukkit.getPlayer(getUniqueId());
        if(player == null) {
            return;
        }

        Configuration languageConfig = LanguageManager.getInstance().getLanguageConfig(getUniqueId());
        for(String path : languageConfig.getConfigurationSection("player-items").getKeys(false)) {
            int slot = Integer.parseInt(path) - 1;
            String displayName = getVisibilityStatus(languageConfig.getString("player-items." + path + ".name"));
            Material material = getVisibilityMaterial(languageConfig.getString("player-items." + path + ".material"));
            List<String> descriptionList = languageConfig.getStringList("player-items." + path + ".description");

            player.getInventory().setItem(
                    slot,
                    ItemUtils.getItemStack(material, displayName,descriptionList, false, 1)
            );
        }
    }

    public void updateVisibility() {
        GeneralStats generalStats = GeneralStatsDataManager.getInstance().getCachedValue(getUniqueId());
        if(generalStats.isPlayersVisibility()) {
            showPlayers();
        } else {
            hidePlayers();
        }
    }

    public void openGui(Material holdingMaterial) {
        Player player = Bukkit.getPlayer(getUniqueId());
        GeneralStatsDataManager generalStatsDataManager = GeneralStatsDataManager.getInstance();

        switch(holdingMaterial) {
            case FIREWORK_CHARGE:
                new LobbySelectorGui(player);
                break;
            case MAGMA_CREAM:
                new MinigamesGui(player);
                break;
            case BLAZE_POWDER:
                new CosmeticsGui(player);
                break;
            case GOLD_NUGGET:
                new SettingsGui(player);
                break;
            case GOLDEN_CARROT:
                if(isFlooding()) {
                    return;
                }
                generalStatsDataManager.applyChangeToRedis(getUniqueId(), generalStats -> generalStats.setPlayersVisibility(false));
                hidePlayers();
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), this::setItemsToPlayerInventory, 3);
                break;
            case CARROT_ITEM:
                if(isFlooding()) {
                    return;
                }
                generalStatsDataManager.applyChangeToRedis(getUniqueId(), generalStats -> generalStats.setPlayersVisibility(true));
                showPlayers();
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), this::setItemsToPlayerInventory, 3);
                break;
        }
    }

    private void hidePlayers() {
        Player player = Bukkit.getPlayer(getUniqueId());
        for(Player eachPlayer : Bukkit.getOnlinePlayers()) {
            player.hidePlayer(eachPlayer);
        }
    }

    private void showPlayers() {
        Player player = Bukkit.getPlayer(getUniqueId());
        for(Player eachPlayer : Bukkit.getOnlinePlayers()) {
            GeneralStats eachPlayerGeneralStats = GeneralStatsDataManager.getInstance().getCachedValue(eachPlayer.getUniqueId());

            // we do not show hidden staff to a player unless it's another staff member
            if(!eachPlayerGeneralStats.isVanished() || player.isOp()) {
                player.showPlayer(eachPlayer);
            } else if(eachPlayerGeneralStats.isVanished()) {
                player.hidePlayer(eachPlayer);
            }
        }
    }

    // We publish the information to redis and then catch it from bungee side
    // This method should be used for lobbies only, e.g. Lobby-Skywars-1 would be Skywars
    public void sendPlayerToLobby(String serverNamePattern) {
        CompletableFuture.runAsync(() -> {
            RTopic topic = Core.getInstance().getRedis().getRedissonClient().getTopic("sendPlayerToLobby");
            topic.publish(getUniqueId() + ":" + serverNamePattern);
        });
    }

    public void sendMessage(String configMessageKey) {
        Player player = Bukkit.getPlayer(getUniqueId());
        String message = StringUtils.format(LanguageManager.getInstance().getLanguageConfig(player.getUniqueId()).getString(configMessageKey));
        player.sendMessage(message);
    }

    private Material getVisibilityMaterial(String text) {
        if(!text.contains("/")) {
            return Material.getMaterial(text);
        }

        GeneralStats generalStats = GeneralStatsDataManager.getInstance().getCachedValue(getUniqueId());
        String[] arr = text.split("/");

        if(generalStats.isPlayersVisibility()) {
            return Material.getMaterial(arr[0]);
        }
        return Material.getMaterial(arr[1]);
    }

    private String getVisibilityStatus(String text) {
        GeneralStats generalStats = GeneralStatsDataManager.getInstance().getCachedValue(getUniqueId());
        return text.replace("%visibility%", generalStats.isPlayersVisibility() ? "Visible" : "Hidden");
    }

    public static SkywarsLobbyPlayer getLobbyPlayer(UUID uniqueId) {
        if(isInCache(uniqueId)) {
            return (SkywarsLobbyPlayer) AbstractServerPlayer.getPlayerFromList(uniqueId);
        }
        return new SkywarsLobbyPlayer(uniqueId);
    }
}
