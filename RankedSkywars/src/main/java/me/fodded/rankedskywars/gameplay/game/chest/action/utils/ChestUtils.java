package me.fodded.rankedskywars.gameplay.game.chest.action.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class ChestUtils {
    public static List<Integer> randomizedSlots = new ArrayList<>(
            Arrays.asList(
                    0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26
            )
    );

    public static void addItemToListRandomly(ItemStack itemStack, int chance, List<ItemStack> list) {
        if(chance >= ThreadLocalRandom.current().nextInt(0, 100)) {
            list.add(itemStack);
        }
    }

    public static void addRandomArmor(int level, List<ItemStack> list) {
        int armorPiecesAmount = 3;
        for(int i = 0; i < armorPiecesAmount; i++) {
            int randomArmor = getRandomFromArray(new int[]{1,2,3,4});
            switch (randomArmor) {
                case 1:
                    list.add(getArmor(Material.DIAMOND_BOOTS, level));
                    break;
                case 2:
                    list.add(getArmor(Material.DIAMOND_LEGGINGS, level));
                    break;
                case 3:
                    list.add(getArmor(Material.DIAMOND_CHESTPLATE, level));
                    break;
                case 4:
                    list.add(getArmor(Material.DIAMOND_HELMET, level));
                    break;
            }
        }
    }

    public static ItemStack getArmor(Material material, int enchantmentLevel) {
        ItemStack armor = new ItemStack(material, 1);
        armor.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, enchantmentLevel);
        return armor;
    }

    public static ItemStack getCustomPotion(PotionEffectType potionEffectType, Integer amount, Integer duration, Integer level, short id) {
        ItemStack itemStack = new ItemStack(Material.POTION, amount);
        itemStack = addCustomEffect(
                itemStack, potionEffectType, duration, level
        );

        itemStack.setDurability(id);

        return itemStack;
    }

    public static int getRandomFromArray(int[] array) {
        int rnd = new Random().nextInt(array.length);
        return array[rnd];
    }

    public static ItemStack addCustomEffect(ItemStack item, PotionEffectType potionEffectType, PotionEffectType potionEffectType2, int i, int j) {
        PotionMeta meta = (PotionMeta)item.getItemMeta();
        PotionEffect effect = new PotionEffect(potionEffectType, i * 20, j);
        PotionEffect effect2 = new PotionEffect(potionEffectType2, i * 20, j);
        meta.addCustomEffect(effect, true);
        meta.addCustomEffect(effect2, true);
        item.setItemMeta((ItemMeta)meta);
        return item;
    }

    public static ItemStack addCustomEffect(ItemStack item, PotionEffectType potionEffectType, int i, int j) {
        PotionMeta meta = (PotionMeta)item.getItemMeta();
        PotionEffect effect = new PotionEffect(potionEffectType, i * 20, j);
        meta.addCustomEffect(effect, true);
        item.setItemMeta(meta);
        return item;
    }

    public static final ItemStack regenerationPotionTwo = getCustomPotion(PotionEffectType.REGENERATION, 1, 8, 1, (short)16417);
    public static final ItemStack regenerationPotionOne = getCustomPotion(PotionEffectType.REGENERATION, 1, 33, 0, (short)16385);
    public static final ItemStack speedPotion = getCustomPotion(PotionEffectType.SPEED, 1, 30, 1, (short)16418);
    public static final ItemStack fireResistancePotion = new ItemStack(Material.POTION, 1, (short) 8227);
    public static final ItemStack healPotionOne = new ItemStack(Material.POTION, 1, (short) 16453);
    public static final ItemStack healPotionTwo = new ItemStack(Material.POTION, 1, (short) 8229);
}
