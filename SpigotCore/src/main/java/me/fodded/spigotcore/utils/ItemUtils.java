package me.fodded.spigotcore.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.LinkedList;
import java.util.List;

public class ItemUtils {

    public static ItemStack getItemStack(ItemStack itemStack, String name, List<String> lore, boolean enchanted) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(StringUtils.format(name));
        meta.setLore(getFormattedList(lore));

        if(enchanted) {
            meta.addEnchant(Enchantment.KNOCKBACK, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack getItemStack(ItemStack itemStack, String name, List<String> lore) {
        ItemMeta meta = itemStack.getItemMeta();

        meta.setDisplayName(StringUtils.format(name));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.setLore(getFormattedList(lore));

        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack getItemStack(Material material, String name, List<String> lore, boolean enchanted, int size) {
        ItemStack stack = new ItemStack(material, size);
        ItemMeta meta = stack.getItemMeta();

        meta.setDisplayName(StringUtils.format(name));
        meta.setLore(getFormattedList(lore));

        if(enchanted) {
            meta.addEnchant(Enchantment.KNOCKBACK, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        }

        stack.setItemMeta(meta);
        return stack;
    }

    public static ItemStack getItemStack(Material material, String name, List<String> lore, boolean enchanted) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();

        meta.setDisplayName(StringUtils.format(name));
        meta.setLore(getFormattedList(lore));

        if(enchanted) {
            meta.addEnchant(Enchantment.KNOCKBACK, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        }

        stack.setItemMeta(meta);
        return stack;
    }

    public static ItemStack getItemStack(Material material, String name, List<String> lore, int size) {
        ItemStack stack = new ItemStack(material, size);
        ItemMeta meta = stack.getItemMeta();

        meta.setDisplayName(StringUtils.format(name));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.setLore(getFormattedList(lore));

        stack.setItemMeta(meta);
        return stack;
    }

    public static ItemStack getItemStack(Material material, String name, List<String> lore, int size, short durability) {
        ItemStack stack = new ItemStack(material, size, durability);
        ItemMeta meta = stack.getItemMeta();

        meta.setDisplayName(StringUtils.format(name));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.setLore(getFormattedList(lore));

        stack.setItemMeta(meta);
        return stack;
    }

    public static ItemStack getItemStack(Material material, String name, List<String> lore, short durability) {
        ItemStack stack = new ItemStack(material, 1, durability);
        ItemMeta meta = stack.getItemMeta();

        meta.setDisplayName(StringUtils.format(name));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.setLore(getFormattedList(lore));

        stack.setItemMeta(meta);
        return stack;
    }

    public static ItemStack getItemStack(Material material, String name, List<String> lore) {
        ItemStack stack = new ItemStack(material, 1);
        ItemMeta meta = stack.getItemMeta();

        meta.setDisplayName(StringUtils.format(name));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.setLore(getFormattedList(lore));

        stack.setItemMeta(meta);
        return stack;
    }

    private static List<String> getFormattedList(List<String> initialList) {
        List<String> resultList = new LinkedList<>();
        for(String text : initialList) {
            resultList.add(StringUtils.format(text));
        }
        return resultList;
    }
}
