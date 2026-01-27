/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.wand;

import java.awt.*;
import java.util.*;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import com.cleanroommc.modularui.utils.item.InvWrapper;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.client.event.ClientRenderEventHandler;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.common.base.Mods;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.integrations.ModIntegrationBotania;
import hellfirepvp.astralsorcery.common.item.ItemBlockStorage;
import hellfirepvp.astralsorcery.common.item.base.render.ItemAlignmentChargeConsumer;
import hellfirepvp.astralsorcery.common.item.base.render.ItemHandRender;
import hellfirepvp.astralsorcery.common.item.base.render.ItemHandRenderHelper;
import hellfirepvp.astralsorcery.common.item.base.render.ItemHudRender;
import hellfirepvp.astralsorcery.common.migration.RayTraceResult;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemArchitectWand
 * Created by HellFirePvP
 * Date: 06.02.2017 / 22:49
 */
public class ItemArchitectWand extends ItemBlockStorage
    implements ItemHandRender, ItemHudRender, ItemAlignmentChargeConsumer {

    private static final double architectRange = 60.0D;

    public ItemArchitectWand() {
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
    @SideOnly(Side.CLIENT)
    public void onRenderInHandHUD(ItemStack lastCacheInstance, float fadeAlpha, float pTicks) {
        Collection<ItemStack> stored = getMappedStoredStates(lastCacheInstance).values();
        if ((stored == null || stored == null || stored.isEmpty())) return;

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
        // 1.7.10: Create new RenderItem instance
        net.minecraft.client.renderer.entity.RenderItem ri = new net.minecraft.client.renderer.entity.RenderItem();

        tempOffsetY = offsetY;
        for (Map.Entry<ItemStack, Integer> entry : amountMap.entrySet()) {
            ri.renderItemAndEffectIntoGUI(
                Minecraft.getMinecraft().fontRenderer,
                Minecraft.getMinecraft().renderEngine,
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
        List<Block> storedStates = Lists.newArrayList(getMappedStoredStates(stack).keySet());
        if (storedStates == null || storedStates.isEmpty()) return;
        Random r = getPreviewRandomFromWorld(Minecraft.getMinecraft().theWorld);

        Deque<BlockPos> placeable = filterBlocksToPlace(
            Minecraft.getMinecraft().thePlayer,
            Minecraft.getMinecraft().theWorld,
            architectRange);
        if (!(placeable == null || placeable == null || placeable.isEmpty())) {
            RayTraceResult rtr = ItemHandRenderHelper
                .getLookBlock(Minecraft.getMinecraft().thePlayer, false, true, architectRange);
            if (rtr == null || rtr.typeOfHit != hellfirepvp.astralsorcery.common.migration.RayTraceResult.Type.BLOCK) {
                return;
            }
            Vec3 hitVec = rtr.hitVec;
            EnumFacing sideHit = rtr.getSideHit();

            Blending.ADDITIVEDARK.applyStateManager();
            RenderingUtils.removeStandartTranslationFromTESRMatrix(pTicks);
            World w = Minecraft.getMinecraft().theWorld;

            TextureHelper.setActiveTextureToAtlasSprite();
            Tessellator tes = Tessellator.instance;
            tes.startDrawing(GL11.GL_QUADS);
            for (BlockPos pos : placeable) {
                Collections.shuffle(storedStates, r);
                Block potentialState = Iterables.getFirst(storedStates, Blocks.air); // 1.7.10: lowercase 'air'
                // 1.7.10: No getStateForPlacement, use block directly
                RenderingUtils.renderBlockSafely(w, pos, potentialState, tes);
            }
            tes.draw();
            Blending.DEFAULT.applyStateManager();
            Blending.DEFAULT.apply();
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World world, EntityPlayer playerIn) {
        ItemStack stack = itemStackIn;
        if ((stack == null || stack.stackSize<=0)) return stack;
        if (world.isRemote) return stack;

        Map<Block, ItemStack> storedStates = getMappedStoredStates(stack);
        if (storedStates == null || storedStates.isEmpty()) return stack;

        RayTraceResult rtr = ItemHandRenderHelper.getLookBlock(playerIn, false, true, architectRange);
        if (rtr == null || rtr.sideHit == -1 || rtr.hitVec == null) return stack;
        EnumFacing sideHit = rtr.getSideHit();
        List<Tuple<Block, ItemStack>> shuffleable = MiscUtils
            .flatten(storedStates, (block, stackItem) -> new Tuple<>(block, stackItem));

        Random r = getPreviewRandomFromWorld(world);
        Deque<BlockPos> placeable = filterBlocksToPlace(playerIn, world, architectRange);
        if (!(placeable == null || placeable == null || placeable.isEmpty())) {
            for (BlockPos placePos : placeable) {
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

                if (drainTempCharge(playerIn, Config.architectWandUseCost, true)) {
                    Block place = applicable.key;
                    // 1.7.10: Extract metadata from ItemStack
                    int meta = applicable.value.getItemDamage();
                    // 1.7.10: canPlayerPlaceBlockPos takes int for side
                    if (MiscUtils.canPlayerPlaceBlockPos(playerIn, place, placePos, sideHit.ordinal())) {
                        if (world.setBlock(placePos.getX(), placePos.getY(), placePos.getZ(), place, meta, 3)) {
                            drainTempCharge(playerIn, Config.architectWandUseCost, false);
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
                            // 1.7.10: Use block ID for additional data
                            ev.setAdditionalDataLong(Block.getIdFromBlock(applicable.key));
                            PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, placePos, 40));
                        }
                    }
                }
            }
        }
        return stack;
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer playerIn, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        ItemStack stack = itemStack;
        if ((stack == null || stack.stackSize<=0)) return true;

        if (playerIn.isSneaking()) {
            tryStoreBlock(stack, world, new BlockPos(x, y, z));
            return true;
        } else {
            if (!world.isRemote) {
                Map<Block, ItemStack> storedStates = getMappedStoredStates(stack);
                if (storedStates == null || storedStates.isEmpty()) return true;

                List<Tuple<Block, ItemStack>> shuffleable = MiscUtils
                    .flatten(storedStates, (block, stackItem) -> new Tuple<>(block, stackItem));
                Random r = getPreviewRandomFromWorld(world);

                Deque<BlockPos> placeable = filterBlocksToPlace(playerIn, world, architectRange);
                if (!(placeable == null || placeable == null || placeable.isEmpty())) {
                    for (BlockPos placePos : placeable) {
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

                        if (drainTempCharge(playerIn, Config.architectWandUseCost, true)) {
                            Block place = applicable.key;
                            // 1.7.10: Extract metadata from ItemStack
                            int meta = applicable.value.getItemDamage();
                            // 1.7.10: canPlayerPlaceBlockPos takes int for side
                            if (MiscUtils.canPlayerPlaceBlockPos(
                                playerIn,
                                place,
                                placePos,
                                EnumFacing.getFront(side)
                                    .ordinal())) {
                                if (world.setBlock(placePos.getX(), placePos.getY(), placePos.getZ(), place, meta, 3)) {
                                    drainTempCharge(playerIn, Config.architectWandUseCost, false);
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
                                    ev.setAdditionalDataLong(Block.getIdFromBlock(applicable.key));
                                    PacketChannel.CHANNEL
                                        .sendToAllAround(ev, PacketChannel.pointFromPos(world, placePos, 40));
                                }
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    @SideOnly(Side.CLIENT)
    public static void playArchitectPlaceEvent(PktParticleEvent event) {
        AstralSorcery.proxy.scheduleClientside(() -> {
            Vector3 at = event.getVec();
            Block state = Block.getBlockById((int) event.getAdditionalDataLong());
            RenderingUtils.playBlockBreakParticles(at.toBlockPos(), state);
            for (int i = 0; i < 9; i++) {
                EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                    at.getX()
                        + (itemRand.nextBoolean() ? -(itemRand.nextFloat() * 0.1) : 1 + (itemRand.nextFloat() * 0.1)),
                    at.getY()
                        + (itemRand.nextBoolean() ? -(itemRand.nextFloat() * 0.1) : 1 + (itemRand.nextFloat() * 0.1)),
                    at.getZ()
                        + (itemRand.nextBoolean() ? -(itemRand.nextFloat() * 0.1) : 1 + (itemRand.nextFloat() * 0.1)));
                p.motion(
                    (itemRand.nextFloat() * 0.03F) * (itemRand.nextBoolean() ? 1 : -1),
                    (itemRand.nextFloat() * 0.03F) * (itemRand.nextBoolean() ? 1 : -1),
                    (itemRand.nextFloat() * 0.03F) * (itemRand.nextBoolean() ? 1 : -1));
                p.scale(0.35F)
                    .setColor(Color.WHITE.brighter());
            }
        }, 1);
    }

    private Deque<BlockPos> filterBlocksToPlace(Entity entity, World world, double range) {
        Deque<BlockPos> placeable = getBlocksToPlaceAt(entity, range);
        boolean discard = false;
        Iterator<BlockPos> iterator = placeable.iterator();
        while (iterator.hasNext()) {
            BlockPos pos = iterator.next();
            if (discard) {
                iterator.remove();
                continue;
            }
            if (!world.isAirBlock(pos.getX(), pos.getY(), pos.getZ())
                && !world.getBlock(pos.getX(), pos.getY(), pos.getZ())
                    .isReplaceable(world, pos.getX(), pos.getY(), pos.getZ())) {
                discard = true;
                iterator.remove();
            }
        }
        return placeable;
    }

    private Deque<BlockPos> getBlocksToPlaceAt(Entity entity, double range) {
        RayTraceResult rtr = ItemHandRenderHelper.getLookBlock(entity, false, true, range);
        if (rtr == null || rtr.typeOfHit != hellfirepvp.astralsorcery.common.migration.RayTraceResult.Type.BLOCK) {
            return Lists.newLinkedList();
        }
        LinkedList<BlockPos> blocks = Lists.newLinkedList();
        // 1.7.10: sideHit is int, use getSideHit() to get EnumFacing
        EnumFacing sideHit = rtr.getSideHit();
        BlockPos hitPos = rtr.blockPos;
        int length;
        int cmpFrom;
        double cmpTo;
        // 1.7.10: EnumFacing doesn't have getAxis(), use offset checks
        if (sideHit.getFrontOffsetX() != 0) {
            cmpFrom = hitPos.getX();
            cmpTo = entity.posX;
        } else if (sideHit.getFrontOffsetY() != 0) {
            cmpFrom = hitPos.getY();
            cmpTo = entity.posY;
        } else {
            cmpFrom = hitPos.getZ();
            cmpTo = entity.posZ;
        }
        length = (int) Math.min(20, Math.abs(cmpFrom + 0.5 - cmpTo));
        for (int i = 1; i < length; i++) {
            blocks.add(
                hitPos
                    .add(sideHit.getFrontOffsetX() * i, sideHit.getFrontOffsetY() * i, sideHit.getFrontOffsetZ() * i));
        }
        return blocks;
    }

}
