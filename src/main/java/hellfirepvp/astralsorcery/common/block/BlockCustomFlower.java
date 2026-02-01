/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Custom flower block - glowing decorative flower
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.base.AstralBaseBlock;
import hellfirepvp.astralsorcery.common.lib.CreativeTabsAS;
import hellfirepvp.astralsorcery.common.util.IconHelper;

/**
 * BlockCustomFlower - Custom flower block (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Glow effect (light level 0.2)</li>
 * <li>Shearable with shears</li>
 * <li>Drops glowstone dust</li>
 * <li>Can only be placed on solid blocks</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Changes:</b>
 * <ul>
 * <li>No PropertyEnum - simplified to single variant</li>
 * <li>No BlockPos - uses integer coordinates</li>
 * <li>No NonNullList - uses ArrayList</li>
 * <li>No getDrops() - uses multiple interfaces</li>
 * <li>No BlockRenderLayer - uses render pass</li>
 * </ul>
 * <p>
 * <b>NOTE:</b> Original version had multiple flower types with PropertyEnum.
 * This version only implements GLOW_FLOWER for simplicity.
 */
public class BlockCustomFlower extends AstralBaseBlock {

    private static final AxisAlignedBB FLOWER_BOX = AxisAlignedBB.getBoundingBox(
        1.5D / 16D,
        0,
        1.5D / 16D, // 0.09375, 0, 0.09375
        14.5D / 16D,
        13D / 16D,
        14.5D / 16D // 0.90625, 0.8125, 0.90625
    );

    @SideOnly(Side.CLIENT)
    private IIcon iconFlower;

    /**
     * Constructor
     */
    public BlockCustomFlower() {
        super(Material.plants);

        // Set block properties
        setLightLevel(0.2F); // Glowing effect
        setStepSound(soundTypeGrass);
        setTickRandomly(true);
        setHardness(0.0F);
        setCreativeTab(CreativeTabsAS.ASTRAL_SORCERY_TAB);

        // Set collision box (no collision)
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return null; // No collision
    }

    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
    }

    public void setBlockBoundsForItemRender() {
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
    }

    public boolean isOpaqueCube() {
        return false;

    }

    public boolean renderAsNormalBlock() {
        return false;

    }

    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return canBlockStay(world, x, y, z);

    }

    /**
     * Check if the flower can stay at this position
     * Must be placed on a solid block
     */
    public boolean canBlockStay(World world, int x, int y, int z) {
        Block blockBelow = world.getBlock(x, y - 1, z);
        return blockBelow != null && blockBelow.isOpaqueCube()
            && blockBelow.getMaterial()
                .isSolid();
    }

    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
        super.onNeighborBlockChange(world, x, y, z, neighbor);

        if (!canBlockStay(world, x, y, z)) {
            this.dropBlockAsItem(world, x, y, z, this.getMeta(world, x, y, z), 0);
            world.setBlockToAir(x, y, z);
        }
    }

    public void updateTick(World world, int x, int y, int z, Random rand) {
        if (!canBlockStay(world, x, y, z)) {
            this.dropBlockAsItem(world, x, y, z, this.getMeta(world, x, y, z), 1);
            world.setBlockToAir(x, y, z);
        }
    }

    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<ItemStack>();

        // Base drop: 1 glowstone dust
        int count = 1;
        Random rand = new Random();
        // Fortune bonus: +0-2 per level
        for (int i = 0; i < fortune; i++) {
            count += rand.nextInt(3);
        }

        drops.add(new ItemStack(Items.glowstone_dust, count, 0));
        return drops;

    }

    public Item getItemDropped(int meta, Random rand, int fortune) {
        return Items.glowstone_dust;

    }

    public int quantityDropped(Random rand) {
        return 1 + rand.nextInt(3); // 1-3 glowstone dust
    }

    public int quantityDroppedWithBonus(int fortune, Random rand) {
        int count = 1;
        for (int i = 0; i < fortune; i++) {
            count += rand.nextInt(3);
        }
        return count;

    }

    public ArrayList<ItemStack> onSheared(Item item, IBlockAccess world, int x, int y, int z, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
        drops.add(new ItemStack(this, 1, 0));
        return drops;
    }

    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        // Allow shearing by right-clicking
        ItemStack heldItem = player.getCurrentEquippedItem();
        if (heldItem != null && heldItem.getItem() instanceof net.minecraftforge.common.IShearable) {
            if (!world.isRemote) {
                ArrayList<ItemStack> drops = onSheared(heldItem.getItem(), world, x, y, z, 0);
                for (ItemStack drop : drops) {
                    EntityItem entityItem = new EntityItem(world, x + 0.5, y + 0.5, z + 0.5, drop);
                    world.spawnEntityInWorld(entityItem);
                }
                world.setBlockToAir(x, y, z);
                heldItem.damageItem(1, player);
            }
            return true;
        }
        return false;

        // NOTE: Original version had PropertyEnum<FlowerType> with multiple flower types
        // In 1.7.10, this is simplified to a single GLOW_FLOWER type
        // To add more types in the future:
        // 1. Use metadata for type selection (0-15 available)
        // 2. Add getIcon() method with icon array
        // 3. Add getSubBlocks() for creative tab display
        // 4. Implement metadata-based drop logic

        // ========== Texture Registration ==========
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        iconFlower = IconHelper.registerBlockIconsFromConfig(reg, "blockcustomflower")[0];
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return iconFlower;
    }
}
