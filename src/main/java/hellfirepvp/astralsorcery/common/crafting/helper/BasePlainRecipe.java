/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.helper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;

import hellfirepvp.astralsorcery.AstralSorcery;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BasePlainRecipe
 * Created by HellFirePvP
 * Date: 18.06.2017 / 12:17
 */
public abstract class BasePlainRecipe implements IRecipe {

    private ResourceLocation registryName;
    private String group = "";

    protected BasePlainRecipe(@Nonnull String recipeName) {
        this(new ResourceLocation(AstralSorcery.MODID, recipeName));
    }

    protected BasePlainRecipe(@Nullable ResourceLocation registryName) {
        this.registryName = registryName;
    }

    public final void setGroup(String group) {
        this.group = group == null ? "" : group;
    }

    public IRecipe setRegistryName(ResourceLocation name) {
        this.registryName = name;
        return this;
    }

    @Nullable
    public ResourceLocation getRegistryName() {
        return this.registryName;
    }

    public Class<IRecipe> getRegistryType() {
        return IRecipe.class;
    }

    public String getGroup() {
        return group;
    }

    /**
     * Returns the size of the recipe area
     * Required by 1.7.10 IRecipe interface
     */
    public abstract int getRecipeSize();

}
