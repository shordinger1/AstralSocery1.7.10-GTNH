/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Base Block class for all AstralSorcery blocks
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.util.ASUtils;

/**
 * AstralBaseBlock - Base class for all AstralSorcery blocks
 * <p>
 * Provides common functionality and all overridable Block methods.
 * All AstralSorcery blocks should extend this class.
 * <p>
 * Registration is handled by RegistryBlocks, not by this class.
 *
 * @author HellFirePvP
 * @version 1.7.10
 */
public class AstralBaseBlock extends Block {

    // ========== Constructors ==========

    /**
     * Default constructor
     * 
     * @param material Block material
     */
    public AstralBaseBlock(Material material) {
        super(material);
    }

    public AstralBaseBlock(Material material, String name) {
        super(material);
        this.setBlockName(name);
    }

    /**
     * Constructor with hardness
     * 
     * @param material Block material
     * @param hardness Hardness value
     */
    public AstralBaseBlock(Material material, float hardness) {
        this(material);
        this.setHardness(hardness);
    }

    /**
     * Full configuration constructor
     * 
     * @param material   Block material
     * @param hardness   Hardness
     * @param resistance Blast resistance
     * @param sound      Step sound
     */
    public AstralBaseBlock(Material material, float hardness, float resistance, SoundType sound) {
        this(material);
        this.setHardness(hardness);
        this.setResistance(resistance);
        this.setStepSound(sound);
    }

