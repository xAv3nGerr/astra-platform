package pl.v3bc.platform.utils.menu;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import pl.v3bc.platform.utils.adventure.NekoChat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @Author: v3bc_
 * @Date: 8/23/26
 * @Project: astra-platform
 */
public final class ItemBuilder {
    private static final NamespacedKey RAW_NAME_KEY = new NamespacedKey("astra", "item_raw_name");
    private static final NamespacedKey RAW_LORE_KEY = new NamespacedKey("astra", "item_raw_lore");
    private static final String LORE_SEPARATOR = "\u241E";

    private final ItemStack itemStack;
    private final ItemMeta itemMeta;

    private String rawName = null;
    private List<String> rawLore = new ArrayList<>();

    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.itemMeta = itemStack.getItemMeta();

        if (this.itemMeta != null) {
            PersistentDataContainer pdc = this.itemMeta.getPersistentDataContainer();
            if (pdc.has(RAW_NAME_KEY, PersistentDataType.STRING)) {
                this.rawName = pdc.get(RAW_NAME_KEY, PersistentDataType.STRING);
            }
            if (pdc.has(RAW_LORE_KEY, PersistentDataType.STRING)) {
                String joined = pdc.get(RAW_LORE_KEY, PersistentDataType.STRING);
                this.rawLore = new ArrayList<>(Arrays.asList(joined.split(LORE_SEPARATOR, -1)));
            }
        }
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
        this.rawName = name;
        if (this.itemMeta != null) {
            this.itemMeta.displayName(NekoChat.translate(name));
            this.itemMeta.getPersistentDataContainer().set(RAW_NAME_KEY, PersistentDataType.STRING, name);
            this.refreshMeta();
        }
        return this;
    }

    public ItemBuilder lore(List<String> strings) {
        this.rawLore = new ArrayList<>(strings);
        if (this.itemMeta != null) {
            this.itemMeta.lore(NekoChat.translate(strings));
            this.itemMeta.getPersistentDataContainer().set(RAW_LORE_KEY, PersistentDataType.STRING, String.join(LORE_SEPARATOR, strings));
            this.refreshMeta();
        }
        return this;
    }

    public ItemBuilder lore(String... strings) {
        return this.lore(Arrays.asList(strings));
    }

    public ItemBuilder appendLore(String... lines) {
        return this.appendLore(Arrays.asList(lines));
    }

    public ItemBuilder appendLore(List<String> strings) {
        if (this.rawLore == null) {
            this.rawLore = new ArrayList<>();
        }
        this.rawLore.addAll(strings);

        if (this.itemMeta != null) {
            List<Component> lore = this.itemMeta.hasLore() ? this.itemMeta.lore() : new ArrayList<>();
            if (lore == null) {
                lore = new ArrayList<>();
            }
            lore.addAll(NekoChat.translate(strings));
            this.itemMeta.lore(lore);
            this.itemMeta.getPersistentDataContainer().set(RAW_LORE_KEY, PersistentDataType.STRING, String.join(LORE_SEPARATOR, this.rawLore));
            this.refreshMeta();
        }
        return this;
    }

    public ItemBuilder placeholder(String key, String value) {
        if (this.itemMeta == null || key == null) return this;
        String replacementValue = value != null ? value : "";

        if (this.rawName != null) {
            this.rawName = this.rawName.replace(key, replacementValue);
            this.itemMeta.displayName(NekoChat.translate(this.rawName));
            this.itemMeta.getPersistentDataContainer().set(RAW_NAME_KEY, PersistentDataType.STRING, this.rawName);
        }

        if (this.rawLore != null && !this.rawLore.isEmpty()) {
            List<String> updatedRawLore = new ArrayList<>();
            for (String line : this.rawLore) {
                updatedRawLore.add(line.replace(key, replacementValue));
            }
            this.rawLore = updatedRawLore;
            this.itemMeta.lore(NekoChat.translate(this.rawLore));
            this.itemMeta.getPersistentDataContainer().set(RAW_LORE_KEY, PersistentDataType.STRING, String.join(LORE_SEPARATOR, this.rawLore));
        }

        this.refreshMeta();
        return this;
    }

    public ItemBuilder glow() {
        if (this.itemMeta != null) {
            this.itemMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
            this.itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            this.refreshMeta();
        }
        return this;
    }

    public ItemBuilder amount(int amount) {
        this.itemStack.setAmount(amount);
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

    @SuppressWarnings("deprecation")
    public ItemBuilder setCustomModelData(int customModelData) {
        if (this.itemMeta != null) {
            this.itemMeta.setCustomModelData(customModelData);
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

    public ItemMeta getMeta() {
        return this.itemMeta;
    }

    public void refreshMeta() {
        if (this.itemMeta != null) {
            this.itemStack.setItemMeta(this.itemMeta);
        }
    }

    public ItemStack asItemStack() {
        return this.itemStack;
    }
}