package pl.v3bc.platform.utils;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import pl.v3bc.platform.utils.nbt.ItemNbt;
import pl.v3bc.platform.utils.text.TextBuilder;
import pl.v3bc.platform.utils.text.TextUtil;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class ItemBuilder {
    private ItemStack itemStack;

    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
    }

    public ItemBuilder(Material material, int amount) {
        this.itemStack = new ItemStack(material, amount);
    }

    public ItemBuilder(ItemStack itemStack, boolean clone) {
        this.itemStack = clone ? new ItemStack(itemStack) : itemStack;
    }

    public static ItemBuilder of(Material material) {
        return new ItemBuilder(material);
    }

    public static ItemBuilder of(Material material, int amount) {
        return new ItemBuilder(material, amount);
    }

    public static ItemBuilder of(ItemStack itemStack) {
        return new ItemBuilder(itemStack, true);
    }

    public static ItemBuilder manipulate(ItemStack itemStack) {
        return new ItemBuilder(itemStack, false);
    }

    public ItemBuilder setAmount(int amount) {
        this.itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder setType(Material material) {
        this.itemStack.setType(material);
        return this;
    }

    public ItemBuilder setType(ItemStack itemStack) {
        return this.setType(itemStack, true);
    }

    public ItemBuilder setType(ItemStack itemStack, boolean clone) {
        ItemStack copy = clone ? new ItemStack(itemStack) : itemStack;
        copy.setAmount(this.itemStack.getAmount());
        if (this.itemStack.hasItemMeta()) {
            copy.setItemMeta(this.itemStack.getItemMeta());
        }
        this.itemStack = copy;
        return this;
    }

    public ItemBuilder withDurability(int durability) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        if (itemMeta instanceof Damageable damageable) {
            damageable.setDamage(durability);
            this.itemStack.setItemMeta(itemMeta);
        }
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        if (itemMeta == null) {
            return this;
        }
        itemMeta.setUnbreakable(unbreakable);
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder setCmd(int cmd) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        if (itemMeta == null) {
            return this;
        }
        itemMeta.setCustomModelData(cmd);
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder setName(String name) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        if (itemMeta == null || name == null) {
            return this;
        }
        itemMeta.displayName(TextUtil.parse(name));
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder setName(String name, String placeholderKey, Object placeholderValue) {
        return setName(name, Map.of(placeholderKey, placeholderValue));
    }

    public ItemBuilder setName(String name, Map<String, ?> placeholders) {
        setName(name);
        return placeholders(placeholders);
    }

    public ItemBuilder setLore(List<String> lore) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        if (itemMeta == null || lore == null) {
            return this;
        }
        itemMeta.lore(TextUtil.parse(lore));
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder setLore(List<String> lore, Map<String, ?> placeholders) {
        setLore(lore);
        return placeholders(placeholders);
    }

    public ItemBuilder setLore(String... lore) {
        return this.setLore(Arrays.asList(lore));
    }

    public ItemBuilder startLoreWith(List<String> lore) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        if (itemMeta == null || lore == null) {
            return this;
        }
        List<Component> newLore = TextUtil.parse(lore);
        List<Component> current = itemMeta.lore();
        if (current != null && !current.isEmpty()) {
            itemMeta.lore(Stream.concat(newLore.stream(), current.stream()).toList());
        } else {
            itemMeta.lore(newLore);
        }
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder startLoreWith(String... lore) {
        return this.startLoreWith(Arrays.asList(lore));
    }

    public ItemBuilder appendLore(List<String> lore) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        if (itemMeta == null || lore == null) {
            return this;
        }
        List<Component> newLore = TextUtil.parse(lore);
        List<Component> current = itemMeta.lore();
        if (current != null && !current.isEmpty()) {
            itemMeta.lore(Stream.concat(current.stream(), newLore.stream()).toList());
        } else {
            itemMeta.lore(newLore);
        }
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder appendLore(String... lore) {
        return this.appendLore(Arrays.asList(lore));
    }

    public ItemBuilder placeholder(String key, Object value) {
        return this.placeholders(Collections.singletonMap(key, value));
    }

    public ItemBuilder placeholders(Map<String, ?> placeholders) {
        if (placeholders == null || placeholders.isEmpty()) {
            return this;
        }
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        if (itemMeta == null) {
            return this;
        }
        if (itemMeta.hasDisplayName()) {
            Component displayName = itemMeta.displayName();
            if (displayName != null) {
                String serializedName = TextUtil.serialize(displayName);
                TextBuilder builder = TextBuilder.builder().text(serializedName);
                placeholders.forEach(builder::placeholder);
                itemMeta.displayName(TextUtil.parse(builder.firstLine()));
            }
        }
        if (itemMeta.hasLore()) {
            List<Component> lore = itemMeta.lore();
            if (lore != null && !lore.isEmpty()) {
                List<String> serializedLore = lore.stream().map(TextUtil::serialize).toList();
                TextBuilder builder = TextBuilder.builder().text(serializedLore);
                placeholders.forEach(builder::placeholder);
                itemMeta.lore(builder.buildAsComponents());
            }
        }
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder withKey(String key, PersistentDataType dataType, Object value) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        if (itemMeta != null) {
            this.itemStack.setItemMeta(itemMeta);
            ItemNbt.withCustomData(this.itemStack, key, value, dataType);
            this.itemStack.setItemMeta(itemMeta);
        }
        return this;
    }

    public ItemBuilder setColor(Color color) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        if (itemMeta instanceof LeatherArmorMeta leatherArmorMeta) {
            leatherArmorMeta.setColor(color);
            this.itemStack.setItemMeta(leatherArmorMeta);
        }
        return this;
    }

    public ItemBuilder setColor(int color) {
        if (color < 0) {
            return this;
        }
        return this.setColor(Color.fromRGB(color));
    }

    public ItemBuilder addEnchant(Enchantment enchantment, int level, boolean ignoreLevelRestriction) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        if (itemMeta == null) {
            return this;
        }
        itemMeta.addEnchant(enchantment, level, ignoreLevelRestriction);
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder addEnchant(Enchantment enchantment, int level) {
        return this.addEnchant(enchantment, level, true);
    }

    public ItemBuilder addFlags(ItemFlag... itemFlag) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        if (itemMeta == null) {
            return this;
        }
        itemMeta.addItemFlags(itemFlag);
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder withCustomMeta(Function<ItemMeta, ItemMeta> function) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        if (itemMeta != null) {
            this.itemStack.setItemMeta(function.apply(itemMeta));
        }
        return this;
    }

    public ItemBuilder setSkin(String texture) {
        if (texture == null || texture.isEmpty()) {
            return this;
        }
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        if (itemMeta instanceof SkullMeta skullMeta) {
            PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
            profile.setProperty(new ProfileProperty("textures", texture));
            skullMeta.setPlayerProfile(profile);
            this.itemStack.setItemMeta(skullMeta);
        }
        return this;
    }

    public ItemStack asItemStack() {
        return this.itemStack;
    }

    public ItemStack toItemStack() {
        return this.itemStack;
    }
}