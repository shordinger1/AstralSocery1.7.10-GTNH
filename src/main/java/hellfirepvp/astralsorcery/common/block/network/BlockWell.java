/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.network;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import hellfirepvp.astralsorcery.common.base.WellLiquefaction;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.tile.TileWell;
import hellfirepvp.astralsorcery.common.tile.base.TileReceiverBaseInventory;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.WrapMathHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockWell
 * Created by HellFirePvP
 * Date: 18.10.2016 / 12:43
 */
public class BlockWell extends BlockStarlightNetwork {

    private static final AxisAlignedBB boxWell = AxisAlignedBB
        .getBoundingBox(1D / 16D, 0D, 1D / 16D, 15D / 16D, 1, 15D / 16D);
    private static final List<AxisAlignedBB> collisionBoxes;

    public BlockWell() {
        super(Material.rock);
        setHardness(3.0F);
        setStepSound(Block.soundTypePiston);
        setResistance(25.0F);
        setHarvestLevel("pickaxe", 2);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileWell();
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return createNewTileEntity(world, metadata);
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        TileWell tw = MiscUtils.getTileAt(world, x, y, z, TileWell.class);
        if (tw != null) {
            if (tw.getHeldFluid() != null) {
                return tw.getHeldFluid()
                    .getLuminosity();
            }
        }
        return super.getLightValue(world, x, y, z);
    }

    @Override
    public int getLightOpacity(IBlockAccess world, int x, int y, int z) {
        return 0;
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer playerIn, int side, float hitX,
        float hitY, float hitZ) {
        if (!worldIn.isRemote) {

            ItemStack heldItem = playerIn.getCurrentEquippedItem();
            if (!(heldItem == null || heldItem.stackSize <= 0) && playerIn instanceof EntityPlayerMP) {
                TileWell tw = MiscUtils.getTileAt(worldIn, x, y, z, TileWell.class);
                if (tw == null) return false;

                WellLiquefaction.LiquefactionEntry entry = WellLiquefaction.getLiquefactionEntry(heldItem);
                if (entry != null) {
                    TileReceiverBaseInventory.ItemHandlerTile handle = tw.getInventoryHandler();
                    ItemStack slotStack = handle.getStackInSlot(0);
                    if (!(slotStack == null || slotStack.stackSize <= 0)) return false;

                    if (!worldIn.isAirBlock(x, y + 1, z)) {
                        return false;
                    }

                    handle.setStackInSlot(0, ItemUtils.copyStackWithSize(heldItem, 1));
                    // 1.7.10: Use playSoundEffect with sound string
                    worldIn.playSoundEffect(
                        x + 0.5,
                        y + 0.5,
                        z + 0.5,
                        "step.stone",
                        0.2F,
                        ((worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);

                    if (!playerIn.capabilities.isCreativeMode) {
                        heldItem.stackSize--;
                    }
                    if (heldItem.stackSize <= 0) {
                        playerIn.inventory.mainInventory[playerIn.inventory.currentItem] = null;
                    }
                }

                // 1.7.10: Capability system not available, fluid handling would need different implementation
                // Skipping FluidUtil code for now
                /*
                 * FluidActionResult far = FluidUtil.tryFillContainerAndStow(
                 * heldItem,
                 * tw.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.DOWN),
                 * new InvWrapper(playerIn.inventory),
                 * Fluid.BUCKET_VOLUME,
                 * playerIn,
                 * true);
                 * if (far.isSuccess()) {
                 * playerIn.inventory.mainInventory[playerIn.inventory.currentItem] = far.getResult();
                 * SoundHelper.playSoundAround(
                 * null,
                 * worldIn,
                 * pos,
                 * 1F,
                 * 1F);
                 * tw.markForUpdate();
                 * }
                 */
            }
        }
        return true;
    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block block, int meta) {
        TileWell tw = MiscUtils.getTileAt(worldIn, x, y, z, TileWell.class);
        if (tw != null && !worldIn.isRemote) {
            ItemStack stack = tw.getInventoryHandler()
                .getStackInSlot(0);
            if (!(stack == null || stack.stackSize <= 0)) {
                tw.breakCatalyst();
            }
        }

        super.breakBlock(worldIn, x, y, z, block, meta);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public void addCollisionBoxesToList(World worldIn, int x, int y, int z, AxisAlignedBB entityBox,
        List<AxisAlignedBB> collidingBoxes, Entity entityIn) {
        // Set each collision box and add it to the list
        for (AxisAlignedBB box : collisionBoxes) {
            this.setBlockBounds(
                (float) box.minX,
                (float) box.minY,
                (float) box.minZ,
                (float) box.maxX,
                (float) box.maxY,
                (float) box.maxZ);
            super.addCollisionBoxesToList(worldIn, x, y, z, entityBox, collidingBoxes, entityIn);
        }
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World worldIn, int x, int y, int z, int side) {
        TileWell tw = MiscUtils.getTileAt(worldIn, x, y, z, TileWell.class);
        if (tw != null) {
            return WrapMathHelper.ceil(tw.getPercFilled() * 15F);
        }
        return 0;
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        return boxWell;
    }

    @Override
    public int getRenderType() {
        return -1; // Custom model renderer
    }

    static {
        List<AxisAlignedBB> boxes = new LinkedList<>();

        boxes.add(AxisAlignedBB.getBoundingBox(1D / 16D, 0D, 1D / 16D, 15D / 16D, 5D / 16D, 15D / 16D));

        boxes.add(AxisAlignedBB.getBoundingBox(1D / 16D, 5D / 16D, 1D / 16D, 2D / 16D, 1D, 15D / 16D));
        boxes.add(AxisAlignedBB.getBoundingBox(1D / 16D, 5D / 16D, 1D / 16D, 15D / 16D, 1D, 2D / 16D));
        boxes.add(AxisAlignedBB.getBoundingBox(14D / 16D, 5D / 16D, 1D / 16D, 15D / 16D, 1D, 15D / 16D));
        boxes.add(AxisAlignedBB.getBoundingBox(1D / 16D, 5D / 16D, 14D / 16D, 15D / 16D, 1D, 15D / 16D));

        collisionBoxes = Collections.unmodifiableList(boxes);
    }

}
