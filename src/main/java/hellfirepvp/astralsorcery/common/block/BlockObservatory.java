/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Observatory - Celestial observation device
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.cleanroommc.modularui.factory.TileEntityGuiFactory;

import hellfirepvp.astralsorcery.common.lib.CreativeTabsAS;
import hellfirepvp.astralsorcery.common.tile.TileObservatory;

/**
 * BlockObservatory - Celestial observatory (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Observatory for constellation viewing</li>
 * <li>Has TileEntity (TileObservatory)</li>
 * <li>Multiblock structure</li>
 * <li>GUI for constellation observation</li>
 * </ul>
 */
public class BlockObservatory extends BlockContainer {

    public BlockObservatory() {
        super(Material.rock);
        setHardness(3.5F);
        setResistance(20.0F);
        setStepSound(soundTypeStone);
        setHarvestLevel("pickaxe", 1);
        setCreativeTab(CreativeTabsAS.ASTRAL_SORCERY_TAB);
    }

    @Override
    public int getRenderType() {
        return -1; // Use TileEntitySpecialRenderer
    }

    @Override
    public boolean isOpaqueCube() {
        return false; // Not a full block
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false; // Special rendering
    }

    /**
     * Check if block can be placed at this position
     * 1.7.10: Simplified version - checks for space around the block
     * Original version: Uses BlockPos.PooledMutableBlockPos
     * 1.7.10 version: Direct coordinate checking
     */
    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        // Check for 3x3x4 space (x-1 to x+1, y to y+3, z-1 to z+1)
        for (int xx = -1; xx <= 1; xx++) {
            for (int yy = 0; yy <= 3; yy++) {
                for (int zz = -1; zz <= 1; zz++) {
                    int checkX = x + xx;
                    int checkY = y + yy;
                    int checkZ = z + zz;

                    // Skip the block itself
                    if (xx == 0 && yy == 0 && zz == 0) continue;

                    // Check if space is free
                    if (!world.isAirBlock(checkX, checkY, checkZ)) {
                        net.minecraft.block.Block block = world.getBlock(checkX, checkY, checkZ);
                        // Check if block is replaceable
                        if (block != null && !block.getMaterial().isReplaceable()) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * On block activated - open GUI or mount observatory
     * 1.7.10: Removed EnumHand (off-hand is 1.9+ feature)
     * Removed: getRidingEntity() mount system (needs EntityObservatory implementation)
     * Current: Directly opens GUI
     */
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        if (!world.isRemote) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof TileObservatory) {
                TileObservatory observatory = (TileObservatory) te;

                // TODO: Re-enable after EntityObservatory is implemented
                // 1.12.2 version has player mount the observatory entity
                // Entity e = observatory.findRideableObservatoryEntity();
                // if (e != null) {
                //     if(player.ridingEntity == null) {
                //         player.mountEntity(e);
                //     }
                //     // Open GUI
                // }

                // Check if observatory is usable
                if (!player.isSneaking() && observatory.isUsable()) {
                    // Open ModularUI GUI
                    com.cleanroommc.modularui.factory.TileEntityGuiFactory.INSTANCE.open(player, observatory);
                }
            }
        }
        return true;
    }

    public Item getItemDropped(int meta, Random rand, int fortune) {
        return Item.getItemFromBlock(this);

    }

    public int quantityDropped(Random rand) {
        return 1;

    }

    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
        drops.add(new ItemStack(this, 1, 0));
        return drops;

    }

    public TileEntity createNewTileEntity(World world, int meta) {
        return new hellfirepvp.astralsorcery.common.tile.TileObservatory();
    }
}
