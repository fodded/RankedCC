package me.fodded.rankedskywars.gameplay.game.chest.fills;

import me.fodded.rankedskywars.gameplay.game.chest.action.ChestAction;
import org.bukkit.inventory.Inventory;

public interface ChestFill extends ChestAction {

    void fillChest(Inventory inventory, int chestIndex, int chestsAmount);
}
