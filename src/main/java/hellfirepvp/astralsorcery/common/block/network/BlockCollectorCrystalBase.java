/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.network;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.data.research.EnumGatedKnowledge;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.item.base.render.ISpecialStackDescriptor;
import hellfirepvp.astralsorcery.common.item.block.ItemCollectorCrystal;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalPropertyItem;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.structure.BlockStructureObserver;
import hellfirepvp.astralsorcery.common.tile.network.TileCollectorCrystal;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockCollectorCrystalBase
 * Created by HellFirePvP
 * Date: 15.09.2016 / 19:03
 */
public abstract class BlockCollectorCrystalBase extends BlockStarlightNetwork
    implements ISpecialStackDescriptor, CrystalPropertyItem, BlockStructureObserver {

    private static AxisAlignedBB boxCrystal = AxisAlignedBB.getBoundingBox(0.3, 0, 0.3, 0.7, 1, 0.7);

    public BlockCollectorCrystalBase(Material material) {
        super(material);
        setBlockUnbreakable();
        setResistance(200000F);
        setHarvestLevel("pickaxe", 2);
        setStepSound(Block.soundTypeGlass);
        setLightLevel(0.7F);
        setCreativeTab(RegistryItems.creativeTabAstralSorceryTunedCrystals);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer effectRenderer) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addHitEffects(World worldObj, MovingObjectPosition target, EffectRenderer effectRenderer) {
        Block state = worldObj.getBlock(target.blockX, target.blockY, target.blockZ);
        Color c = null;
        if (state instanceof BlockCelestialCollectorCrystal) {
            c = CollectorCrystalType.CELESTIAL_CRYSTAL.displayColor;
        }
        int x = target.blockX;
        int y = target.blockY;
        int z = target.blockZ;
        for (int i = 0; i < 1 + worldObj.rand.nextInt(2); i++) {
            AstralSorcery.proxy.fireLightning(
                worldObj,
                new Vector3(x + 0.5, y + 0.5, z + 0.5),
                new Vector3(x + 0.5, y + 0.5, z + 0.5).add(
                    -0.5 + worldObj.rand.nextFloat(),
                    -2 + worldObj.rand.nextFloat() * 4,
                    -0.5 + worldObj.rand.nextFloat()),
                c);
        }
        return true;
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return false;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return boxCrystal;
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        return boxCrystal;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        this.setBlockBounds(0.3F, 0F, 0.3F, 0.7F, 1F, 0.7F);
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip) {
        CrystalProperties prop = CrystalProperties.getCrystalProperties(stack);
        // 1.7.10 compatibility: Optional doesn't exist - use @Nullable Boolean instead
        Boolean missing = CrystalProperties.addPropertyTooltip(prop, tooltip, getMaxSize(stack));

        if (missing != null) {
            ProgressionTier tier = ResearchManager.clientProgress.getTierReached();
            IWeakConstellation c = ItemCollectorCrystal.getConstellation(stack);
            if (c != null) {
                if (EnumGatedKnowledge.COLLECTOR_TYPE.canSee(tier)
                    && ResearchManager.clientProgress.hasConstellationDiscovered(c.getUnlocalizedName())) {
                    tooltip.add(
                        EnumChatFormatting.GRAY + I18n.format(
                            "crystal.collect.type",
                            EnumChatFormatting.BLUE + I18n.format(c.getUnlocalizedName())));
                    IMinorConstellation tr = ItemCollectorCrystal.getTrait(stack);
                    if (tr != null) {
                        if (EnumGatedKnowledge.CRYSTAL_TRAIT.canSee(tier)
                            && ResearchManager.clientProgress.hasConstellationDiscovered(tr.getUnlocalizedName())) {
                            tooltip.add(
                                EnumChatFormatting.GRAY + I18n.format(
                                    "crystal.trait",
                                    EnumChatFormatting.BLUE + I18n.format(tr.getUnlocalizedName())));
                        } else {
                            tooltip.add(EnumChatFormatting.GRAY + I18n.format("progress.missing.knowledge"));
                        }
                    }
                } else if (!missing) {
                    // 1.7.10 compatibility: .get() removed - checking Boolean directly
                    tooltip.add(EnumChatFormatting.GRAY + I18n.format("progress.missing.knowledge"));
                }
            }
        }
    }

    @Override
    public int getMaxSize(ItemStack stack) {
        CollectorCrystalType type = ItemCollectorCrystal.getType(stack);
        if (type == CollectorCrystalType.CELESTIAL_CRYSTAL) {
            return CrystalProperties.MAX_SIZE_CELESTIAL;
        }
        return CrystalProperties.MAX_SIZE_ROCK;
    }

    @Nullable
    @Override
    public CrystalProperties provideCurrentPropertiesOrNull(ItemStack stack) {
        return CrystalProperties.getCrystalProperties(stack);
    }

    /*
     * @Override
     * public boolean onBlockActivated(World worldIn, BlockPos pos, Block state, EntityPlayer playerIn, EnumHand
     * hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
     * if(!worldIn.isRemote) {
     * TileCollectorCrystal te = MiscUtils.getTileAt(worldIn, x, y, z, TileCollectorCrystal.class);
     * if(te != null) {
     * playerIn.addChatMessage(new ChatComponentText("PlayerMade: " + te.isPlayerMade()));
     * playerIn.addChatMessage(new ChatComponentText("Constellation: " + te.getTransmittingType().getName()));
     * playerIn.addChatMessage(new ChatComponentText("Can charge: " + te.canCharge()));
     * playerIn.addChatMessage(new ChatComponentText("Charge: " + te.getCharge()));
     * }
     * }
     * return super.onBlockActivated(worldIn, x, y, z, playerIn, side, side, hitX, hitY, hitZ);
     * }
     */

    @Override
    public float getBlockHardness(World worldIn, int x, int y, int z) {
        TileCollectorCrystal te = MiscUtils.getTileAt(worldIn, x, y, z, TileCollectorCrystal.class);
        if (te != null) {
            if (te.isPlayerMade()) {
                return 4.0F;
            }
        }
        return super.getBlockHardness(worldIn, x, y, z);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        if (!(placer instanceof EntityPlayer)) return;
        TileCollectorCrystal te = MiscUtils.getTileAt(worldIn, x, y, z, TileCollectorCrystal.class);
        if (te == null) return;

        IWeakConstellation c = ItemCollectorCrystal.getConstellation(stack);
        if (c != null) {
            te.onPlace(
                c,
                ItemCollectorCrystal.getTrait(stack),
                CrystalProperties.getCrystalProperties(stack),
                placer.getUniqueID(),
                ItemCollectorCrystal.getType(stack));
        }
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        return new ArrayList<>();
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileCollectorCrystal();
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return null;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        TileCollectorCrystal te = MiscUtils.getTileAt(world, x, y, z, TileCollectorCrystal.class);
        if (te != null) {
            if (te.getCrystalProperties() == null || te.getConstellation() == null || te.getType() == null) {
                return null;
            }
            ItemStack stack = new ItemStack(this);
            CrystalProperties.applyCrystalProperties(stack, te.getCrystalProperties());
            ItemCollectorCrystal.setConstellation(stack, te.getConstellation());
            ItemCollectorCrystal.setTraitConstellation(stack, te.getTrait());
            ItemCollectorCrystal.setType(stack, te.getType());
            return stack;
        }
        return null;
    }

    @Override
    public String getUnlocalizedName() {
        PlayerProgress client = ResearchManager.clientProgress;
        if (EnumGatedKnowledge.COLLECTOR_CRYSTAL.canSee(client.getTierReached())) {
            return super.getUnlocalizedName();
        }
        return "tile.blockcollectorcrystal.obf";
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public int damageDropped(int metadata) {
        return metadata;
    }

    @Override
    public void onBlockHarvested(World worldIn, int x, int y, int z, int metadata, EntityPlayer player) {
        TileCollectorCrystal te = MiscUtils.getTileAt(worldIn, x, y, z, TileCollectorCrystal.class);
        if (te != null && !worldIn.isRemote) {
            PktParticleEvent event = new PktParticleEvent(PktParticleEvent.ParticleEventType.COLLECTOR_BURST, x, y, z);
            PacketChannel.CHANNEL
                .sendToAllAround(event, PacketChannel.pointFromPos(worldIn, new ChunkCoordinates(x, y, z), 32));
            TileCollectorCrystal.breakDamage(worldIn, new BlockPos(x, y, z));

            if (te.isPlayerMade() && !player.capabilities.isCreativeMode) {
                ItemStack drop = new ItemStack(
                    te.getType() == CollectorCrystalType.CELESTIAL_CRYSTAL ? BlocksAS.celestialCollectorCrystal
                        : BlocksAS.collectorCrystal);
                if (te.getCrystalProperties() != null && te.getConstellation() != null) {
                    CrystalProperties.applyCrystalProperties(drop, te.getCrystalProperties());
                    ItemCollectorCrystal
                        .setType(drop, te.getType() != null ? te.getType() : CollectorCrystalType.ROCK_CRYSTAL);
                    ItemCollectorCrystal.setConstellation(drop, te.getConstellation());
                    ItemCollectorCrystal.setTraitConstellation(drop, te.getTrait());
                    ItemUtils.dropItemNaturally(worldIn, x + 0.5, y + 0.5, z + 0.5, drop);
                }
            }
        }
        super.onBlockHarvested(worldIn, x, y, z, metadata, player);
    }

    public static enum CollectorCrystalType {

        ROCK_CRYSTAL(new Color(0xDD, 0xDD, 0xFF)),
        CELESTIAL_CRYSTAL(new Color(0x0, 0x88, 0xFF));

        public final Color displayColor;

        private CollectorCrystalType(Color c) {
            this.displayColor = c;
        }

    }

}
