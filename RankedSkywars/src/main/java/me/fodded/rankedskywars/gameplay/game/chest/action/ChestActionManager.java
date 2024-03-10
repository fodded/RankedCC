package me.fodded.rankedskywars.gameplay.game.chest.action;

import me.fodded.rankedskywars.gameplay.game.RankedSkywarsGame;
import me.fodded.rankedskywars.gameplay.game.chest.GameChestType;
import me.fodded.rankedskywars.gameplay.game.chest.fills.ChestFill;
import me.fodded.rankedskywars.gameplay.game.chest.fills.IslandChestFill;
import me.fodded.rankedskywars.gameplay.game.chest.fills.MiddleChestFill;
import me.fodded.rankedskywars.gameplay.game.chest.refills.ChestRefill;
import me.fodded.rankedskywars.gameplay.game.chest.refills.IslandChestRefill;
import me.fodded.rankedskywars.gameplay.game.chest.refills.MiddleChestRefill;
import me.fodded.spigotcore.gameplay.games.GameInstance;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;

import java.util.Map;
import java.util.Set;

public class ChestActionManager {

    public void refillChests(GameInstance gameInstance, int refillIndex) {
        RankedSkywarsGame game = (RankedSkywarsGame) gameInstance;
        for (Map.Entry<GameChestType, Set<Location>> gameChestsMap : game.getGameChestMap().entrySet()) {
            GameChestType gameChestType = gameChestsMap.getKey();
            Set<Location> chestsLocationSet = gameChestsMap.getValue();

            ChestRefill chestRefill = gameChestType.equals(GameChestType.MIDDLE) ?
                    new MiddleChestRefill() :
                    new IslandChestRefill();

            int chestsIterated = 1;
            int chestsPlacedAmount = ChestActionManager.placedChestsAmount(chestsLocationSet);

            chestRefill.setupChestItems();
            if(chestRefill instanceof MiddleChestRefill) {
                ((MiddleChestRefill) chestRefill).addRandomArmor(refillIndex+1);
            }

            for(Location location : chestsLocationSet) {
                Chest chest = (Chest) location.getBlock().getState();
                Inventory chestInventory = chest.getInventory();

                chestRefill.refillChest(chestInventory, chestsIterated++, chestsPlacedAmount, refillIndex);
            }
        }
    }

    public void fillChests(GameInstance gameInstance) {
        RankedSkywarsGame game = (RankedSkywarsGame) gameInstance;
        for (Map.Entry<GameChestType, Set<Location>> gameChestsMap : game.getGameChestMap().entrySet()) {
            GameChestType gameChestType = gameChestsMap.getKey();
            Set<Location> chestsLocationSet = gameChestsMap.getValue();

            ChestFill chestFill = gameChestType.equals(GameChestType.MIDDLE) ?
                    new MiddleChestFill() :
                    new IslandChestFill();

            int chestsIterated = 1;
            int chestsPlacedAmount = ChestActionManager.placedChestsAmount(chestsLocationSet);

            chestFill.setupChestItems();
            if(chestFill instanceof IslandChestFill) {
                ((IslandChestFill) chestFill).setupArmor();
            }

            for(Location location : chestsLocationSet) {
                Chest chest = (Chest) location.getBlock().getState();
                Inventory chestInventory = chest.getInventory();

                chestFill.fillChest(chestInventory, chestsIterated++, chestsPlacedAmount);
            }
        }
    }

    public static int placedChestsAmount(Set<Location> locationSet) {
        return (int) locationSet.stream()
                .filter(location -> location.getBlock().getType() == Material.CHEST)
                .count();
    }
}
