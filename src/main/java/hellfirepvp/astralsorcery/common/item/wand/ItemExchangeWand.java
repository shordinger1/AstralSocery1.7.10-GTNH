/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.wand;

import java.util.*;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.renderer.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.event.ClientRenderEventHandler;
import hellfirepvp.astralsorcery.client.util.AirBlockRenderWorld;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.common.base.Mods;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.integrations.ModIntegrationBotania;
import hellfirepvp.astralsorcery.common.item.ItemBlockStorage;
import hellfirepvp.astralsorcery.common.item.base.render.ItemAlignmentChargeConsumer;
import hellfirepvp.astralsorcery.common.item.base.render.ItemHandRender;
import hellfirepvp.astralsorcery.common.item.base.render.ItemHudRender;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.structure.array.BlockArray;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import hellfirepvp.astralsorcery.common.util.struct.BlockDiscoverer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemExchangeWand
 * Created by HellFirePvP
 * Date: 07.02.2017 / 01:03
 */
public class ItemExchangeWand extends ItemBlockStorage
    implements ItemHandRender, ItemHudRender, ItemAlignmentChargeConsumer {

    private static final int searchDepth = 5;

    public ItemExchangeWand() {
        setMaxDamage(0);
        setMaxStackSize(1);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldReveal(ChargeType ct, ItemStack stack) {
        return ct == ChargeType.TEMP;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, Block state) {
        return 0;
    }

    @Override
    public boolean canHarvestBlock(Block blockIn) {
        return true;
    }

    @Override
    public boolean canHarvestBlock(Block state, ItemStack stack) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onRenderInHandHUD(ItemStack lastCacheInstance, float fadeAlpha, float pTicks) {
        Collection<ItemStack> stored = getMappedStoredStates(lastCacheInstance).values();
        if ((stored == null || stored.isEmpty())) return;

        Map<ItemStack, Integer> amountMap = new LinkedHashMap<>();
        for (ItemStack stack : stored) {
            int found = 0;
            if (Mods.BOTANIA.isPresent()) {
                found = ModIntegrationBotania.getItemCount(
                    Minecraft.getMinecraft().thePlayer,
                    lastCacheInstance,
                    ItemUtils.createBlockState(stack));
            } else {
                Collection<ItemStack> stacks = ItemUtils.scanInventoryForMatching(
                    new InvWrapper(Minecraft.getMinecraft().thePlayer.inventory),
                    stack,
                    false);
                for (ItemStack foundStack : stacks) {
                    found += foundStack.stackSize;
                }
            }
            amountMap.put(stack, found);
        }

        int heightNormal = 26;
        int heightSplit = 13;
        int width = 26;
        int offsetX = 30;
        int offsetY = 15;

        Blending.DEFAULT.applyStateManager();
        Blending.DEFAULT.apply();
        GL11.glColor4f(1F, 1F, 1F, fadeAlpha * 0.9F);
        Tessellator tes = Tessellator.instance;

        int tempOffsetY = offsetY;
        for (int i = 0; i < amountMap.size(); i++) {
            boolean first = i == 0;
            boolean last = (i + 1 == amountMap.size());
            if (first) {
                tes.startDrawing(GL11.GL_QUADS);
                ClientRenderEventHandler.texHUDItemFrame.bind();
                tes.addVertexWithUV(offsetX, tempOffsetY + heightSplit, 10, 0, 0.5);
                tes.addVertexWithUV(offsetX + width, tempOffsetY + heightSplit, 10, 1, 0.5);
                tes.addVertexWithUV(offsetX + width, tempOffsetY, 10, 1, 0);
                tes.addVertexWithUV(offsetX, tempOffsetY, 10, 0, 0);
                tempOffsetY += heightSplit;
                tes.draw();
            } else {
                tes.startDrawing(GL11.GL_QUADS);
                ClientRenderEventHandler.texHUDItemFrameEx.bind();
                tes.addVertexWithUV(offsetX, tempOffsetY + heightNormal, 10, 0, 1);
                tes.addVertexWithUV(offsetX + width, tempOffsetY + heightNormal, 10, 1, 1);
                tes.addVertexWithUV(offsetX + width, tempOffsetY, 10, 1, 0);
                tes.addVertexWithUV(offsetX, tempOffsetY, 10, 0, 0);
                tempOffsetY += heightNormal;
                tes.draw();
            }
            if (last) {
                tes.startDrawing(GL11.GL_QUADS);
                ClientRenderEventHandler.texHUDItemFrame.bind();
                tes.addVertexWithUV(offsetX, tempOffsetY + heightSplit, 10, 0, 1);
                tes.addVertexWithUV(offsetX + width, tempOffsetY + heightSplit, 10, 1, 1);
                tes.addVertexWithUV(offsetX + width, tempOffsetY, 10, 1, 0.5);
                tes.addVertexWithUV(offsetX, tempOffsetY, 10, 0, 0.5);
                tempOffsetY += heightSplit;
                tes.draw();
            }
        }

        TextureHelper.refreshTextureBindState();
        TextureHelper.setActiveTextureToAtlasSprite();
        RenderHelper.enableGUIStandardItemLighting();
        RenderItem ri = Minecraft.getMinecraft()
            .getRenderItem();

        tempOffsetY = offsetY;
        for (Map.Entry<ItemStack, Integer> entry : amountMap.entrySet()) {
            ri.renderItemAndEffectIntoGUI(
                Minecraft.getMinecraft().thePlayer,
                entry.getKey(),
                offsetX + 5,
                tempOffsetY + 5);
            tempOffsetY += heightNormal;
        }

        RenderHelper.disableStandardItemLighting();

        int c = 0x00DDDDDD;
        for (Map.Entry<ItemStack, Integer> entry : amountMap.entrySet()) {
            String amountStr = String.valueOf(entry.getValue());
            if (entry.getValue() == -1) {
                amountStr = "\u221E";
            }
            if (amountStr.length() > 3) {}
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(amountStr, 0, 0, c);

        }
        TextureHelper.refreshTextureBindState();

    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onRenderWhileInHand(ItemStack stack, float pTicks) {
        Map<Block, ItemStack> storedStates = getMappedStoredStates(stack);
        if (storedStates.isEmpty()) return;
        World world = Minecraft.getMinecraft().theWorld;
        Random r = getPreviewRandomFromWorld(world);

        EntityPlayer pl = Minecraft.getMinecraft().thePlayer;
        PlayerControllerMP ctrl = Minecraft.getMinecraft().thePlayerController;
        if (ctrl == null || pl == null) return;
        RayTraceResult rtr = getLookBlock(pl, false, true, ctrl.getBlockReachDistance());
        if (rtr == null || rtr.typeOfHit != hellfirepvp.astralsorcery.common.migration.RayTraceResult.Type.BLOCK)
            return;

        IBlockAccess airWorld = new AirBlockRenderWorld(
            net.minecraft.world.biome.BiomeGenBase.plains,
            world.getWorldType());
        BlockPos origin = rtr.getBlockPos();
        Block atOrigin = world.getBlock(origin);
        Block match = MiscUtils.getMatchingState(storedStates.keySet(), atOrigin);
        if (match != null && storedStates.keySet()
            .size() <= 1) {
            storedStates.remove(match);
        }
        if (storedStates.isEmpty()) {
            return;
        }
        float hardness = atOrigin.getBlockHardness(world, origin);
        if (Config.exchangeWandMaxHardness != -1) {
            if (hardness > Config.exchangeWandMaxHardness) {
                return;
            }
        }
        if (hardness == -1) {
            return;
        }

        int total = 0;
        Map<Block, Tuple<ItemStack, Integer>> amountMap = new LinkedHashMap<>();
        for (Map.Entry<Block, ItemStack> entry : storedStates.entrySet()) {
            int found = 0;
            if (Mods.BOTANIA.isPresent()) {
                found = ModIntegrationBotania.getItemCount(
                    Minecraft.getMinecraft().thePlayer,
                    stack,
                    ItemUtils.createBlockState(entry.getValue()));
            } else {
                Collection<ItemStack> stacks = ItemUtils.scanInventoryForMatching(
                    new InvWrapper(Minecraft.getMinecraft().thePlayer.inventory),
                    entry.getValue(),
                    false);
                for (ItemStack foundStack : stacks) {
                    found += foundStack.stackSize;
                }
            }
            total += (found == -1 ? 500_000 : found); // 500k should be large enough.
            amountMap.put(entry.getKey(), new Tuple<>(entry.getValue(), found));
        }

        Map<Block, Integer> amtMap = MiscUtils.remap(amountMap, tpl -> tpl.value);
        if (pl.capabilities.isCreativeMode) {
            for (Block state : amtMap.keySet()) {
                amtMap.put(state, Integer.MAX_VALUE);
            }
            total = Integer.MAX_VALUE;
        }
        BlockArray found = BlockDiscoverer
            .discoverBlocksWithSameStateAround(world, origin, true, searchDepth, total, false);
        if (found.isEmpty()) return;

        List<Block> applicableStates = Lists.newArrayList(storedStates.keySet());

        Blending.ADDITIVEDARK.applyStateManager();
        Blending.ADDITIVEDARK.apply();
        RenderingUtils.removeStandartTranslationFromTESRMatrix(pTicks);
        TextureHelper.setActiveTextureToAtlasSprite();

        Tessellator tes = Tessellator.instance;
        tes.startDrawing(GL11.GL_QUADS);
        for (BlockPos pos : found.getPattern()
            .keySet()) {
            Collections.shuffle(applicableStates, r);
            Block potentialState = Iterables.getFirst(applicableStates, Blocks.air); // 1.7.10: lowercase 'air'
            // 1.7.10: No getStateForPlacement in Block, use block directly
            RenderingUtils.renderBlockSafely(airWorld, pos, potentialState, tes);
        }
        tes.draw();
        TextureHelper.refreshTextureBindState();

        Blending.DEFAULT.apply();
        Blending.DEFAULT.applyStateManager();
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;
        if ((stack == null || stack.stackSize <= 0)) return true;

        BlockPos origin = new BlockPos(x, y, z);

        if (playerIn.isSneaking()) {
            tryStoreBlock(stack, world, origin);
            return true;
        }

        Map<Block, ItemStack> storedStates = getMappedStoredStates(stack);
        Block atOrigin = world.getBlock(x, y, z);
        Block match = MiscUtils.getMatchingState(storedStates.keySet(), atOrigin);
        if (match != null && storedStates.keySet()
            .size() <= 1) {
            storedStates.remove(match);
        }
        if (storedStates.isEmpty()) return true;

        float hardness = atOrigin.getBlockHardness(world, x, y, z);
        if (Config.exchangeWandMaxHardness != -1) {
            if (hardness > Config.exchangeWandMaxHardness) {
                return true;
            }
        }
        if (hardness == -1) {
            return true;
        }

        int total = 0;
        Map<Block, Tuple<ItemStack, Integer>> amountMap = new LinkedHashMap<>();
        for (Map.Entry<Block, ItemStack> entry : storedStates.entrySet()) {
            int found = 0;
            if (Mods.BOTANIA.isPresent()) {
                found = ModIntegrationBotania
                    .getItemCount(playerIn, stack, ItemUtils.createBlockState(entry.getValue()));
            } else {
                Collection<ItemStack> stacks = ItemUtils
                    .scanInventoryForMatching(new InvWrapper(playerIn.inventory), entry.getValue(), false);
                for (ItemStack foundStack : stacks) {
                    found += foundStack.stackSize;
                }
            }
            total += (found == -1 ? 500_000 : found); // 500k should be large enough.
            amountMap.put(entry.getKey(), new Tuple<>(entry.getValue(), found));
        }

        Map<Block, Integer> amtMap = MiscUtils.remap(amountMap, tpl -> tpl.value);
        if (playerIn.capabilities.isCreativeMode) {
            for (Block state : amtMap.keySet()) {
                amtMap.put(state, Integer.MAX_VALUE);
            }
            total = Integer.MAX_VALUE;
        }
        BlockArray found = BlockDiscoverer
            .discoverBlocksWithSameStateAround(world, origin, true, searchDepth, total, false);
        if (found.isEmpty()) return true;

        List<Tuple<Block, ItemStack>> shuffleable = MiscUtils
            .flatten(storedStates, (block, stack) -> new Tuple<>(block, stack));
        Random r = getPreviewRandomFromWorld(world);
        for (BlockPos placePos : found.getPattern()
            .keySet()) {
            Collections.shuffle(shuffleable, r);
            Tuple<Block, ItemStack> applicable = playerIn.capabilities.isCreativeMode
                ? Iterables.getFirst(shuffleable, null)
                : null;
            if (!playerIn.capabilities.isCreativeMode) {
                for (Tuple<Block, ItemStack> it : shuffleable) {
                    ItemStack test = ItemUtils.copyStackWithSize(it.value, 1);
                    if (ItemUtils.consumeFromPlayerInventory(playerIn, stack, test, true)) {
                        applicable = it;
                        break;
                    }
                }
            }
            if (applicable == null) break; // No more blocks. LUL

            if (drainTempCharge(playerIn, Config.exchangeWandUseCost, true)) {
                if (((EntityPlayerMP) playerIn).interactionManager
                    .tryHarvestBlock(placePos.getX(), placePos.getY(), placePos.getZ())) {
                    Block place = applicable.key;
                    // 1.7.10: No getStateForPlacement, use block directly
                    if (MiscUtils.canPlayerPlaceBlockPos(playerIn, side, place, placePos, EnumFacing.getFront(side))) {
                        if (world.setBlock(
                            placePos.getX(),
                            placePos.getY(),
                            placePos.getZ(),
                            place,
                            applicable.value.getItemDamage(),
                            3)) {
                            drainTempCharge(playerIn, Config.exchangeWandUseCost, false);
                            if (!playerIn.capabilities.isCreativeMode) {
                                ItemUtils.consumeFromPlayerInventory(
                                    playerIn,
                                    stack,
                                    ItemUtils.copyStackWithSize(applicable.value, 1),
                                    false);
                            }
                            PktParticleEvent ev = new PktParticleEvent(
                                PktParticleEvent.ParticleEventType.ARCHITECT_PLACE,
                                placePos);
                            ev.setAdditionalDataLong(Block.getIdFromBlock(atOrigin));
                            PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, placePos, 40));
                        }
                    }
                }
            }
        }
        return true;
    }

}