    // ========== Interaction Methods ==========

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float subX,
        float subY, float subZ) {
        return false;
    }

    @Override
    public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
        super.onBlockClicked(world, x, y, z, player);
    }

    @Override
    public void onEntityWalking(World world, int x, int y, int z, Entity entity) {
        super.onEntityWalking(world, x, y, z, entity);
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        super.onEntityCollidedWithBlock(world, x, y, z, entity);
    }

    @Override
    public void onFallenUpon(World world, int x, int y, int z, Entity entity, float fallDistance) {
        super.onFallenUpon(world, x, y, z, entity, fallDistance);
    }

    // ========== Update and Tick ==========

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        super.updateTick(world, x, y, z, random);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
        super.randomDisplayTick(world, x, y, z, random);
    }

    @Override
    public int tickRate(World world) {
        return super.tickRate(world);
    }

    // ========== Placement and Destruction ==========

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float subX, float subY, float subZ, int meta) {
        return super.onBlockPlaced(world, x, y, z, side, subX, subY, subZ, meta);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack itemStack) {
        super.onBlockPlacedBy(world, x, y, z, placer, itemStack);
    }

    @Override
    public void onPostBlockPlaced(World world, int x, int y, int z, int meta) {
        super.onPostBlockPlaced(world, x, y, z, meta);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block blockBroken, int meta) {
        if (hasTileEntity(meta)) {
            world.removeTileEntity(x, y, z);
        }
        super.breakBlock(world, x, y, z, blockBroken, meta);
    }

    @Override
    public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int meta) {
        super.onBlockDestroyedByPlayer(world, x, y, z, meta);
    }

    @Override
    public void onBlockHarvested(World world, int x, int y, int z, int meta, EntityPlayer player) {
        super.onBlockHarvested(world, x, y, z, meta, player);
    }

    @Override
    public void onBlockPreDestroy(World world, int x, int y, int z, int meta) {
        super.onBlockPreDestroy(world, x, y, z, meta);
    }

    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
        return super.removedByPlayer(world, player, x, y, z, willHarvest);
    }

    // ========== Neighbor Changes ==========

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
        super.onNeighborBlockChange(world, x, y, z, neighbor);
    }

    @Override
    public void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ) {
        super.onNeighborChange(world, x, y, z, tileX, tileY, tileZ);
    }

    // ========== Drops ==========

    @Override
    public int quantityDropped(Random random) {
        return super.quantityDropped(random);
    }

    @Override
    public int quantityDropped(int meta, int fortune, Random random) {
        return super.quantityDropped(meta, fortune, random);
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return super.getItemDropped(meta, random, fortune);
    }

    @Override
    public int damageDropped(int meta) {
        return super.damageDropped(meta);
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        return super.getDrops(world, x, y, z, metadata, fortune);
    }

    @Override
    public boolean canSilkHarvest(World world, EntityPlayer player, int x, int y, int z, int metadata) {
        return super.canSilkHarvest(world, player, x, y, z, metadata);
    }

    @Override
    public int getExpDrop(IBlockAccess world, int metadata, int fortune) {
        return super.getExpDrop(world, metadata, fortune);
    }

    // ========== Collision and Selection ==========

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB mask, List<AxisAlignedBB> list,
        Entity collider) {
        super.addCollisionBoxesToList(world, x, y, z, mask, list, collider);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        super.setBlockBoundsBasedOnState(world, x, y, z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        return super.getSelectedBoundingBoxFromPool(world, x, y, z);
    }

    // ========== Rendering ==========

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return super.getIcon(side, meta);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        return super.getIcon(world, x, y, z, side);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister reg) {
        // Register icon using: "astralsorcery:textures/blocks/[textureName].png"
        // textureName is derived from block unlocalizedName
        this.blockIcon = reg.registerIcon(this.getTextureName());
    }

    @Override
    public int getRenderType() {
        return super.getRenderType();
    }

    @Override
    public boolean renderAsNormalBlock() {
        return super.renderAsNormalBlock();
    }

    @Override
    public boolean isOpaqueCube() {
        return super.isOpaqueCube();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
        return super.shouldSideBeRendered(world, x, y, z, side);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderBlockPass() {
        return super.getRenderBlockPass();
    }

    @Override
    public boolean canRenderInPass(int pass) {
        return super.canRenderInPass(pass);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getAmbientOcclusionLightValue() {
        return super.getAmbientOcclusionLightValue();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getBlockColor() {
        return super.getBlockColor();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderColor(int meta) {
        return super.getRenderColor(meta);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
        return super.colorMultiplier(world, x, y, z);
    }

    // ========== Particles ==========

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addHitEffects(World world, MovingObjectPosition target, EffectRenderer effectRenderer) {
        return super.addHitEffects(world, target, effectRenderer);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer effectRenderer) {
        return super.addDestroyEffects(world, x, y, z, meta, effectRenderer);
    }

    // ========== Redstone ==========

    @Override
    public boolean canProvidePower() {
        return super.canProvidePower();
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side) {
        return super.isProvidingWeakPower(world, x, y, z, side);
    }

    @Override
    public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int side) {
        return super.isProvidingStrongPower(world, x, y, z, side);
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side) {
        return super.canConnectRedstone(world, x, y, z, side);
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return super.hasComparatorInputOverride();
    }

    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int side) {
        return super.getComparatorInputOverride(world, x, y, z, side);
    }

    @Override
    public boolean shouldCheckWeakPower(IBlockAccess world, int x, int y, int z, int side) {
        return super.shouldCheckWeakPower(world, x, y, z, side);
    }

    @Override
    public boolean getWeakChanges(IBlockAccess world, int x, int y, int z) {
        return super.getWeakChanges(world, x, y, z);
    }

    // ========== TileEntity ==========

    @Override
    public boolean hasTileEntity(int metadata) {
        return super.hasTileEntity(metadata);
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return super.createTileEntity(world, metadata);
    }

    // ========== Plants ==========

    @Override
    public boolean canSustainPlant(IBlockAccess world, int x, int y, int z, ForgeDirection direction,
        IPlantable plantable) {
        return super.canSustainPlant(world, x, y, z, direction, plantable);
    }

    @Override
    public void onPlantGrow(World world, int x, int y, int z, int sourceX, int sourceY, int sourceZ) {
        super.onPlantGrow(world, x, y, z, sourceX, sourceY, sourceZ);
    }

    @Override
    public boolean isFertile(World world, int x, int y, int z) {
        return super.isFertile(world, x, y, z);
    }

    @Override
    public boolean isWood(IBlockAccess world, int x, int y, int z) {
        return super.isWood(world, x, y, z);
    }

    @Override
    public boolean isLeaves(IBlockAccess world, int x, int y, int z) {
        return super.isLeaves(world, x, y, z);
    }

    @Override
    public void beginLeavesDecay(World world, int x, int y, int z) {
        super.beginLeavesDecay(world, x, y, z);
    }

    @Override
    public boolean canSustainLeaves(IBlockAccess world, int x, int y, int z) {
        return super.canSustainLeaves(world, x, y, z);
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockAccess world, int x, int y, int z) {
        return super.canBeReplacedByLeaves(world, x, y, z);
    }

    // ========== Tools and Harvesting ==========

    public String getHarvestTool(int meta) {
        return null;
    }

    public int getHarvestLevel(int meta) {
        return -1;
    }

    @Override
    public boolean canHarvestBlock(EntityPlayer player, int meta) {
        return super.canHarvestBlock(player, meta);
    }

    // ========== Placement ==========

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return super.canPlaceBlockAt(world, x, y, z);
    }

    @Override
    public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int side) {
        return super.canPlaceBlockOnSide(world, x, y, z, side);
    }

    @Override
    public boolean canReplace(World world, int x, int y, int z, int side, ItemStack itemStack) {
        return super.canReplace(world, x, y, z, side, itemStack);
    }

    @Override
    public boolean canBlockStay(World world, int x, int y, int z) {
        return super.canBlockStay(world, x, y, z);
    }

    @Override
    public boolean canPlaceTorchOnTop(World world, int x, int y, int z) {
        return super.canPlaceTorchOnTop(world, x, y, z);
    }

    // ========== Entities ==========

    @Override
    public boolean isLadder(IBlockAccess world, int x, int y, int z, EntityLivingBase entity) {
        return super.isLadder(world, x, y, z, entity);
    }

    @Override
    public boolean isBed(IBlockAccess world, int x, int y, int z, EntityLivingBase player) {
        return super.isBed(world, x, y, z, player);
    }

    @Override
    public ChunkCoordinates getBedSpawnPosition(IBlockAccess world, int x, int y, int z, EntityPlayer player) {
        return super.getBedSpawnPosition(world, x, y, z, player);
    }

    @Override
    public void setBedOccupied(IBlockAccess world, int x, int y, int z, EntityPlayer player, boolean occupied) {
        super.setBedOccupied(world, x, y, z, player, occupied);
    }

    @Override
    public int getBedDirection(IBlockAccess world, int x, int y, int z) {
        return super.getBedDirection(world, x, y, z);
    }

    @Override
    public boolean isBedFoot(IBlockAccess world, int x, int y, int z) {
        return super.isBedFoot(world, x, y, z);
    }

    @Override
    public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z) {
        return super.canCreatureSpawn(type, world, x, y, z);
    }

    // ========== Explosions ==========

    @Override
    public float getExplosionResistance(Entity exploder) {
        return super.getExplosionResistance(exploder);
    }

    @Override
    public float getExplosionResistance(Entity exploder, World world, int x, int y, int z, double explosionX,
        double explosionY, double explosionZ) {
        return super.getExplosionResistance(exploder, world, x, y, z, explosionX, explosionY, explosionZ);
    }

    @Override
    public void onBlockDestroyedByExplosion(World world, int x, int y, int z, Explosion explosion) {
        super.onBlockDestroyedByExplosion(world, x, y, z, explosion);
    }

    @Override
    public void onBlockExploded(World world, int x, int y, int z, Explosion explosion) {
        super.onBlockExploded(world, x, y, z, explosion);
    }

    @Override
    public boolean canDropFromExplosion(Explosion explosion) {
        return super.canDropFromExplosion(explosion);
    }

    // ========== Other Forge Extensions ==========

    @Override
    public boolean isBeaconBase(IBlockAccess world, int x, int y, int z, int beaconX, int beaconY, int beaconZ) {
        return super.isBeaconBase(world, x, y, z, beaconX, beaconY, beaconZ);
    }

    @Override
    public float getEnchantPowerBonus(World world, int x, int y, int z) {
        return super.getEnchantPowerBonus(world, x, y, z);
    }

    @Override
    public boolean isFireSource(World world, int x, int y, int z, ForgeDirection side) {
        return super.isFireSource(world, x, y, z, side);
    }

    @Override
    public boolean isFlammable(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        return super.isFlammable(world, x, y, z, face);
    }

    @Override
    public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        return super.getFlammability(world, x, y, z, face);
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        return super.getFireSpreadSpeed(world, x, y, z, face);
    }

    @Override
    public boolean isBurning(IBlockAccess world, int x, int y, int z) {
        return super.isBurning(world, x, y, z);
    }

    @Override
    public boolean isAir(IBlockAccess world, int x, int y, int z) {
        return super.isAir(world, x, y, z);
    }

    @Override
    public boolean isReplaceable(IBlockAccess world, int x, int y, int z) {
        return super.isReplaceable(world, x, y, z);
    }

    @Override
    public boolean isFoliage(IBlockAccess world, int x, int y, int z) {
        return super.isFoliage(world, x, y, z);
    }

    @Override
    public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity) {
        return super.canEntityDestroy(world, x, y, z, entity);
    }

    @Override
    public boolean isReplaceableOreGen(World world, int x, int y, int z, Block target) {
        return super.isReplaceableOreGen(world, x, y, z, target);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isFlowerPot() {
        return super.isFlowerPot();
    }

    @Override
    public boolean isNormalCube(IBlockAccess world, int x, int y, int z) {
        return super.isNormalCube(world, x, y, z);
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return super.isSideSolid(world, x, y, z, side);
    }

    @Override
    public void velocityToAddToEntity(World world, int x, int y, int z, Entity entity, Vec3 velocity) {
        super.velocityToAddToEntity(world, x, y, z, entity, velocity);
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player) {
        return super.getPickBlock(target, world, x, y, z, player);
    }

    @Override
    public boolean recolourBlock(World world, int x, int y, int z, ForgeDirection side, int colour) {
        return super.recolourBlock(world, x, y, z, side, colour);
    }

    @Override
    public boolean rotateBlock(World world, int x, int y, int z, ForgeDirection axis) {
        return super.rotateBlock(world, x, y, z, axis);
    }

    @Override
    public ForgeDirection[] getValidRotations(World world, int x, int y, int z) {
        return super.getValidRotations(world, x, y, z);
    }

    // ========== Other Methods ==========

    @Override
    @SideOnly(Side.CLIENT)
    public Item getItem(World world, int x, int y, int z) {
        return super.getItem(world, x, y, z);
    }

    @Override
    public int getDamageValue(World world, int x, int y, int z) {
        return super.getDamageValue(world, x, y, z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        super.getSubBlocks(item, tab, list);
    }

    @Override
    public int getMobilityFlag() {
        return super.getMobilityFlag();
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int meta) {
        super.harvestBlock(world, player, x, y, z, meta);
    }

    @Override
    public boolean onBlockEventReceived(World world, int x, int y, int z, int eventId, int eventData) {
        return super.onBlockEventReceived(world, x, y, z, eventId, eventData);
    }

    public void setUnlocalizedName(String name) {
        setBlockName(name);
    }

    @Override
    public void fillWithRain(World world, int x, int y, int z) {
        super.fillWithRain(world, x, y, z);
    }

    @Override
    public boolean isAssociatedBlock(Block other) {
        return super.isAssociatedBlock(other);
    }

    // ========== Helper Methods ==========

    /**
     * Check if world is client side
     * 
     * @param world World instance
     * @return true if client
     */
    protected boolean isClient(World world) {
        return world.isRemote;
    }

    /**
     * Check if world is server side
     * 
     * @param world World instance
     * @return true if server
     */
    protected boolean isServer(World world) {
        return !world.isRemote;
    }

    /**
     * Check if block at position matches target
     * 
     * @param world World access
     * @param x,    y, z Position
     * @param block Target block
     * @return true if matches
     */
    protected boolean isBlock(IBlockAccess world, int x, int y, int z, Block block) {
        return world.getBlock(x, y, z) == block;
    }

    /**
     * Check if block at position can be replaced
     * 
     * @param world World access
     * @param x,    y, z Position
     * @return true if replaceable
     */
    protected boolean canReplace(IBlockAccess world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        return block != null && block.isReplaceable(world, x, y, z);
    }

    /**
     * Notify block update
     * 
     * @param world World instance
     * @param x,    y, z Position
     */
    protected void notifyBlockUpdate(World world, int x, int y, int z) {
        world.markBlockForUpdate(x, y, z);
    }

    /**
     * Play sound at position
     * 
     * @param world     World instance
     * @param x,        y, z Position
     * @param soundName Sound name
     * @param volume    Volume
     * @param pitch     Pitch
     */
    protected void playSound(World world, int x, int y, int z, String soundName, float volume, float pitch) {
        world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, soundName, volume, pitch);
    }

    /**
     * Get block metadata
     * 
     * @param world World access
     * @param x,    y, z Position
     * @return Metadata value
     */
    protected int getMeta(IBlockAccess world, int x, int y, int z) {
        return world.getBlockMetadata(x, y, z);
    }

    /**
     * Set block metadata
     *
     * @param world World instance
     * @param x,    y, z Position
     * @param meta  Metadata value
     * @param flags Notify flags
     */
    protected void setMeta(World world, int x, int y, int z, int meta, int flags) {
        world.setBlockMetadataWithNotify(x, y, z, meta, flags);
    }

    // ========================================================================
    // Localization Helper Methods (Refactored for TST-style approach)
    // ========================================================================

    /**
     * Get localized text for this block.
     * <p>
     * Uses the StatCollector system for runtime localization.
     * Language files are loaded from assets/astralsorcery/lang/
     * <p>
     * <b>Usage:</b>
     *
     * <pre>
     * // In block class:
     * public class MyBlock extends AstralBaseBlock {
     * 
     *     public MyBlock() {
     *         super(Material.rock);
     *         this.setBlockName("astralsorcery.myBlock");
     *     }
     *
     *     // Get localized info
     *     public String getInfo() {
     *         return getLocal("astralsorcery.myBlock.info");
     *     }
     * }
     * </pre>
     *
     * @param key the localization key
     * @return the localized text
     * @see StatCollector#translateToLocal(String)
     */
    protected String getLocal(String key) {
        return ASUtils.tr(key);
    }

    /**
     * Get localized text with formatting.
     *
     * @param key  the localization key
     * @param args format arguments
     * @return the localized and formatted text
     * @see StatCollector#translateToLocalFormatted(String, Object...)
     */
    protected String getLocalFormatted(String key, Object... args) {
        return ASUtils.tr(key, args);
    }

    /**
     * Check if localization key exists.
     *
     * @param key the localization key
     * @return true if the key has a translation
     */
    protected boolean hasLocal(String key) {
        return ASUtils.canTranslate(key);
    }

    /**
     * Get localized tooltip for this block (from its item form).
     * <p>
     * Helper method to get tooltip lines from localization.
     * Tooltip keys should be in format: {@code {unlocalizedName}.tooltip.{lineNumber}}
     * <p>
     * Example:
     * 
     * <pre>
     * // Language file:
     * tile.astralsorcery.myBlock.name=My Block
     * tile.astralsorcery.myBlock.tooltip.1=Line 1 of tooltip
     * tile.astralsorcery.myBlock.tooltip.2=Line 2 of tooltip
     *
     * // In ItemBlock class:
     * public void addInformation(...) {
     *     addTooltips(tooltip, 2); // Adds tooltip.1, tooltip.2
     * }
     * </pre>
     *
     * @param tooltip   the tooltip list to add to
     * @param lineCount number of tooltip lines to add
     */
    @SideOnly(Side.CLIENT)
    protected void addTooltips(List<String> tooltip, int lineCount) {
        addTooltips(tooltip, lineCount, false);
    }

    /**
     * Get localized tooltip for this block (from its item form).
     *
     * @param tooltip       the tooltip list to add to
     * @param lineCount     number of tooltip lines to add
     * @param showShiftOnly true if tooltips only show when holding shift
     */
    @SideOnly(Side.CLIENT)
    protected void addTooltips(List<String> tooltip, int lineCount, boolean showShiftOnly) {
        // Check shift requirement
        if (showShiftOnly && !isShiftKeyDown()) {
            tooltip.add(getLocal("misc.moreInformation"));
            return;
        }

        // Get base unlocalizedName without "tile." prefix
        String baseKey = this.getUnlocalizedName();
        if (baseKey.startsWith("tile.")) {
            baseKey = baseKey.substring(5); // Remove "tile."
        }

        // Add each tooltip line
        for (int i = 1; i <= lineCount; i++) {
            String key = baseKey + ".tooltip." + i;
            String localized = getLocal(key);
            // Only add if the key exists (not equal to the key itself)
            if (!localized.equals(key)) {
                tooltip.add(localized);
            }
        }
    }

    /**
     * Check if player is holding shift key (client-side).
     *
     * @return true if shift is pressed
     */
    @SideOnly(Side.CLIENT)
    protected boolean isShiftKeyDown() {
        return net.minecraft.client.gui.GuiScreen.isShiftKeyDown();
    }
}
