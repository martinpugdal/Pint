package dk.martinersej.pint.utils;

import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ItemBuilder {

    private ItemStack itemStack;

    public ItemBuilder() {
        this(Material.STONE);
    }

    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemBuilder(Material material) {
        this(new ItemStack(material));
    }

    private String convertToColor(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public ItemBuilder setType(Material material) {
        itemStack.setType(material);
        return this;
    }

    public ItemBuilder setMaterial(Material material) {
        itemStack.setType(material);
        return this;
    }

    public ItemBuilder setItemMeta(ItemMeta itemMeta) {
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder setItemStack(ItemStack itemStack) {
        itemStack.setItemMeta(itemStack.getItemMeta());
        return this;
    }

    public ItemBuilder clone() {
        return new ItemBuilder(itemStack);
    }

    public ItemBuilder setDurability(short durability) {
        itemStack.setDurability(durability);
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder setDisplayName(String name) {
        name = convertToColor(name);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(name);
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setName(String name) {
        return setDisplayName(name);
    }

    public ItemBuilder addUnsafeEnchantment(Enchantment ench, int level) {
        itemStack.addUnsafeEnchantment(ench, level);
        return this;
    }

    public ItemBuilder removeEnchantment(Enchantment ench) {
        itemStack.removeEnchantment(ench);
        return this;
    }

    public ItemBuilder setGlowing(boolean glowing) {
        ItemMeta meta = itemStack.getItemMeta();
        if (glowing) {
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        } else {
            meta.removeEnchant(Enchantment.DURABILITY);
            meta.removeItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        }
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment ench, int level) {
        ItemMeta im = itemStack.getItemMeta();
        im.addEnchant(ench, level, true);
        itemStack.setItemMeta(im);
        return this;
    }

    public ItemBuilder addEnchantments(Map<Enchantment, Integer> enchantments) {
        itemStack.addEnchantments(enchantments);
        return this;
    }

    public ItemBuilder clearEnchantments() {
        ItemMeta meta = itemStack.getItemMeta();
        for (org.bukkit.enchantments.Enchantment enchantment : meta.getEnchants().keySet()) {
            meta.removeEnchant(enchantment);
        }
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        ItemMeta im = itemStack.getItemMeta();
        im.setLore(Arrays.asList(lore));
        itemStack.setItemMeta(im);
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        ItemMeta im = itemStack.getItemMeta();
        im.setLore(lore);
        itemStack.setItemMeta(im);
        return this;
    }

    public ItemBuilder removeLoreLine(String line) {
        ItemMeta im = itemStack.getItemMeta();
        List<String> lore = new ArrayList<>(im.getLore());
        if (!lore.contains(line)) return this;
        lore.remove(line);
        im.setLore(lore);
        itemStack.setItemMeta(im);
        return this;
    }

    public ItemBuilder removeLoreLine(int index) {
        ItemMeta im = itemStack.getItemMeta();
        List<String> lore = new ArrayList<>(im.getLore());
        if (index < 0 || index > lore.size()) return this;
        lore.remove(index);
        im.setLore(lore);
        itemStack.setItemMeta(im);
        return this;
    }

    public ItemBuilder addLoreLine(String line) {
        ItemMeta im = itemStack.getItemMeta();
        List<String> lore = new ArrayList<>();
        if (im.hasLore()) lore = new ArrayList<>(im.getLore());
        lore.add(line);
        im.setLore(lore);
        itemStack.setItemMeta(im);
        return this;
    }

    public ItemBuilder addLoreLine(String... lines) {
        ItemMeta im = itemStack.getItemMeta();
        List<String> lore = new ArrayList<>();
        if (im.hasLore()) lore = new ArrayList<>(im.getLore());
        lore.addAll(Arrays.asList(lines));
        im.setLore(lore);
        itemStack.setItemMeta(im);
        return this;
    }

    public ItemBuilder addLoreLine(String line, int pos) {
        ItemMeta im = itemStack.getItemMeta();
        List<String> lore = new ArrayList<>(im.getLore());
        lore.set(pos, line);
        im.setLore(lore);
        itemStack.setItemMeta(im);
        return this;
    }

    public ItemBuilder addItemFlag(org.bukkit.inventory.ItemFlag itemFlag) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.addItemFlags(itemFlag);
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder removeItemFlag(org.bukkit.inventory.ItemFlag itemFlag) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.removeItemFlags(itemFlag);
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder clearItemFlags() {
        ItemMeta meta = itemStack.getItemMeta();
        meta.removeItemFlags(org.bukkit.inventory.ItemFlag.values());
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setLeatherArmorColor(Color color) {
        try {
            LeatherArmorMeta im = (LeatherArmorMeta) itemStack.getItemMeta();
            im.setColor(color);
            itemStack.setItemMeta(im);
        } catch (ClassCastException ignored) {
        }
        return this;
    }

    public ItemBuilder setNbt(String key, String value) {
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();

        tag.setString(key, value);

        itemStack = CraftItemStack.asBukkitCopy(nmsItem);
        return this;
    }

    public static String getNbt(ItemStack item, String key) {
        if (item == null) return null;
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();
        if (!tag.hasKey(key)) return null;
        if (tag.getString(key).isEmpty()) return null;
        return tag.getString(key);
    }

    public SkullBuilder skull() {
        if (!SkullBuilder.isSkull(itemStack)) {
            this.setType(Material.SKULL_ITEM);
            this.setDurability((short) 3);
        }
        return new SkullBuilder(this.build());
    }

    public ItemStack build() {
        return itemStack;
    }
}