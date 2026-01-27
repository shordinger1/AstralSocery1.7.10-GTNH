/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.oredict.ShapedOreRecipe;

import hellfirepvp.astralsorcery.common.crafting.helper.BasePlainRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapeMap;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.data.DataLightBlockEndpoints;
import hellfirepvp.astralsorcery.common.data.SyncDataHolder;
import hellfirepvp.astralsorcery.common.migration.Ingredient;
import hellfirepvp.astralsorcery.common.util.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ShapedLightProximityRecipe
 * Created by HellFirePvP
 * Date: 02.08.2016 / 22:57
 */
public class ShapedLightProximityRecipe extends BasePlainRecipe {

    public static BlockPos clientWorkbenchPosition = null;

    private final ItemStack out;
    private final ShapeMap.Baked grid;

    public ShapedLightProximityRecipe(ResourceLocation name, ItemStack out, ShapeMap.Baked grid) {
        super(name);
        this.out = out;
        this.grid = grid;
    }

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        if (!vanillaMatch(inv)) return false;

        Container c = inv.eventHandler;
        if (!(c instanceof ContainerWorkbench)) return false;
        // In 1.7.10, ContainerWorkbench doesn't have pos field - can't easily determine position
        // Skip this recipe check for now
        return false;
    }

    private boolean vanillaMatch(InventoryCrafting inv) {
        for (int x = 0; x <= 3 - grid.getWidth(); x++) {
            for (int y = 0; y <= 3 - grid.getHeight(); y++) {
                if (checkMatch(inv, x, y)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean checkMatch(InventoryCrafting inv, int startX, int startY) {
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                int subX = x - startX;
                int subY = y - startY;
                Ingredient target;

                if (subX >= 0 && subY >= 0 && subX < grid.getWidth() && subY < grid.getHeight()) {
                    target = grid.get(ShapedRecipeSlot.getByRowColumnIndex(subX, subY));

                    if (!target.apply(inv.getStackInRowAndColumn(y, x))) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        return out.copy();
    }

    public boolean canFit(int width, int height) {
        return width >= grid.getWidth() && height >= grid.getHeight();
    }

    @Override
    public int getRecipeSize() {
        return grid.getWidth() * grid.getHeight();
    }

    @Override
    public ItemStack getRecipeOutput() {
        return out.copy();
    }

    public ArrayList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        // In 1.7.10, defaultRecipeGetRemainingItems doesn't exist in ForgeHooks
        // Manually create container items
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack != null && stack.getItem().hasContainerItem()) {
                ItemStack container = stack.getItem().getContainerItem(stack);
                if (container != null && container.stackSize > 0) {
                    ret.add(container);
                }
            }
        }
        return ret;
    }

    public ArrayList<Ingredient> getIngredients() {
        return grid.getRawIngredientList();
    }

}
