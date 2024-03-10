package me.fodded.rankedskywars.gameplay.game.chest.refills;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static me.fodded.rankedskywars.gameplay.game.chest.utils.ChestUtils.*;

public class IslandChestRefill implements ChestRefill {

    private final List<ItemStack> itemStackList = new LinkedList<>();

    @Override
    public void refillChest(Inventory inventory, int chestIndex, int chestsAmount, int refillIndex) {
        Collections.shuffle(randomizedSlots);

        List<ItemStack> chestRefillItems = itemStackList;
        int size = chestRefillItems.size() / chestsAmount;

        int start = size * chestIndex;
        int end = Math.min(size * (chestIndex + 1), chestRefillItems.size());

        chestRefillItems = chestRefillItems.subList(start, end);

        int index = 0;
        for (ItemStack itemStack : chestRefillItems) {
            int slotIndex = randomizedSlots.get(index++);
            if (refillIndex == 2) {
                ItemStack enderPearlItemStack = new ItemStack(Material.ENDER_PEARL);
                inventory.setItem(Math.min(slotIndex, 26), enderPearlItemStack);
            } else if (inventory.getItem(slotIndex) == null) {
                inventory.setItem(slotIndex, itemStack);
            }
        }
    }

    @Override
    public void setupChestItems() {
        addItemToListRandomly(new ItemStack(Material.EXP_BOTTLE, getRandomFromArray(new int[]{8, 10, 12})), 100, itemStackList);
        addItemToListRandomly(new ItemStack(Material.LAVA_BUCKET), 70, itemStackList);
        addItemToListRandomly(new ItemStack(Material.DIAMOND_SWORD), 50, itemStackList);
        addItemToListRandomly(new ItemStack(Material.FLINT_AND_STEEL), 50, itemStackList);
        addItemToListRandomly(new ItemStack(Material.FISHING_ROD), 30, itemStackList);
        addItemToListRandomly(new ItemStack(Material.GOLDEN_APPLE), 60, itemStackList);
        addItemToListRandomly(new ItemStack(Material.WOOD, 64), 100, itemStackList);
        addItemToListRandomly(new ItemStack(Material.WOOD, 32), 50, itemStackList);
        addItemToListRandomly(new ItemStack(Material.SNOW_BALL, 16), 60, itemStackList);
        addItemToListRandomly(new ItemStack(Material.SNOW_BALL, 16), 40, itemStackList);
        addItemToListRandomly(new ItemStack(Material.EGG, 8), 30, itemStackList);
        addItemToListRandomly(regenerationPotionOne, 30, itemStackList);
    }
}