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
import pl.v3bc.platform.utils.ChatUtil;
import pl.v3bc.platform.utils.nbt.ItemNbt;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public final class ItemBuilder {
    private final ItemStack itemStack;
    private ItemMeta itemMeta;

    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.itemMeta = itemStack.getItemMeta();
    }

    private ItemBuilder(Material material, int amount) {
        this.itemStack = new ItemStack(material, amount);
        this.itemMeta = this.itemStack.getItemMeta();
    }

    public static ItemBuilder of(Material material) {
        return new ItemBuilder(material, 1);
    }

    public static ItemBuilder of(Material material, int amount) {
        return new ItemBuilder(material, amount);
    }

    public static ItemBuilder of(ItemStack itemStack) {
        return new ItemBuilder(itemStack.clone());
    }

    public static ItemBuilder of(ItemStack itemStack, int amount) {
        ItemBuilder builder = new ItemBuilder(itemStack.clone());
        builder.amount(amount);
        return builder;
    }

    public ItemBuilder name(String name) {
        return this.name(name, Collections.emptyMap());
    }

    public ItemBuilder name(String name, String key, Object value) {
        return this.name(name, Map.of(key, value));
    }

    public ItemBuilder name(String name, Map<String, ?> placeholders) {
        if (this.itemMeta != null) {
            this.itemMeta.displayName(ChatUtil.component(name, placeholders));
            this.refreshMeta();
        }
        return this;
    }

    public ItemBuilder lore(List<String> strings) {
        return this.lore(strings, Collections.emptyMap());
    }

    public ItemBuilder lore(String... strings) {
        return this.lore(Arrays.asList(strings));
    }

    public ItemBuilder lore(List<String> strings, Map<String, ?> placeholders) {
        if (this.itemMeta != null) {
            this.itemMeta.lore(ChatUtil.component(strings, placeholders));
            this.refreshMeta();
        }
        return this;
    }

    public ItemBuilder startLoreWith(List<String> strings, Map<String, ?> placeholders) {
        if (this.itemMeta != null) {
            List<Component> newLore = ChatUtil.component(strings, placeholders);
            List<Component> current = this.itemMeta.lore();
            if (current != null && !current.isEmpty()) {
                this.itemMeta.lore(Stream.concat(newLore.stream(), current.stream()).toList());
            } else {
                this.itemMeta.lore(newLore);
            }
            this.refreshMeta();
        }
        return this;
    }

    public ItemBuilder startLoreWith(List<String> strings) {
        return this.startLoreWith(strings, Collections.emptyMap());
    }

    public ItemBuilder startLoreWith(String... strings) {
        return this.startLoreWith(Arrays.asList(strings));
    }

    public ItemBuilder appendLore(List<String> strings, Map<String, ?> placeholders) {
        if (this.itemMeta != null) {
            List<Component> newLore = ChatUtil.component(strings, placeholders);
            List<Component> current = this.itemMeta.lore();
            if (current != null && !current.isEmpty()) {
                this.itemMeta.lore(Stream.concat(current.stream(), newLore.stream()).toList());
            } else {
                this.itemMeta.lore(newLore);
            }
            this.refreshMeta();
        }
        return this;
    }

    public ItemBuilder appendLore(List<String> strings) {
        return this.appendLore(strings, Collections.emptyMap());
    }

    public ItemBuilder appendLore(String... lines) {
        return this.appendLore(Arrays.asList(lines));
    }

    public ItemBuilder amount(int amount) {
        this.itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder durability(int durability) {
        if (this.itemMeta instanceof Damageable damageable) {
            damageable.setDamage(durability);
            this.refreshMeta();
        }
        return this;
    }

    public ItemBuilder unbreakable(boolean unbreakable) {
        if (this.itemMeta != null) {
            this.itemMeta.setUnbreakable(unbreakable);
            this.refreshMeta();
        }
        return this;
    }

    public ItemBuilder color(Color color) {
        if (this.itemMeta instanceof LeatherArmorMeta leatherMeta) {
            leatherMeta.setColor(color);
            this.refreshMeta();
        }
        return this;
    }

    public ItemBuilder color(int rgb) {
        return rgb >= 0 ? this.color(Color.fromRGB(rgb)) : this;
    }

    public ItemBuilder glow() {
        if (this.itemMeta != null) {
            this.itemMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
            this.itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            this.refreshMeta();
        }
        return this;
    }

    public ItemBuilder flag(ItemFlag... itemFlags) {
        if (this.itemMeta != null) {
            this.itemMeta.addItemFlags(itemFlags);
            this.refreshMeta();
        }
        return this;
    }

    public ItemBuilder enchantment(Enchantment enchantment, int level) {
        if (this.itemMeta != null) {
            this.itemMeta.addEnchant(enchantment, level, true);
            this.refreshMeta();
        }
        return this;
    }

    public ItemBuilder setCustomModelData(int customModelData) {
        if (this.itemMeta != null) {
            this.itemMeta.setCustomModelData(customModelData);
            this.refreshMeta();
        }
        return this;
    }

    public ItemBuilder withKey(String key, PersistentDataType dataType, Object value) {
        this.refreshMeta();
        ItemNbt.withCustomData(this.itemStack, key, value, dataType);
        this.itemMeta = this.itemStack.getItemMeta();
        return this;
    }

    public ItemBuilder withCustomMeta(Function<ItemMeta, ItemMeta> function) {
        if (this.itemMeta != null) {
            this.itemMeta = function.apply(this.itemMeta);
            this.refreshMeta();
        }
        return this;
    }

    public ItemBuilder texture(String texture) {
        if (this.itemStack.getType() != Material.PLAYER_HEAD || !(this.itemMeta instanceof SkullMeta skullMeta)) {
            return this;
        }
        this.setSkullTexture(skullMeta, texture);
        this.refreshMeta();
        return this;
    }

    public void setSkullTexture(SkullMeta meta, String textureValue) {
        if (textureValue == null || textureValue.isEmpty()) return;

        try {
            PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
            profile.setProperty(new ProfileProperty("textures", textureValue));
            meta.setPlayerProfile(profile);
        } catch (Throwable e) {
            try {
                org.bukkit.profile.PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
                profile.getTextures().setSkin(new java.net.URL("https://textures.minecraft.net/texture/" + textureValue));
                meta.setOwnerProfile(profile);
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
    }

    public ItemMeta getMeta() {
        return this.itemMeta;
    }

    public void refreshMeta() {
        if (this.itemMeta != null) {
            this.itemStack.setItemMeta(this.itemMeta);
        }
    }

    public ItemStack asItemStack() {
        this.refreshMeta();
        return this.itemStack;
    }

    public ItemStack toItemStack() {
        return asItemStack();
    }
}