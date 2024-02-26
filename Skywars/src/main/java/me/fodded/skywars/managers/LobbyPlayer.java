package me.fodded.skywars.managers;

import lombok.Getter;
import me.fodded.core.managers.stats.impl.profile.GeneralStats;
import me.fodded.core.managers.stats.impl.profile.GeneralStatsDataManager;
import me.fodded.core.user.AbstractNetworkPlayer;
import me.fodded.skywars.Main;
import me.fodded.skywars.gameplay.guis.CosmeticsGui;
import me.fodded.skywars.gameplay.guis.LobbySelectorGui;
import me.fodded.skywars.gameplay.guis.MinigamesGui;
import me.fodded.skywars.gameplay.guis.settings.SettingsGui;
import me.fodded.skywars.gameplay.scoreboard.SkywarsLobbyScoreboard;
import me.fodded.skywars.tasks.UpdateScoreboardTask;
import me.fodded.spigotcore.SpigotCore;
import me.fodded.spigotcore.languages.LanguageManager;
import me.fodded.spigotcore.utils.ItemUtils;
import me.fodded.spigotcore.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public class LobbyPlayer extends AbstractNetworkPlayer {

    private static Map<UUID, LobbyPlayer> lobbyPlayerMap = new HashMap<>();

    private long lastTimeUsed = 0;

    public LobbyPlayer(UUID uniqueId) {
        super(uniqueId);

        lobbyPlayerMap.put(uniqueId, this);
    }

    public void handleJoin() {
        Player player = Bukkit.getPlayer(getUniqueId());
        player.teleport(ServerLocations.getInstance().getLobbyLocation());
        player.setAllowFlight(true); // needed for double jump
        player.setGameMode(GameMode.ADVENTURE);

        UpdateScoreboardTask updateScoreboardTask = new UpdateScoreboardTask(player);
        updateScoreboardTask.runTaskTimer(SpigotCore.getInstance().getPlugin(), 10L, 20L);

        // We need to give player items with a small delay, so we are sure we have enough time to load data
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            if(player.isOnline()) {
                initializePlayer();

                for(Player eachPlayer : Bukkit.getOnlinePlayers()) {
                    LobbyPlayer lobbyPlayer = getLobbyPlayer(eachPlayer.getUniqueId());
                    lobbyPlayer.updateVisibility();
                }
            }
        }, 10L);
    }

    public void handleQuit() {
        SkywarsLobbyScoreboard skywarsLobbyScoreboard = (SkywarsLobbyScoreboard) SkywarsLobbyScoreboard.getScoreboardManager(getUniqueId());
        if(skywarsLobbyScoreboard != null) {
            skywarsLobbyScoreboard.removeScoreboard();
        }
    }

    public void initializePlayer() {
        setItemsToPlayerInventory();
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
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), this::setItemsToPlayerInventory, 2);
                break;
            case CARROT_ITEM:
                if(isFlooding()) {
                    return;
                }
                generalStatsDataManager.applyChangeToRedis(getUniqueId(), generalStats -> generalStats.setPlayersVisibility(true));
                showPlayers();
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), this::setItemsToPlayerInventory, 2);
                break;
        }
    }

    private boolean isFlooding() {
        Player player = Bukkit.getPlayer(getUniqueId());
        String floodingMessage = LanguageManager.getInstance().getLanguageConfig(player.getUniqueId()).getString("chat-delay");

        if(lastTimeUsed > System.currentTimeMillis()) {
            player.sendMessage(StringUtils.format(floodingMessage));
            return true;
        }
        lastTimeUsed = System.currentTimeMillis() + 3000;
        return false;
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
            }
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

    public static LobbyPlayer getLobbyPlayer(UUID uniqueId) {
        if(lobbyPlayerMap.containsKey(uniqueId)) {
            return lobbyPlayerMap.get(uniqueId);
        }
        return new LobbyPlayer(uniqueId);
    }
}
