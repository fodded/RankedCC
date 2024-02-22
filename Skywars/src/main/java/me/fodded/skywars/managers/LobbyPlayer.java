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
import me.fodded.spigotcore.configs.ConfigLoader;
import me.fodded.spigotcore.servers.SpigotServerManager;
import me.fodded.spigotcore.utils.ItemUtils;
import me.fodded.spigotcore.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class LobbyPlayer extends AbstractNetworkPlayer {

    private static Map<UUID, LobbyPlayer> lobbyPlayerMap = new HashMap<>();

    private long lastTimeUsed = 0;
    private final UUID uniqueId;

    public LobbyPlayer(UUID uniqueId) {
        super(uniqueId);

        lobbyPlayerMap.put(uniqueId, this);
    }

    public void handleJoin() {
        Player player = Bukkit.getPlayer(uniqueId);
        player.teleport(ServerLocations.getInstance().getLobbyLocation());
        player.setAllowFlight(true); // needed for double jump

        UpdateScoreboardTask updateScoreboardTask = new UpdateScoreboardTask(player);
        updateScoreboardTask.runTaskTimer(SpigotCore.getInstance().getPlugin(), 10L, 20L);

        // telling redis what is actual amount of players on our server atm
        SpigotServerManager.getInstance().updatePlayerCount(Bukkit.getOnlinePlayers().size()+1);

        // We need to give player items with a small delay, so we are sure we have enough time to load data
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            if(player.isOnline()) {
                initializePlayer();
            }
        }, 10L);
    }

    public void handleQuit() {
        SkywarsLobbyScoreboard skywarsLobbyScoreboard = (SkywarsLobbyScoreboard) SkywarsLobbyScoreboard.getScoreboardManager(uniqueId);
        if(skywarsLobbyScoreboard != null) {
            skywarsLobbyScoreboard.removeScoreboard();
        }

        // telling redis what is actual amount of players on our server atm
        SpigotServerManager.getInstance().updatePlayerCount(Bukkit.getOnlinePlayers().size()-1);
    }

    public void initializePlayer() {
        setItemsToPlayerInventory();
    }

    public void openGui(Material holdingMaterial) {
        Player player = Bukkit.getPlayer(uniqueId);
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
                generalStatsDataManager.applyChangeToRedis(uniqueId, generalStats -> generalStats.setPlayersVisibility(false));
                hidePlayers();
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), this::setItemsToPlayerInventory, 2);
                break;
            case CARROT_ITEM:
                if(isFlooding()) {
                    return;
                }
                generalStatsDataManager.applyChangeToRedis(uniqueId, generalStats -> generalStats.setPlayersVisibility(true));
                showPlayers();
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), this::setItemsToPlayerInventory, 2);
                break;
        }
    }

    private boolean isFlooding() {
        GeneralStats generalStats = GeneralStatsDataManager.getInstance().getCachedValue(uniqueId);
        Player player = Bukkit.getPlayer(uniqueId);

        String floodingMessage = ConfigLoader.getInstance().getConfig(generalStats + "-lang.yml").getString("chat-delay");
        if(lastTimeUsed > System.currentTimeMillis()) {
            player.sendMessage(StringUtils.format(floodingMessage));
            return true;
        }
        lastTimeUsed = System.currentTimeMillis() + 3000;
        return false;
    }

    private void hidePlayers() {
        Player player = Bukkit.getPlayer(uniqueId);
        for(Player eachPlayer : Bukkit.getOnlinePlayers()) {
            player.hidePlayer(eachPlayer);
        }
    }

    private void showPlayers() {
        Player player = Bukkit.getPlayer(uniqueId);
        for(Player eachPlayer : Bukkit.getOnlinePlayers()) {
            GeneralStats eachPlayerGeneralStats = GeneralStatsDataManager.getInstance().getCachedValue(eachPlayer.getUniqueId());

            // we do not show hidden staff to a player unless it's another staff member
            if(!eachPlayerGeneralStats.isVanished() || player.isOp()) {
                player.showPlayer(eachPlayer);
            }
        }
    }

    private void setItemsToPlayerInventory() {
        Player player = Bukkit.getPlayer(uniqueId);
        if(player == null) {
            return;
        }

        GeneralStats generalStats = GeneralStatsDataManager.getInstance().getCachedValue(uniqueId);
        player.getInventory().setItem(8, ItemUtils.getItemStack(
                Material.FIREWORK_CHARGE,
                "&fLobby Selector &6(Right Click)",
                Arrays.asList("&fRight Click to open Lobby Selector menu!")
        ));

        player.getInventory().setItem(7, ItemUtils.getItemStack(
                generalStats.isPlayersVisibility() ? Material.GOLDEN_CARROT : Material.CARROT_ITEM,
                "&fPlayers: " + (generalStats.isPlayersVisibility() ? "Visible" : "Hidden") + " &6(Right Click)",
                Arrays.asList("&fRight-click to toggle player visibility!")
        ));

        player.getInventory().setItem(6, ItemUtils.getItemStack(
                Material.GOLD_NUGGET,
                "&fSettings &6(Right Click)",
                Arrays.asList("&fRight-click to edit your settings!")
        ));

        player.getInventory().setItem(1, ItemUtils.getItemStack(
                Material.BLAZE_POWDER,
                "&fCosmetics Menu &6(Right Click)",
                Arrays.asList("&fRight Click to open Cosmetics Menu!")
        ));

        player.getInventory().setItem(0, ItemUtils.getItemStack(
                Material.MAGMA_CREAM,
                "&6&lServer Menu",
                Arrays.asList("&fRight Click to open Server Menu!")
        ));
    }

    public static LobbyPlayer getLobbyPlayer(UUID uniqueId) {
        if(lobbyPlayerMap.containsKey(uniqueId)) {
            return lobbyPlayerMap.get(uniqueId);
        }
        return new LobbyPlayer(uniqueId);
    }
}
