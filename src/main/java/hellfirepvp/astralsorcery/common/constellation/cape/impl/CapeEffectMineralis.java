/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.cape.impl;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.block.EffectTranslucentFallingBlock;
import hellfirepvp.astralsorcery.common.base.Mods;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.cape.CapeArmorEffect;
import hellfirepvp.astralsorcery.common.integrations.ModIntegrationOreStages;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CapeEffectMineralis
 * Created by HellFirePvP
 * Date: 17.10.2017 / 00:35
 */
public class CapeEffectMineralis extends CapeArmorEffect {

    private static int highlightRange = 20;

    public CapeEffectMineralis(NBTTagCompound cmp) {
        super(cmp, "mineralis");
    }

    @Override
    public IConstellation getAssociatedConstellation() {
        return Constellations.mineralis;
    }

    @Override
    public void playActiveParticleTick(EntityPlayer pl) {
        playConstellationCapeSparkles(pl, 0.15F);
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        highlightRange = cfg.getInt(
            getKey() + "HighlightRange",
            getConfigurationSection(),
            highlightRange,
            4,
            64,
            "Sets the highlight radius in which the cape effect will search for the block you're holding.");
    }

    @SideOnly(Side.CLIENT)
    public void playClientHighlightTick(EntityPlayer pl) {
        if (rand.nextFloat() > 0.7F) return;

        ItemStack main = pl.getCurrentEquippedItem();
        Block check = null;
        int itemMeta = 0;
        if (!(main == null || main.stackSize <= 0)) {
            try {
                check = ItemUtils.createBlockState(main);
                itemMeta = main.getItemDamage();
            } catch (Exception e) {}
        }
        // 1.7.10: No off-hand, skip offhand check

        if (check != null) {
            if (Mods.ORESTAGES.isPresent()) {
                if (!ModIntegrationOreStages.canSeeOreClient(check)) {
                    return;
                }
            }

            Block b;
            int meta;
            try {
                b = check;
                meta = itemMeta;
            } catch (Exception e) {
                return;
            }
            List<BlockPos> blocks = MiscUtils.searchAreaFor(pl.worldObj, new BlockPos(pl), b, meta, highlightRange);
            if (blocks.isEmpty()) return;

            int index = blocks.size() > 10 ? rand.nextInt(blocks.size()) : rand.nextInt(10);
            if (index >= blocks.size()) {
                return;
            }
            BlockPos at = blocks.get(index);
            Block act = pl.worldObj.getBlock(at.getX(), at.getY(), at.getZ());
            EffectTranslucentFallingBlock bl = EffectHandler.getInstance()
                .translucentFallingBlock(new Vector3(at).add(0.5, 0.5, 0.5), act);
            bl.setDisableDepth(true)
                .setScaleFunction(new EntityComplexFX.ScaleFunction.Shrink<>());
            bl.setMotion(0, 0.02, 0)
                .setAlphaFunction(EntityComplexFX.AlphaFunction.PYRAMID);
            bl.tumble();
            bl.setMaxAge(40 + rand.nextInt(15));
        }
    }

}
