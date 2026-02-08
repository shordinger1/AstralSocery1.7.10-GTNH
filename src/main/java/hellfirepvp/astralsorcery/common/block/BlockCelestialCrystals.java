/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Celestial Crystal Block - Growing celestial crystal formations
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.lib.CreativeTabsAS;

/**
 * Celestial Crystal Block
 * <p>
 * A block that grows celestial crystals from constellation light.
 * <p>
 * Features:
 * - 5 growth stages (metadata 0-4)
 * - Grows when exposed to starlight (TODO)
 * - Emits light based on stage
 * - Can be harvested for ItemCelestialCrystal
 * - Has TileEntity for growth tracking
 * <p>
 * Growth stages:
 * - STAGE_0 (0): Small crystal
 * - STAGE_1 (1): Medium crystal
 * - STAGE_2_SKY (2): Large crystal attuned to sky
 * - STAGE_2_DAY (3): Large crystal attuned to day
 * - STAGE_2_NIGHT (4): Large crystal attuned to night
 * <p>
 * TODO:
 * - Implement growth logic based on starlight exposure
 * - Implement constellation attunement requirements
 * - Implement light emission scaling
 * - Implement TileEntity with growth tracking
 * - Implement particle effects for growth
 * - Link with constellation discovery system
 */
public class BlockCelestialCrystals extends BlockContainer {

    /**
     * Growth stage enum
     */
    public enum GrowthStage {

        STAGE_0(0, "small", 0.3F, 3.0F), // Small crystal
        STAGE_1(1, "medium", 0.5F, 4.0F), // Medium crystal
        STAGE_2_SKY(2, "sky", 0.8F, 5.0F), // Sky attuned
        STAGE_2_DAY(3, "day", 0.8F, 5.0F), // Day attuned
        STAGE_2_NIGHT(4, "night", 0.8F, 5.0F); // Night attuned

        private final int metadata;
        private final String name;
        private final float lightLevel;
        private final float height;

        GrowthStage(int metadata, String name, float lightLevel, float height) {
            this.metadata = metadata;
            this.name = name;
            this.lightLevel = lightLevel;
            this.height = height;
        }

        public int getMetadata() {
            return metadata;
        }

        public String getName() {
            return name;
        }

        public float getLightLevel() {
            return lightLevel;
        }

        public float getHeight() {
            return height;
        }

        public static GrowthStage byMetadata(int meta) {
            for (GrowthStage stage : values()) {
                if (stage.metadata == meta) {
                    return stage;
                }
            }
            return STAGE_0;
        }
    }

    @SideOnly(Side.CLIENT)
    private IIcon iconCrystal;

    public BlockCelestialCrystals() {
        super(Material.glass);

        setHardness(2.0F);
        setResistance(10.0F);
        setStepSound(soundTypeGlass);
        setHarvestLevel("pickaxe", 1);
        setLightLevel(0.3F); // Base light level
        setCreativeTab(CreativeTabsAS.ASTRAL_SORCERY_TAB);

        // Enable metadata variants
        // NOTE: 1.7.10 doesn't have a direct way to set this in constructor
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        // Register all block textures
        hellfirepvp.astralsorcery.client.util.BlockTextureRegister.registerAll(register);
        // Store reference for stage 0 as default
        iconCrystal = hellfirepvp.astralsorcery.client.util.BlockTextureRegister.getCelestialCrystalStage(0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        // Return different icon based on growth stage (metadata)
        GrowthStage stage = GrowthStage.byMetadata(meta);
        return hellfirepvp.astralsorcery.client.util.BlockTextureRegister.getCelestialCrystalStage(stage.getMetadata());
    }

    public int damageDropped(int meta) {
        // Drop the same stage when broken
        // TODO: Different stages should drop different amounts of crystal
        return meta;

    }

    public Item getItemDropped(int meta, Random rand, int fortune) {
        // TODO: Return ItemCelestialCrystal when implemented
        // For now, return the item form of this block
        return null; // Will drop itself by default
    }

    public int quantityDropped(Random rand) {
        // TODO: Drop multiple crystals at higher stages
        return 1;
    }

    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        // Add all growth stages to creative tab
        for (GrowthStage stage : GrowthStage.values()) {
            list.add(new ItemStack(item, 1, stage.getMetadata()));
        }
    }

    public TileEntity createNewTileEntity(World world, int meta) {
        return new hellfirepvp.astralsorcery.common.tile.TileCelestialCrystals();
    }
    // ========== Collision ==========

    // getBlockHeight not available in 1.7.10
    // TODO: Implement collision box updates based on stage
    public float getBlockHeight(World world, int x, int y, int z) {
        // Height based on growth stage
        int meta = world.getBlockMetadata(x, y, z);
        GrowthStage stage = GrowthStage.byMetadata(meta);
        return stage.getHeight();
    }

    /**
     * Get collision box based on growth stage
     * STAGE_0: (0.3, 0, 0.3) to (0.7, 0.3, 0.7)
     * STAGE_1: (0.3, 0, 0.3) to (0.7, 0.5, 0.7)
     * STAGE_2_*: (0.25, 0, 0.25) to (0.75, height, 0.75)
     */
    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        GrowthStage stage = GrowthStage.byMetadata(meta);

        float height = stage.getHeight();

        // Base size and height depend on stage
        if (stage == GrowthStage.STAGE_0) {
            // Small crystal
            return AxisAlignedBB.getBoundingBox(0.3, 0.0, 0.3, 0.7, height, 0.7);
        } else if (stage == GrowthStage.STAGE_1) {
            // Medium crystal
            return AxisAlignedBB.getBoundingBox(0.3, 0.0, 0.3, 0.7, height, 0.7);
        } else {
            // Large crystal (stage 2 variants)
            return AxisAlignedBB.getBoundingBox(0.25, 0.0, 0.25, 0.75, height, 0.75);
        }
    }

    /**
     * Get selected bounding box (for block outline)
     */
    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        return getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    // ========== Light ==========

    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        // Light level based on growth stage
        int meta = world.getBlockMetadata(x, y, z);
        GrowthStage stage = GrowthStage.byMetadata(meta);
        return (int) (stage.getLightLevel() * 15.0F);

        // ========== Helper Methods ==========
    }

    /**
     * Get growth stage from metadata
     *
     * @param meta Block metadata
     * @return Growth stage
     */
    public static GrowthStage getStage(int meta) {
        return GrowthStage.byMetadata(meta);
    }

    /**
     * Check if crystal is fully grown
     *
     * @param meta Block metadata
     * @return true if stage >= STAGE_1
     */
    public static boolean isGrown(int meta) {
        GrowthStage stage = GrowthStage.byMetadata(meta);
        return stage == GrowthStage.STAGE_1 || stage == GrowthStage.STAGE_2_SKY
            || stage == GrowthStage.STAGE_2_DAY
            || stage == GrowthStage.STAGE_2_NIGHT;
    }

    /**
     * Check if crystal is attuned (stage 2)
     *
     * @param meta Block metadata
     * @return true if attuned to a constellation
     */
    public static boolean isAttuned(int meta) {
        GrowthStage stage = GrowthStage.byMetadata(meta);
        return stage == GrowthStage.STAGE_2_SKY || stage == GrowthStage.STAGE_2_DAY
            || stage == GrowthStage.STAGE_2_NIGHT;
    }
}
