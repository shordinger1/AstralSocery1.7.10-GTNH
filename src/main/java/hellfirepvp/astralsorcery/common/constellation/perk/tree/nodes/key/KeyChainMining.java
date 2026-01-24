/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.key;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.world.BlockEvent;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.KeyPerk;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.data.config.entry.ConfigEntry;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.structure.array.BlockArray;
import hellfirepvp.astralsorcery.common.util.BlockDropCaptureAssist;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.WrapMathHelper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.struct.BlockDiscoverer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyChainMining
 * Created by HellFirePvP
 * Date: 30.07.2018 / 01:09
 */
public class KeyChainMining extends KeyPerk {

    private float chainChance = 0.2F;
    private int chainLength = 4;

    private static boolean chainOngoing = false;

    public KeyChainMining(String name, int x, int y) {
        super(name, x, y);
        Config.addDynamicEntry(new ConfigEntry(ConfigEntry.Section.PERKS, name) {

            @Override
            public void loadFromConfig(Configuration cfg) {
                chainLength = cfg.getInt(
                    "ChainLength",
                    getConfigurationSection(),
                    chainLength,
                    2,
                    20,
                    "Defines the maximum length of a single break-chain.");
                chainChance = cfg.getFloat(
                    "ChainChance",
                    getConfigurationSection(),
                    chainChance,
                    0.01F,
                    1F,
                    "Defines the base chance a chain is tried to be built.");
            }
        });
    }

    @Override
    protected void applyEffectMultiplier(double multiplier) {
        super.applyEffectMultiplier(multiplier);

        this.chainChance *= multiplier;
        this.chainLength = WrapMathHelper.ceil(this.chainLength * multiplier);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onBreak(BlockEvent.BreakEvent event) {
        EntityPlayer player = event.getPlayer();
        Side side = player.worldObj.isRemote ? Side.CLIENT : Side.SERVER;
        PlayerProgress prog = ResearchManager.getProgress(player, side);
        if (side == Side.SERVER && player instanceof EntityPlayerMP
            && prog.hasPerkEffect(this)
            && !MiscUtils.isPlayerFakeMP((EntityPlayerMP) player)
            && !player.isSneaking()
            && event.world instanceof WorldServer
            && !player.capabilities.isCreativeMode) {
            if (chainOngoing) return;
            chainOngoing = true;
            try {
                WorldServer world = (WorldServer) event.world;
                BlockPos pos = new BlockPos(event.x, event.y, event.z);
                Block state = event.world.getBlock(event.x, event.y, event.z);
                if (doMiningChain(world, pos, state, player, side)) {
                    float doubleChance = PerkAttributeHelper.getOrCreateMap(player, side)
                        .getModifier(player, prog, AttributeTypeRegistry.ATTR_TYPE_MINING_CHAIN_SUCCESSIVECHAIN);
                    if (rand.nextFloat() < doubleChance) {
                        while (doMiningChain(world, pos, state, player, side)) {}
                    }
                }
            } finally {
                chainOngoing = false;
            }
        }
    }

    private boolean doMiningChain(WorldServer world, BlockPos pos, Block state, EntityPlayer player, Side side) {
        PlayerProgress prog = ResearchManager.getProgress(player, side);
        float ch = chainChance;
        ch = PerkAttributeHelper.getOrCreateMap(player, side)
            .modifyValue(player, prog, AttributeTypeRegistry.ATTR_TYPE_MINING_CHAIN_CHANCE, ch);
        if (rand.nextFloat() < ch) {
            float fLength = chainLength;
            fLength = PerkAttributeHelper.getOrCreateMap(player, side)
                .modifyValue(player, prog, AttributeTypeRegistry.ATTR_TYPE_MINING_CHAIN_LENGTH, fLength);
            BlockArray chain = BlockDiscoverer.discoverBlocksWithSameStateAroundChain(
                world,
                pos,
                state,
                Math.round(fLength),
                null,
                ((world1, pos1, state1, meta1) -> pos1.posY >= (int) player.posY
                    && state1.getBlockHardness(world1, pos1.getX(), pos1.getY(), pos1.getZ()) >= 0
                    && world1.getTileEntity(pos1.getX(), pos1.getY(), pos1.getZ()) == null
                    && !world1.isAirBlock(pos1.getX(), pos1.getY(), pos1.getZ())
                    && state1.canHarvestBlock(player, meta1)));
            if (!(chain == null || chain.getPattern()
                .size() <= 0)) {
                int broken = 0;
                FakePlayer fp = AstralSorcery.proxy.getASFakePlayerServer(world);
                for (BlockPos at : chain.getPattern()
                    .keySet()) {
                    Block atState = at.getBlock(world);
                    int meta = at.getBlockMetadata(world);
                    int exp;
                    try {
                        // 1.7.10: BreakEvent constructor signature
                        BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(
                            at.getX(),
                            at.getY(),
                            at.getZ(),
                            world,
                            atState,
                            meta,
                            fp);
                        MinecraftForge.EVENT_BUS.post(event);
                        exp = event.getExpToDrop();
                        if (event.isCanceled()) continue;
                    } catch (Exception exc) {
                        continue;
                    }
                    boolean capturing = false;
                    try {
                        BlockDropCaptureAssist.startCapturing();
                        capturing = true;

                        // 1.7.10: Use BlockPos.getTileEntity(World) method
                        TileEntity te = at.getTileEntity(world);
                        if (atState.removedByPlayer(world, player, at.getX(), at.getY(), at.getZ())) {
                            atState.onBlockDestroyedByPlayer(world, at.getX(), at.getY(), at.getZ(), meta);
                            atState.harvestBlock(world, player, at.getX(), at.getY(), at.getZ(), meta);
                            if (exp > 0) {
                                atState.dropXpOnBlockBreak(world, at.getX(), at.getY(), at.getZ(), exp);
                            }
                            PktParticleEvent ev = new PktParticleEvent(
                                PktParticleEvent.ParticleEventType.ARCHITECT_PLACE,
                                at);
                            // 1.7.10: Use block ID and metadata for state ID
                            long stateId = ((long) Block.getIdFromBlock(atState) << 4) | (meta & 15);
                            ev.setAdditionalDataLong(stateId);
                            PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, at, 16));
                            broken++;
                        }
                        List<ItemStack> drops = BlockDropCaptureAssist.getCapturedStacksAndStop();
                        capturing = false;
                        Vector3 plPos = Vector3.atEntityCenter(player);
                        for (ItemStack stack : drops) {
                            // 1.7.10: Use inventory.addItemStackToInventory
                            if (!player.inventory.addItemStackToInventory(stack)) {
                                ItemUtils.dropItemNaturally(
                                    player.worldObj,
                                    plPos.getX() + rand.nextFloat() - rand.nextFloat(),
                                    player.posY,
                                    plPos.getZ() + rand.nextFloat() - rand.nextFloat(),
                                    stack);
                            }
                        }
                    } catch (Exception ignored) {} finally {
                        if (capturing) {
                            BlockDropCaptureAssist.getCapturedStacksAndStop(); // Discard.
                        }
                    }
                }
                return broken >= chain.getPattern()
                    .size() / 2;
            }
        }
        return false;
    }

}
