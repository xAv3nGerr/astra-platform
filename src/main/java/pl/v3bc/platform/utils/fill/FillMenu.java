package pl.v3bc.platform.utils.fill;


import me.devnatan.inventoryframework.context.RenderContext;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import pl.v3bc.platform.utils.ItemBuilder;

public class FillMenu {

    public static final int[] BLUE_SLOTS = {0, 8, 36, 44};
    public static final int[] LIGHT_BLUE_SLOTS = {1, 7, 9, 17, 27, 35, 37, 43};
    public static final int[] WHITE_SLOTS = {2, 6, 18, 26, 38, 42};
    public static final int[] IRON_BARS = {3, 5, 39, 41};
    public static final int[] CANDLE = {4, 40};

    public static void fillIron(RenderContext render) {
        ItemStack glass1 = ItemBuilder.of(Material.BLUE_STAINED_GLASS_PANE).name(" ").asItemStack();
        ItemStack glass2 = ItemBuilder.of(Material.LIGHT_BLUE_STAINED_GLASS_PANE).name(" ").asItemStack();
        ItemStack glass3 = ItemBuilder.of(Material.WHITE_STAINED_GLASS_PANE).name(" ").asItemStack();
        ItemStack ironbars = ItemBuilder.of(Material.IRON_BARS).name(" ").asItemStack();
        ItemStack candle = ItemBuilder.of(Material.BLUE_CANDLE).name(" ").asItemStack();

        for (int slot : BLUE_SLOTS) {
            render.slot(slot, glass1);
        }
        for (int slot : LIGHT_BLUE_SLOTS) {
            render.slot(slot, glass2);
        }
        for (int slot : WHITE_SLOTS) {
            render.slot(slot, glass3);
        }
        for (int slot: IRON_BARS) {
            render.slot(slot, ironbars);
        }
        for (int slot: CANDLE) {
            render.slot(slot, candle);
        }
    }
}