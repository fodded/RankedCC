package me.fodded.rankedskywars.gameplay.game.chest.refills;

import me.fodded.rankedskywars.gameplay.game.chest.action.ChestAction;
import org.bukkit.inventory.Inventory;

public interface ChestRefill extends ChestAction {

    void refillChest(Inventory inventory, int chestIndex, int chestsAmount, int refillIndex);

}
