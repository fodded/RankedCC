package me.fodded.rankedskywars.managers;

import me.fodded.core.Core;
import me.fodded.core.managers.stats.impl.profile.GeneralStats;
import me.fodded.core.managers.stats.impl.profile.GeneralStatsDataManager;
import me.fodded.rankedskywars.gameplay.game.RankedSkywarsGame;
import me.fodded.rankedskywars.gameplay.scoreboard.RankedSkywarsScoreboard;
import me.fodded.spigotcore.gameplay.disguise.DisguiseManager;
import me.fodded.spigotcore.gameplay.games.GameManager;
import me.fodded.spigotcore.gameplay.player.AbstractServerPlayer;
import me.fodded.spigotcore.languages.LanguageManager;
import me.fodded.spigotcore.utils.ItemUtils;
import me.fodded.spigotcore.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.redisson.api.RTopic;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class RankedSkywarsPlayer extends AbstractServerPlayer {

    public RankedSkywarsPlayer(UUID uniqueId) {
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
        updateVisibility();
    }

    public void handleQuit() {
        RankedSkywarsScoreboard rankedSkywarsScoreboard = (RankedSkywarsScoreboard) RankedSkywarsScoreboard.getScoreboardManager(getUniqueId());
        if(rankedSkywarsScoreboard != null) {
            rankedSkywarsScoreboard.removeScoreboard();
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
        player.setGameMode(GameMode.SURVIVAL);

        player.setMaxHealth(20.0);
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setExhaustion(0.0f);
        player.setExp(0.0f);
        player.setLevel(0);
        player.setAllowFlight(false);
        player.setFireTicks(0);
        player.closeInventory();
        player.spigot().setCollidesWithEntities(true);

        for (PotionEffect pe : player.getActivePotionEffects()) {
            player.removePotionEffect(pe.getType());
        }

        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[4]);
    }

    public void setItemsToPlayerInventory() {
        Player player = Bukkit.getPlayer(getUniqueId());
        if(player == null) {
            return;
        }

        Configuration languageConfig = LanguageManager.getInstance().getLanguageConfig(getUniqueId());
        for(String path : languageConfig.getConfigurationSection("player-items").getKeys(false)) {
            int slot = Integer.parseInt(path) - 1;
            String displayName = "";
            Material material = null;
            List<String> descriptionList = languageConfig.getStringList("player-items." + path + ".description");

            player.getInventory().setItem(
                    slot,
                    ItemUtils.getItemStack(material, displayName,descriptionList, false, 1)
            );
        }
    }

    public void updateVisibility() {
        Player player = Bukkit.getPlayer(getUniqueId());
        for(Player eachPlayer : Bukkit.getOnlinePlayers()) {
            if(eachPlayer.getWorld() != player.getWorld()) {
                eachPlayer.hidePlayer(player);
                player.hidePlayer(eachPlayer);
            } else {
                eachPlayer.showPlayer(player);
                player.showPlayer(eachPlayer);
            }
        }
    }

    public void openGui(Material holdingMaterial) {
        Player player = Bukkit.getPlayer(getUniqueId());
        GeneralStatsDataManager generalStatsDataManager = GeneralStatsDataManager.getInstance();
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

    public RankedSkywarsGame getCurrentGame() {
        return (RankedSkywarsGame) GameManager.getAllCurrentGames().stream()
                .filter(gameInstance -> gameInstance.getAlivePlayersList().contains(getUniqueId()) ||
                        gameInstance.getSpectatorPlayersList().contains(getUniqueId()))
                .findFirst()
                .orElse(null);
    }

    public static RankedSkywarsPlayer getRankedSkywarsPlayer(UUID uniqueId) {
        if(isInCache(uniqueId)) {
            return (RankedSkywarsPlayer) AbstractServerPlayer.getPlayerFromList(uniqueId);
        }
        return new RankedSkywarsPlayer(uniqueId);
    }
}
