package me.fodded.rankedskywars.gameplay.game.chest.fills;

import me.fodded.rankedskywars.gameplay.game.chest.utils.ChestUtils;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static me.fodded.rankedskywars.gameplay.game.chest.utils.ChestUtils.*;

public class IslandChestFill implements ChestFill {

    private ItemStack armorPiece;
    private final List<ItemStack> itemStackList = new LinkedList<>();

    @Override
    public void fillChest(Inventory inventory, int chestIndex, int chestsAmount) {
        inventory.clear();
        Collections.shuffle(ChestUtils.randomizedSlots);

        List<ItemStack> chestRefillItems = itemStackList;
        int size = chestRefillItems.size() / chestsAmount;

        int start = size * chestIndex;
        int end = Math.min(size * (chestIndex + 1), chestRefillItems.size());

        chestRefillItems = chestRefillItems.subList(start, end);

        int index = 0;
        for (ItemStack itemStack : chestRefillItems) {
            int slotIndex = ChestUtils.randomizedSlots.get(index++);
            if (inventory.getItem(slotIndex) == null) {
                inventory.setItem(slotIndex, itemStack);
            }
        }
    }

    @Override
    public void setupChestItems() {
        addItemToListRandomly(new ItemStack(Material.EXP_BOTTLE, getRandomFromArray(new int[]{8, 10, 12})), 100, itemStackList);
        addItemToListRandomly(new ItemStack(Material.WATER_BUCKET), 100, itemStackList);
        addItemToListRandomly(new ItemStack(Material.LAVA_BUCKET), 100, itemStackList);
        addItemToListRandomly(new ItemStack(Material.DIAMOND_SWORD), 100, itemStackList);
        addItemToListRandomly(new ItemStack(Material.FLINT_AND_STEEL), 75, itemStackList);
        addItemToListRandomly(new ItemStack(Material.FISHING_ROD), 70, itemStackList);
        addItemToListRandomly(new ItemStack(Material.GOLDEN_APPLE), 60, itemStackList);
        addItemToListRandomly(new ItemStack(Material.WOOD, 64), 100, itemStackList);
        addItemToListRandomly(new ItemStack(Material.WOOD, 32), 50, itemStackList);
        addItemToListRandomly(new ItemStack(Material.WOOD, 32), 50, itemStackList);
        addItemToListRandomly(new ItemStack(Material.SNOW_BALL, 8), 100, itemStackList);
        addItemToListRandomly(new ItemStack(Material.SNOW_BALL, 16), 60, itemStackList);
        addItemToListRandomly(new ItemStack(Material.SNOW_BALL, 16), 60, itemStackList);
        addItemToListRandomly(new ItemStack(Material.EGG, 8), 65, itemStackList);
        addItemToListRandomly(new ItemStack(armorPiece), 100, itemStackList);
        addItemToListRandomly(getCustomPotion(PotionEffectType.REGENERATION, 1, 33, 0, (short) 16385), 10, itemStackList);
    }

    public void setupArmor() {
        int randomArmor = getRandomFromArray(new int[]{1,2,3,4});
        switch (randomArmor) {
            case 1:
                armorPiece = new ItemStack(Material.DIAMOND_BOOTS);
                break;
            case 2:
                armorPiece = new ItemStack(Material.DIAMOND_LEGGINGS);
                break;
            case 3:
                armorPiece = new ItemStack(Material.DIAMOND_CHESTPLATE);
                break;
            case 4:
                armorPiece = new ItemStack(Material.DIAMOND_HELMET);
                break;
        }
    }
}
