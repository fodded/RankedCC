package me.fodded.rankedskywars.gameplay.game.chest.fills;

import me.fodded.rankedskywars.gameplay.game.chest.utils.ChestUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static me.fodded.rankedskywars.gameplay.game.chest.utils.ChestUtils.*;

public class MiddleChestFill implements ChestFill {

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
        addItemToListRandomly(fireResistancePotion, 100, itemStackList);
        addItemToListRandomly(fireResistancePotion, 50, itemStackList);
        addItemToListRandomly(fireResistancePotion, 50, itemStackList);

        addItemToListRandomly(regenerationPotionTwo, 100, itemStackList);
        addItemToListRandomly(regenerationPotionTwo, 50, itemStackList);

        addItemToListRandomly(regenerationPotionOne, 100, itemStackList);
        addItemToListRandomly(regenerationPotionOne, 100, itemStackList);
        addItemToListRandomly(regenerationPotionOne, 70, itemStackList);
        addItemToListRandomly(regenerationPotionOne, 40, itemStackList);

        addItemToListRandomly(speedPotion, 100, itemStackList);
        addItemToListRandomly(speedPotion, 50, itemStackList);

        addItemToListRandomly(new ItemStack(Material.GOLDEN_APPLE, getRandomFromArray(new int[]{1, 2})), 100, itemStackList);
        addItemToListRandomly(new ItemStack(Material.GOLDEN_APPLE, getRandomFromArray(new int[]{1, 2})), 100, itemStackList);
        addItemToListRandomly(new ItemStack(Material.GOLDEN_APPLE, getRandomFromArray(new int[]{1, 2})), 50, itemStackList);
        addItemToListRandomly(new ItemStack(Material.GOLDEN_APPLE, getRandomFromArray(new int[]{1, 2, 3})), 30, itemStackList);
        addItemToListRandomly(new ItemStack(Material.ARROW, getRandomFromArray(new int[]{12, 16})), 100, itemStackList);
        addItemToListRandomly(new ItemStack(Material.ARROW, getRandomFromArray(new int[]{12, 16})), 70, itemStackList);
        addItemToListRandomly(new ItemStack(Material.LAVA_BUCKET), 100, itemStackList);
        addItemToListRandomly(new ItemStack(Material.LAVA_BUCKET), 70, itemStackList);
        addItemToListRandomly(new ItemStack(Material.WATER_BUCKET), 100, itemStackList);
        addItemToListRandomly(new ItemStack(Material.WATER_BUCKET), 70, itemStackList);
        addItemToListRandomly(new ItemStack(Material.LOG, 32), 100, itemStackList);
        addItemToListRandomly(new ItemStack(Material.LOG, 32), 100, itemStackList);
        addItemToListRandomly(new ItemStack(Material.LOG, 32), 100, itemStackList);
        addItemToListRandomly(new ItemStack(Material.LOG, 32), 100, itemStackList);
        addItemToListRandomly(new ItemStack(Material.LOG, 32), 30, itemStackList);
        addItemToListRandomly(new ItemStack(Material.TNT, getRandomFromArray(new int[]{3, 5})), 100, itemStackList);
        addItemToListRandomly(new ItemStack(Material.TNT, getRandomFromArray(new int[]{5, 7})), 50, itemStackList);
        addItemToListRandomly(new ItemStack(Material.FLINT_AND_STEEL, 1), 80, itemStackList);
        addItemToListRandomly(new ItemStack(Material.SNOW_BALL, 8), 100, itemStackList);
        addItemToListRandomly(new ItemStack(Material.EGG, 8), 100, itemStackList);
        addItemToListRandomly(new ItemStack(Material.EGG, 12), 60, itemStackList);
        addItemToListRandomly(new ItemStack(Material.FISHING_ROD), 60, itemStackList);
        addItemToListRandomly(new ItemStack(Material.FISHING_ROD), 50, itemStackList);
        addItemToListRandomly(new ItemStack(Material.EXP_BOTTLE, getRandomFromArray(new int[]{12, 16, 24})), 100, itemStackList);

        ItemStack bow = new ItemStack(Material.BOW, 1);
        bow.addEnchantment(Enchantment.ARROW_DAMAGE, 1);

        ItemStack pickaxe = new ItemStack(Material.DIAMOND_PICKAXE, 1);
        pickaxe.addEnchantment(Enchantment.DIG_SPEED, 2);

        ItemStack axe = new ItemStack(Material.DIAMOND_AXE, 1);
        axe.addEnchantment(Enchantment.DIG_SPEED, 2);

        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD, 1);
        sword.addEnchantment(Enchantment.FIRE_ASPECT, 1);

        addItemToListRandomly(sword, 100, itemStackList);
        addItemToListRandomly(pickaxe, 100, itemStackList);
        addItemToListRandomly(axe, 100, itemStackList);
        addItemToListRandomly(bow, 60, itemStackList);
    }
}
