/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.base.patreon.PatreonEffectHelper;
import hellfirepvp.astralsorcery.common.base.patreon.base.PtEffectTreeBeacon;
import hellfirepvp.astralsorcery.common.migration.ParticleManager;
import hellfirepvp.astralsorcery.common.tile.TileFakeTree;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockFakeTree
 * Created by HellFirePvP
 * Date: 11.11.2016 / 20:31
 */
public class BlockFakeTree extends BlockContainer {

    public BlockFakeTree() {
        super(Material.rock);
        setBlockUnbreakable();
        setResistance(6000001.0F);
        setLightLevel(0.6F);
    }

    // 1.7.10: addDestroyEffects doesn't override in 1.7.10, remove @Override
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(World world, int x, int y, int z, int meta, ParticleManager manager) {
        // 1.7.10: Use world.getTileEntity directly
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileFakeTree) {
            TileFakeTree tft = (TileFakeTree) te;
            if (tft.getFakedState() != null) {
                // 1.7.10: playBlockBreakParticles takes BlockPos and Block
                RenderingUtils.playBlockBreakParticles(
                    new hellfirepvp.astralsorcery.common.util.BlockPos(x, y, z),
                    tft.getFakedState());
            }
        }
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World worldIn, int x, int y, int z, Random rand) {
        // 1.7.10: Use worldIn.getTileEntity directly
        TileEntity te = worldIn.getTileEntity(x, y, z);
        if (!(te instanceof TileFakeTree)) return;
        TileFakeTree tft = (TileFakeTree) te;
        if (tft.getReference() == null) return;
        if (rand.nextInt(20) == 0) {
            Color c = new Color(63, 255, 63);
            PatreonEffectHelper.PatreonEffect pe;
            if (tft.getPlayerEffectRef() != null) {
                Collection<PatreonEffectHelper.PatreonEffect> effects = PatreonEffectHelper
                    .getPatreonEffects(Side.CLIENT, tft.getPlayerEffectRef());
                for (PatreonEffectHelper.PatreonEffect effect : effects) {
                    if (effect instanceof PtEffectTreeBeacon) {
                        pe = effect;
                        c = new Color(((PtEffectTreeBeacon) pe).getColorTreeDrainEffects());
                        break;
                    }
                }
            }

            EntityFXFacingParticle p = EffectHelper
                .genericFlareParticle(x + rand.nextFloat(), y + rand.nextFloat(), z + rand.nextFloat());
            p.motion(0, 0, 0);
            p.scale(0.45F)
                .setColor(c)
                .setMaxAge(65);
        }
    }

    // 1.7.10: getStepSound doesn't override in BlockContainer
    public Block.SoundType getStepSound(int metadata) {
        // 1.7.10: stepSound is Block.SoundType, not StepSound
        return stepSound;
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        return new ArrayList<>();
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public Item getItemDropped(int metadata, Random rand, int fortune) {
        // 1.7.10: null doesn't exist, return null
        return null;
    }

    @Override
    public int getRenderType() {
        return -1; // Invisible in 1.7.10
    }

    // 1.7.10: isTranslucent doesn't override in BlockContainer, remove @Override
    @SideOnly(Side.CLIENT)
    public boolean isTranslucent(int meta) {
        return true;
    }

    // 1.7.10: isTopSolid doesn't override in BlockContainer, remove @Override
    public boolean isTopSolid(Block block) {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    // 1.7.10: isNormalBlock doesn't override in BlockContainer, remove @Override
    public boolean isNormalBlock() {
        return false;
    }

    // 1.7.10: isSideSolid doesn't override in BlockContainer, remove @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, int side) {
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return null;
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileFakeTree();
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        // 1.7.10: Use world.getTileEntity directly
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileFakeTree) {
            TileFakeTree tft = (TileFakeTree) te;
            try {
                if (tft.getFakedState() != null) {
                    return tft.getFakedState()
                        .getPickBlock(target, world, x, y, z);
                }
            } catch (Exception ignored) {}
        }
        return null;
    }

}
