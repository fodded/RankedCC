package me.fodded.rankedskywars.gameplay.game.tracker;

import me.fodded.rankedskywars.gameplay.game.RankedSkywarsGame;
import me.fodded.rankedskywars.managers.RankedSkywarsPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GameDamageTracker {
    private Map<UUID, List<DamageDealt>> playersDealtDamageMap = new HashMap<>();

    private final RankedSkywarsGame gameInstance;
    public GameDamageTracker(RankedSkywarsGame gameInstance) {
        this.gameInstance = gameInstance;
    }

    private void event(EntityDamageByEntityEvent event) {
        Player damager = (Player) event.getDamager();
        RankedSkywarsPlayer rankedSkywarsPlayer = RankedSkywarsPlayer.getRankedSkywarsPlayer(damager.getUniqueId());
        RankedSkywarsGame game = rankedSkywarsPlayer.getCurrentGame();

        if(game.isPlayerAlive(rankedSkywarsPlayer.getUniqueId())) {
            GamePlayerStatisticsTracker gamePlayerStatisticsTracker = game.getGamePlayerStatisticsTrackerMap().get(damager.getUniqueId());
            gamePlayerStatisticsTracker.getDamagedPlayersMap().put(event.getEntity().getUniqueId(), event.getDamage());
        }
    }

    // TODO: finish
    private Player getAssistant(Player target, String some) {
        double fullDealtDamage = 0.0;

        for(Map.Entry<UUID, List<DamageDealt>> entry : playersDealtDamageMap.entrySet()) {
            List<DamageDealt> listOfDealtDamage = entry.getValue();
            for(DamageDealt damageDealtLooped : listOfDealtDamage) {
                if(damageDealtLooped.getPlayerUniqueId().equals(target.getUniqueId())) {
                    fullDealtDamage += damageDealtLooped.getDealtDamage();
                }
            }
        }

        for(Map.Entry<UUID, List<DamageDealt>> entry : playersDealtDamageMap.entrySet()) {
            List<DamageDealt> listOfDealtDamage = entry.getValue();
            for(DamageDealt damageDealtLooped : listOfDealtDamage) {
                if(damageDealtLooped.getPlayerUniqueId().equals(target.getUniqueId())) {
                    Player hitter = Bukkit.getPlayer(entry.getKey());
                    if(hitter == null) return null;
                    if (hitter.isOnline()) {
                        if (damageDealtLooped.getDealtDamage() >= fullDealtDamage / 2) {
                            return hitter;
                        }
                    }
                }
            }
        }
        return null;
    }

    private Player getAssistant(Player target) {
        double fullDealtDamage = 0.0;

        for (Map.Entry<UUID, List<DamageDealt>> entry : playersDealtDamageMap.entrySet()) {
            for (DamageDealt damageDealtLooped : entry.getValue()) {
                if (damageDealtLooped.getPlayerUniqueId().equals(target.getUniqueId())) {
                    fullDealtDamage += damageDealtLooped.getDealtDamage();
                    Player hitter = Bukkit.getPlayer(entry.getKey());
                    if (hitter != null && hitter.isOnline() && damageDealtLooped.getDealtDamage() >= fullDealtDamage / 2) {
                        return hitter;
                    }
                }
            }
        }

        return null;
    }
}
