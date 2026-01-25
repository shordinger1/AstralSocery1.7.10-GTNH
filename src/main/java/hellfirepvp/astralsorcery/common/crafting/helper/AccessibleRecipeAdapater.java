/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.helper;

import java.util.ArrayList;

import javax.annotation.Nullable;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.migration.Ingredient;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AccessibleRecipeAdapater
 * Created by HellFirePvP
 * Date: 06.10.2016 / 14:26
 */
public class AccessibleRecipeAdapater extends AccessibleRecipe {

    private final IRecipe parent;
    private final AbstractRecipeAccessor abstractRecipe;

    public AccessibleRecipeAdapater(IRecipe parent, AbstractRecipeAccessor abstractRecipe) {
        super(null); // 1.7.10 IRecipe doesn't have getRegistryName()
        this.parent = parent;
        this.abstractRecipe = abstractRecipe;
    }

    @Nullable
    @Override
    @SideOnly(Side.CLIENT)
    public ArrayList<ItemStack> getExpectedStackForRender(int row, int column) {
        ItemHandle handle = abstractRecipe.getExpectedStack(row, column);
        if (handle == null) return new ArrayList<>();
        return refactorSubItems(handle.getApplicableItemsForRender());
    }

    @Nullable
    public ItemHandle getExpectedStackHandle(int row, int column) {
        return abstractRecipe.getExpectedStack(row, column);
    }

    @Nullable
    @Override
    @SideOnly(Side.CLIENT)
    public ArrayList<ItemStack> getExpectedStackForRender(ShapedRecipeSlot slot) {
        ItemHandle handle = abstractRecipe.getExpectedStack(slot);
        if (handle == null) return new ArrayList<>();
        return refactorSubItems(handle.getApplicableItemsForRender());
    }

    @Nullable
    public ItemHandle getExpectedStackHandle(ShapedRecipeSlot slot) {
        return abstractRecipe.getExpectedStack(slot);
    }

    @SideOnly(Side.CLIENT)
    private ArrayList<ItemStack> refactorSubItems(ArrayList<ItemStack> applicableItems) {
        ArrayList<ItemStack> out = new ArrayList<>();
        for (ItemStack oreDictIn : applicableItems) {
            if (oreDictIn.getItemDamage() == OreDictionary.WILDCARD_VALUE && !oreDictIn.isItemStackDamageable()) {
                oreDictIn.getItem()
                    .getSubItems(
                        oreDictIn.getItem(),
                        oreDictIn.getItem()
                            .getCreativeTab(),
                        out);
            } else {
                out.add(oreDictIn);
            }
        }
        return out;
    }

    public ArrayList<Ingredient> getIngredients() {
        // 1.7.10 IRecipe doesn't have getIngredients(), return empty list
        return new ArrayList<>();
    }

    public String getGroup() {
        // 1.7.10 IRecipe doesn't have getGroup(), return empty string
        return "";
    }

    public boolean isDynamic() {
        // 1.7.10 IRecipe doesn't have isDynamic(), return false
        return false;
    }

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        return parent.matches(inv, worldIn);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        return parent.getCraftingResult(inv);
    }

    @Override
    public int getRecipeSize() {
        return parent.getRecipeSize();
    }

    public boolean canFit(int width, int height) {
        // 1.7.10 IRecipe doesn't have canFit(), use getRecipeSize() instead
        return width * height >= parent.getRecipeSize();
    }

    @Override
    public ItemStack getRecipeOutput() {
        return parent.getRecipeOutput();
    }

    public ArrayList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        // 1.7.10 IRecipe doesn't have getRemainingItems(), return empty list
        return new ArrayList<>();
    }

    public IRecipe getParentRecipe() {
        return parent;
    }
}
