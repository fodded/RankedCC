package me.fodded.rankedskywars.gameplay.game.tracker;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
public class DamageDealt {

    private final UUID playerUniqueId;
    private Double dealtDamage;

    private DamageDealt(Player playerUniqueId, Double dealtDamage) {
        this.playerUniqueId = playerUniqueId.getUniqueId();
        this.dealtDamage = dealtDamage;
    }

    public DamageDealt(Player playerUniqueId) {
        this.playerUniqueId = playerUniqueId.getUniqueId();
    }

    public void addDamage(Double damage) {
        this.dealtDamage += damage;
    }
}

