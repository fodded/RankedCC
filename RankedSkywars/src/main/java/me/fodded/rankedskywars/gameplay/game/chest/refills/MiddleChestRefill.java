package me.fodded.rankedskywars.gameplay.game.chest.refills;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static me.fodded.rankedskywars.gameplay.game.chest.utils.ChestUtils.*;

public class MiddleChestRefill implements ChestRefill {

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
            if (index == 1) {
                ItemStack enderPearlItemStack = new ItemStack(Material.ENDER_PEARL, ThreadLocalRandom.current().nextInt(2, 8));
                inventory.setItem(Math.min(slotIndex, 26), enderPearlItemStack);
            } else if (inventory.getItem(slotIndex) == null) {
                inventory.setItem(slotIndex, itemStack);
            }
        }
    }

    @Override
    public void setupChestItems() {
        ItemStack refillSword = new ItemStack(Material.DIAMOND_SWORD, 1);
        refillSword.addEnchantment(Enchantment.FIRE_ASPECT, 2);
        refillSword.addEnchantment(Enchantment.DAMAGE_ALL, 1);

        ItemStack refillBow = new ItemStack(Material.BOW, 1);
        refillBow.addEnchantment(Enchantment.ARROW_DAMAGE, 2);

        addItemToListRandomly(refillSword, 100, itemStackList);
        addItemToListRandomly(new ItemStack(Material.ARROW, getRandomFromArray(new int[]{12, 24})), 100, itemStackList);
        addItemToListRandomly(new ItemStack(Material.GOLDEN_APPLE, getRandomFromArray(new int[]{1, 2, 3})), 50, itemStackList);
        addItemToListRandomly(new ItemStack(Material.GOLDEN_APPLE, getRandomFromArray(new int[]{1, 2})), 100, itemStackList);
        addItemToListRandomly(new ItemStack(Material.GOLDEN_APPLE, getRandomFromArray(new int[]{1, 2})), 100, itemStackList);
        addItemToListRandomly(new ItemStack(Material.GOLDEN_APPLE, getRandomFromArray(new int[]{1, 2, 3})), 50, itemStackList);
        addItemToListRandomly(new ItemStack(Material.LOG, 32), 100, itemStackList);
        addItemToListRandomly(new ItemStack(Material.LOG, 32), 100, itemStackList);
        addItemToListRandomly(new ItemStack(Material.LOG, 32), 50, itemStackList);
        addItemToListRandomly(refillBow, 100, itemStackList);
        addItemToListRandomly(new ItemStack(Material.EXP_BOTTLE, getRandomFromArray(new int[]{16, 24})), 100, itemStackList);
        addItemToListRandomly(new ItemStack(Material.EXP_BOTTLE, getRandomFromArray(new int[]{16})), 100, itemStackList);
        addItemToListRandomly(new ItemStack(Material.TNT, getRandomFromArray(new int[]{3, 5, 7})), 50, itemStackList);
        addItemToListRandomly(regenerationPotionTwo, 100, itemStackList);
        addItemToListRandomly(regenerationPotionTwo, 100, itemStackList);
        addItemToListRandomly(regenerationPotionOne, 100, itemStackList);
        addItemToListRandomly(regenerationPotionOne, 50, itemStackList);
        addItemToListRandomly(healPotionTwo, 100, itemStackList);
        addItemToListRandomly(healPotionTwo, 70, itemStackList);
        addItemToListRandomly(healPotionTwo, 100, itemStackList);
        addItemToListRandomly(healPotionOne, 100, itemStackList);
        addItemToListRandomly(healPotionOne, 100, itemStackList);
        addItemToListRandomly(healPotionOne, 50, itemStackList);
        addItemToListRandomly(fireResistancePotion, 100, itemStackList);
        addItemToListRandomly(speedPotion, 100, itemStackList);
        addItemToListRandomly(speedPotion, 70, itemStackList);
        addItemToListRandomly(speedPotion, 50, itemStackList);
    }

    public void addRandomArmor(int enchantmentLevel) {
        int armorPiecesAmount = 3;
        for(int i = 0; i < armorPiecesAmount; i++) {
            int randomArmor = getRandomFromArray(new int[]{1,2,3,4});
            switch (randomArmor) {
                case 1:
                    ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS, 1);
                    boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, enchantmentLevel);
                    itemStackList.add(boots);
                    break;
                case 2:
                    ItemStack leggings = new ItemStack(Material.DIAMOND_LEGGINGS, 1);
                    leggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, enchantmentLevel);
                    itemStackList.add(leggings);
                    break;
                case 3:
                    ItemStack chestplate = new ItemStack(Material.DIAMOND_CHESTPLATE, 1);
                    chestplate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, enchantmentLevel);
                    itemStackList.add(chestplate);
                    break;
                case 4:
                    ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET, 1);
                    helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, enchantmentLevel);
                    itemStackList.add(helmet);
                    break;
            }
        }
    }
}
