/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Celestial Gateway Block - Interdimensional teleportation gateway
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.lib.CreativeTabsAS;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * Celestial Gateway Block
 * <p>
 * A gateway that allows interdimensional teleportation.
 * <p>
 * Features:
 * - Teleport players between dimensions
 * - Named gateways
 * - Placed by player tracking
 * - Part of gateway network (TODO)
 * - Uses TESR for OBJ model rendering
 * <p>
 * Rendering:
 * - Uses TESR to render OBJ model (celestial_gateway.obj)
 * - Model includes platform, pillars, and orbs
 * - Textures: platform.png, orb_inner.png, orb_outer.png
 * <p>
 * Based on 1.12.2 implementation which uses EnumBlockRenderType.MODEL
 * <p>
 * TODO:
 * - Implement TileCelestialGateway
 * - Implement teleportation logic
 * - Implement gateway network system
 * - Implement GatewayCache
 * - Implement structure validation
 * - Implement portal rendering
 * - Implement particle effects
 */
public class BlockCelestialGateway extends BlockContainer {

    @SideOnly(Side.CLIENT)
    private IIcon iconGateway;

    public BlockCelestialGateway() {
        super(Material.rock);

        setHardness(4.0F);
        setResistance(40.0F);
        setStepSound(soundTypeStone);
        setHarvestLevel("pickaxe", 2); // Iron tier
        setCreativeTab(CreativeTabsAS.ASTRAL_SORCERY_TAB);

        // Set block bounds (thin slab)
        // In 1.7.10: setBlockBounds(x1, y1, z1, x2, y2, z2)
        // Values are 0-16
        setBlockBounds(1.0F / 16.0F, 0.0F, 1.0F / 16.0F, 15.0F / 16.0F, 1.0F / 16.0F, 15.0F / 16.0F);
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        // For TESR blocks, we need a simple icon for the item in hand/inventory
        // Use the platform texture as the item icon
        iconGateway = register.registerIcon("astralsorcery:models/celestialgateway/platform");
        LogHelper.info("[BlockCelestialGateway] Registered icon: " + iconGateway.getIconName());
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return iconGateway;

    }

    public boolean isOpaqueCube() {
        return false; // Not a full block
    }

    public boolean renderAsNormalBlock() {
        return false; // Special rendering
    }

    public int getRenderType() {
        // Return -1 to use TESR rendering
        // In 1.7.10, -1 means "use TileEntitySpecialRenderer"
        return -1;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, x, y, z, placer, stack);

        // Initialize TileEntity with placement data
        hellfirepvp.astralsorcery.common.tile.TileCelestialGateway tile = (hellfirepvp.astralsorcery.common.tile.TileCelestialGateway) world
            .getTileEntity(x, y, z);

        if (tile != null) {
            // Set gateway name if item has display name
            if (stack.hasDisplayName()) {
                tile.setGatewayName(stack.getDisplayName());
            }

            // Set player who placed this gateway
            if (placer instanceof net.minecraft.entity.player.EntityPlayer) {
                net.minecraft.entity.player.EntityPlayer player = (net.minecraft.entity.player.EntityPlayer) placer;
                // In 1.7.10: getGameProfile().getId()
                tile.setPlacedBy(
                    player.getGameProfile()
                        .getId());
            }
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        super.breakBlock(world, x, y, z, block, meta);

        // TODO: Remove from GatewayCache when implemented
        // GatewayCache cache = WorldCacheManager.getOrLoadData(world, GatewayCache.class);
        // cache.removePosition(world, new BlockPos(x, y, z));
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new hellfirepvp.astralsorcery.common.tile.TileCelestialGateway();
    }

    @Override
    public boolean hasTileEntity(int meta) {
        return true;
    }
}
