package pl.v3bc.platform.utils.menu;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import pl.v3bc.platform.utils.NekoChat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


/**
 * @Author: v3bc_
 * @Date: 8/23/26
 * @Project: astra-platform
 */

public final class ItemBuilder {
    private final ItemStack itemStack;
    private final ItemMeta itemMeta;

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

    public static ItemBuilder of(ItemStack itemStack) {
        return new ItemBuilder(itemStack.clone());
    }

    public ItemBuilder name(String name) {
        this.itemMeta.displayName(NekoChat.translate(name));
        this.refreshMeta();
        return this;
    }

    public ItemBuilder lore(List<String> strings) {
        this.itemMeta.lore(NekoChat.translate(strings));
        this.refreshMeta();
        return this;
    }

    public ItemBuilder lore(String... strings) {
        return this.lore(Arrays.asList(strings));
    }

    public ItemBuilder appendLore(String... lines) {
        return this.appendLore(Arrays.asList(lines));
    }

    public ItemBuilder appendLore(List<String> strings) {
        List<Component> lore = this.itemMeta.hasLore() ? this.itemMeta.lore() : new ArrayList<>();
        if (lore == null) {
            lore = new ArrayList<>();
        }
        lore.addAll(NekoChat.translate(strings));
        this.itemMeta.lore(lore);
        this.refreshMeta();
        return this;
    }

    public ItemBuilder glow() {
        this.itemMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
        this.itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        this.refreshMeta();
        return this;
    }

    public ItemBuilder amount(int amount) {
        this.itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder flag(ItemFlag... itemFlags) {
        this.itemMeta.addItemFlags(itemFlags);
        this.refreshMeta();
        return this;
    }

    public ItemBuilder enchantment(Enchantment enchantment, int level) {
        this.itemMeta.addEnchant(enchantment, level, true);
        this.refreshMeta();
        return this;
    }

    @SuppressWarnings("deprecation")
    public ItemBuilder setCustomModelData(int customModelData) {
        this.itemMeta.setCustomModelData(customModelData);
        this.refreshMeta();
        return this;
    }

    public ItemBuilder texture(String texture) {
        if (this.itemStack.getType() != Material.PLAYER_HEAD) {
            return this;
        }
        if (!(this.itemMeta instanceof SkullMeta skullMeta)) {
            return this;
        }
        this.setSkullTexture(skullMeta, texture);
        this.refreshMeta();
        return this;
    }

    public void setSkullTexture(SkullMeta meta, String textureValue) {
        if (textureValue == null || textureValue.isEmpty()) {
            return;
        }

        try {
            com.destroystokyo.paper.profile.PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
            profile.setProperty(new com.destroystokyo.paper.profile.ProfileProperty("textures", textureValue));
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

    public ItemBuilder placeholder(String key, String value) {
        if (this.itemMeta == null || key == null) return this;

        String replacementValue = value != null ? value : "";

        if (this.itemMeta.hasDisplayName()) {
            Component currentName = this.itemMeta.displayName();
            if (currentName != null) {
                this.itemMeta.displayName(replaceInComponent(currentName, key, replacementValue));
            }
        }

        if (this.itemMeta.hasLore()) {
            List<Component> lore = this.itemMeta.lore();
            if (lore != null) {
                List<Component> updatedLore = lore.stream()
                        .map(line -> replaceInComponent(line, key, replacementValue))
                        .toList();
                this.itemMeta.lore(updatedLore);
            }
        }

        this.refreshMeta();
        return this;
    }

    private Component replaceInComponent(Component target, String key, String value) {
        boolean containsMiniMessage = value.contains("<") && value.contains(">");

        net.kyori.adventure.text.TextReplacementConfig config = net.kyori.adventure.text.TextReplacementConfig.builder()
                .matchLiteral(key)
                .replacement(containsMiniMessage ? NekoChat.translate(value) : Component.text(value))
                .build();

        return target.replaceText(config)
                .decoration(net.kyori.adventure.text.format.TextDecoration.ITALIC, false);
    }

    public ItemMeta getMeta() {
        return this.itemMeta;
    }

    public void refreshMeta() {
        this.itemStack.setItemMeta(this.itemMeta);
    }

    public ItemStack asItemStack() {
        return this.itemStack;
    }
}