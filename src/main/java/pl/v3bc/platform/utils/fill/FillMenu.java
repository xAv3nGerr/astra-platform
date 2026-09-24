package pl.v3bc.platform.utils.fill;

import me.devnatan.inventoryframework.context.RenderContext;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import pl.v3bc.platform.utils.ItemBuilder;

public class FillMenu {

    public static final int[] BLUE_SLOTS_6 = {0, 8, 45, 53};
    public static final int[] LIGHT_BLUE_SLOTS_6 = {1, 7, 9, 17, 36, 44, 46, 52};
    public static final int[] WHITE_SLOTS_6 = {2, 6, 18, 26, 27, 35, 47, 51};
    public static final int[] IRON_BARS_6 = {3, 5, 48, 50};
    public static final int[] CANDLE_6 = {4, 49};

    public static final int[] BLUE_SLOTS_5 = {0, 8, 36, 44};
    public static final int[] LIGHT_BLUE_SLOTS_5 = {1, 7, 9, 17, 27, 35, 37, 43};
    public static final int[] WHITE_SLOTS_5 = {2, 6, 18, 26, 38, 42};
    public static final int[] IRON_BARS_5 = {3, 5, 39, 41};
    public static final int[] CANDLE_5 = {4, 40};

    public static final int[] BLUE_SLOTS_3 = {0, 8, 18, 26};
    public static final int[] LIGHT_BLUE_SLOTS_3 = {1, 7, 9, 17, 19, 25};
    public static final int[] WHITE_SLOTS_3 = {2, 3, 5, 6, 20, 21,  23, 24};

    public static void fill6Rows(RenderContext render) {
        ItemStack glass1 = ItemBuilder.of(Material.BLUE_STAINED_GLASS_PANE).name(" ").asItemStack();
        ItemStack glass2 = ItemBuilder.of(Material.LIGHT_BLUE_STAINED_GLASS_PANE).name(" ").asItemStack();
        ItemStack glass3 = ItemBuilder.of(Material.WHITE_STAINED_GLASS_PANE).name(" ").asItemStack();
        ItemStack ironBars = ItemBuilder.of(Material.IRON_BARS).name(" ").asItemStack();
        ItemStack candle = ItemBuilder.of(Material.BLUE_CANDLE).name(" ").asItemStack();

        for (int slot : BLUE_SLOTS_6) {
            render.slot(slot, glass1);
        }
        for (int slot : LIGHT_BLUE_SLOTS_6) {
            render.slot(slot, glass2);
        }
        for (int slot : WHITE_SLOTS_6) {
            render.slot(slot, glass3);
        }
        for (int slot : IRON_BARS_6) {
            render.slot(slot, ironBars);
        }
        for (int slot : CANDLE_6) {
            render.slot(slot, candle);
        }
    }

    public static void fill5Rows(RenderContext render) {
        ItemStack glass1 = ItemBuilder.of(Material.BLUE_STAINED_GLASS_PANE).name(" ").asItemStack();
        ItemStack glass2 = ItemBuilder.of(Material.LIGHT_BLUE_STAINED_GLASS_PANE).name(" ").asItemStack();
        ItemStack glass3 = ItemBuilder.of(Material.WHITE_STAINED_GLASS_PANE).name(" ").asItemStack();
        ItemStack ironBars = ItemBuilder.of(Material.IRON_BARS).name(" ").asItemStack();
        ItemStack candle = ItemBuilder.of(Material.BLUE_CANDLE).name(" ").asItemStack();

        for (int slot : BLUE_SLOTS_5) {
            render.slot(slot, glass1);
        }
        for (int slot : LIGHT_BLUE_SLOTS_5) {
            render.slot(slot, glass2);
        }
        for (int slot : WHITE_SLOTS_5) {
            render.slot(slot, glass3);
        }
        for (int slot : IRON_BARS_5) {
            render.slot(slot, ironBars);
        }
        for (int slot : CANDLE_5) {
            render.slot(slot, candle);
        }
    }

    public static void fill3Rows(RenderContext render) {
        ItemStack glass1 = ItemBuilder.of(Material.BLUE_STAINED_GLASS_PANE).name(" ").asItemStack();
        ItemStack glass2 = ItemBuilder.of(Material.LIGHT_BLUE_STAINED_GLASS_PANE).name(" ").asItemStack();
        ItemStack glass3 = ItemBuilder.of(Material.WHITE_STAINED_GLASS_PANE).name(" ").asItemStack();

        for (int slot : BLUE_SLOTS_3) {
            render.slot(slot, glass1);
        }
        for (int slot : LIGHT_BLUE_SLOTS_3) {
            render.slot(slot, glass2);
        }
        for (int slot : WHITE_SLOTS_3) {
            render.slot(slot, glass3);
        }
    }
}
