/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.structure.BlockStructureObserver;
import hellfirepvp.astralsorcery.common.tile.TileBore;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.MiscUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockBore
 * Created by HellFirePvP
 * Date: 03.11.2017 / 14:49
 */
public class BlockBore extends BlockContainer implements BlockStructureObserver {

    public BlockBore() {
        super(Material.wood);
        setHarvestLevel("axe", 2);
        setHardness(3.0F);
        setStepSound(Block.soundTypeWood);
        setResistance(25.0F);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer playerIn, int side, float hitX,
        float hitY, float hitZ) {
        BlockPos down = new BlockPos(x, y - 1, z);
        // 1.7.10: getBlock returns Block directly
        Block blockBelow = worldIn.getBlock(down.getX(), down.getY(), down.getZ());
        if (blockBelow.isReplaceable(worldIn, down.getX(), down.getY(), down.getZ())) {
            // 1.7.10: getTileAt takes x, y, z coordinates
            TileBore tb = MiscUtils.getTileAt(worldIn, x, y, z, TileBore.class);
            ItemStack held = playerIn.getCurrentEquippedItem();
            if (tb != null && !(held == null || held.stackSize <= 0) && held.getItem() instanceof ItemBlock
            // 1.7.10: ItemBlock has field_150939_a for the block
                && ((ItemBlock) held.getItem()).field_150939_a instanceof BlockBoreHead) {
                if (!worldIn.isRemote) {
                    if (worldIn.setBlock(down.getX(), down.getY(), down.getZ(), BlocksAS.blockBoreHead, 0, 3)) {
                        if (!playerIn.capabilities.isCreativeMode) {
                            held.stackSize -= 1;
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public int getRenderType() {
        return -1; // Custom model renderer
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileBore();
    }

}
