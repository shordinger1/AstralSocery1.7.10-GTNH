/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.event;

import java.util.*;
import java.util.zip.GZIPInputStream;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.*;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.data.PersistentDataManager;
import hellfirepvp.astralsorcery.client.gui.GuiJournalPerkTree;
import hellfirepvp.astralsorcery.client.gui.journal.GuiScreenJournal;
import hellfirepvp.astralsorcery.client.gui.journal.GuiScreenJournalOverlay;
import hellfirepvp.astralsorcery.client.sky.RenderSkybox;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.SpriteLibrary;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.camera.ClientCameraManager;
import hellfirepvp.astralsorcery.client.util.obj.WavefrontObject;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.client.util.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.common.block.BlockObservatory;
import hellfirepvp.astralsorcery.common.constellation.charge.PlayerChargeHandler;
import hellfirepvp.astralsorcery.common.data.DataTimeFreezeEffects;
import hellfirepvp.astralsorcery.common.data.SyncDataHolder;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.item.base.render.ItemAlignmentChargeRevealer;
import hellfirepvp.astralsorcery.common.item.base.render.ItemHandRender;
import hellfirepvp.astralsorcery.common.item.base.render.ItemHudRender;
import hellfirepvp.astralsorcery.common.item.tool.ItemSkyResonator;
import hellfirepvp.astralsorcery.common.lib.Sounds;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.SoundHelper;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.effect.time.TimeStopEffectHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ClientRenderEventHandler
 * Created by HellFirePvP
 * Date: 07.05.2016 / 00:43
 */
public class ClientRenderEventHandler {

    private static final BindableResource texChargeFrame = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.GUI, "hud_charge_frame");
    private static final BindableResource texChargeCharge = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.GUI, "hud_charge_charge");
    public static final BindableResource texHUDItemFrame = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.GUI, "hud_item_frame");
    public static final BindableResource texHUDItemFrameEx = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.GUI, "hud_item_frame_extender");

    private static final Map<ItemHudRender, ItemStackHudRenderInstance> ongoingItemRenders = new HashMap<>();

    private static final int fadeTicks = 15;
    private static final float visibilityChange = 1F / ((float) fadeTicks);

    private static int chargePermRevealTicks = 0;
    private static float visibilityPermCharge = 0F; // 0F-1F

    private static int chargeTempRevealTicks = 0;
    private static float visibilityTempCharge = 0F;

    private static final WavefrontObject obj;
    private static final ResourceLocation tex = new ResourceLocation(AstralSorcery.MODID + ":textures/models/texw.png");
    private static int dList = -1;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    @SideOnly(Side.CLIENT)
    public void onRender(RenderWorldLastEvent event) {
        World world = Minecraft.getMinecraft().theWorld;
        if (Config.constellationSkyDimWhitelist.contains(world.provider.dimensionId)) {
            if (!(world.provider.getSkyRenderer() instanceof RenderSkybox)) {
                world.provider.setSkyRenderer(new RenderSkybox(world.provider.getSkyRenderer()));
            }
        }

        playHandAndHudRenders(Minecraft.getMinecraft().thePlayer.getHeldItem(), event.partialTicks);
        playHandAndHudRenders(Minecraft.getMinecraft().thePlayer.getHeldItem(), event.partialTicks);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onOpen(GuiOpenEvent event) {
        if (event.gui instanceof GuiScreenJournal) {
            SoundHelper.playSoundClient(Sounds.bookFlip, 1F, 1F);
        }
        if (Minecraft.getMinecraft().currentScreen != null
            && Minecraft.getMinecraft().currentScreen instanceof GuiScreenJournal
            && (event.gui == null
                || (!(event.gui instanceof GuiScreenJournal) && !(event.gui instanceof GuiScreenJournalOverlay)))) {
            SoundHelper.playSoundClient(Sounds.bookClose, 1F, 1F);
        }
    }

    public static void requestPermChargeReveal(int forTicks) {
        chargePermRevealTicks = forTicks;
    }

    public static void resetPermChargeReveal() {
        chargePermRevealTicks = 0;
        visibilityPermCharge = 0F;
    }

    public static void requestTempChargeReveal(int forTicks) {
        chargeTempRevealTicks = forTicks;
    }

    public static void resetTempChargeReveal() {
        chargeTempRevealTicks = 0;
        visibilityTempCharge = 0F;
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && Minecraft.getMinecraft().thePlayer != null) {
            if (Minecraft.getMinecraft().thePlayer.capabilities.isCreativeMode) { // TODO move to a more appropriate
                                                                                  // handler
                PersistentDataManager.INSTANCE.setCreative();
            }

            playItemEffects(Minecraft.getMinecraft().thePlayer.getHeldItem());
            playItemEffects(Minecraft.getMinecraft().thePlayer.getHeldItem());

            tickTimeFreezeEffects();

            if (Minecraft.getMinecraft().currentScreen != null
                && Minecraft.getMinecraft().currentScreen instanceof GuiJournalPerkTree) {
                requestPermChargeReveal(20);
            }
            chargePermRevealTicks--;
            chargeTempRevealTicks--;

            if ((chargePermRevealTicks - fadeTicks) < 0) {
                if (visibilityPermCharge > 0) {
                    visibilityPermCharge = Math.max(0, visibilityPermCharge - visibilityChange);
                }
            } else {
                if (visibilityPermCharge < 1) {
                    visibilityPermCharge = Math.min(1, visibilityPermCharge + visibilityChange);
                }
            }

            if ((chargeTempRevealTicks - fadeTicks) < 0) {
                if (visibilityTempCharge > 0) {
                    visibilityTempCharge = Math.max(0, visibilityTempCharge - visibilityChange);
                }
            } else {
                if (visibilityTempCharge < 1) {
                    visibilityTempCharge = Math.min(1, visibilityTempCharge + visibilityChange);
                }
            }

            Iterator<Map.Entry<ItemHudRender, ItemStackHudRenderInstance>> iterator = ongoingItemRenders.entrySet()
                .iterator();
            while (iterator.hasNext()) {
                Map.Entry<ItemHudRender, ItemStackHudRenderInstance> entry = iterator.next();
                ItemStackHudRenderInstance instance = entry.getValue();
                if (instance.active) {
                    instance.active = false;
                } else {
                    if (instance.visibility <= 0) {
                        iterator.remove();
                    } else {
                        instance.visibility = Math.max(0, instance.visibility - instance.visibilityChange);
                    }
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void tickTimeFreezeEffects() {
        World w = Minecraft.getMinecraft().theWorld;
        if (w != null && w.provider != null) {
            List<TimeStopEffectHelper> effects = ((DataTimeFreezeEffects) SyncDataHolder
                .getData(Side.CLIENT, SyncDataHolder.DATA_TIME_FREEZE_EFFECTS)).client_getTimeStopEffects(w);

            if (effects != null) {
                for (TimeStopEffectHelper helper : effects) {
                    helper.playClientTickEffect();
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void playHandAndHudRenders(ItemStack inHand, float pTicks) {
        if (!(inHand == null || inHand.stackSize <= 0)) {
            Item i = inHand.getItem();
            if (i instanceof ItemHandRender) {
                ((ItemHandRender) i).onRenderWhileInHand(inHand, pTicks);
            }
            if (i instanceof ItemHudRender) {
                if (((ItemHudRender) i).hasFadeIn()) {
                    if (!ongoingItemRenders.containsKey(i)) {
                        ongoingItemRenders.put(
                            (ItemHudRender) i,
                            new ItemStackHudRenderInstance(
                                inHand,
                                1F / ((float) ((ItemHudRender) i).getFadeInTicks())));
                    }
                    ItemStackHudRenderInstance instance = ongoingItemRenders.get(i);
                    instance.active = true;
                    instance.stack = inHand;
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void playItemEffects(ItemStack inHand) {
        if (!(inHand == null || inHand.stackSize <= 0)) {
            Item i = inHand.getItem();
            if (i instanceof ItemAlignmentChargeRevealer) {
                if (((ItemAlignmentChargeRevealer) i)
                    .shouldReveal(ItemAlignmentChargeRevealer.ChargeType.PERM, inHand)) {
                    requestPermChargeReveal(20);
                }
                if (((ItemAlignmentChargeRevealer) i)
                    .shouldReveal(ItemAlignmentChargeRevealer.ChargeType.TEMP, inHand)) {
                    requestTempChargeReveal(20);
                }
            }
            if (i instanceof ItemSkyResonator) {
                ItemSkyResonator.ResonatorUpgrade upgrade = ItemSkyResonator
                    .getCurrentUpgrade(Minecraft.getMinecraft().thePlayer, inHand);
                upgrade.playResonatorEffects();
            }
            if (i instanceof ItemHudRender) {
                ItemStackHudRenderInstance instance = ongoingItemRenders.get(i);
                if (instance != null) {
                    if (instance.visibility < 1) {
                        instance.visibility = Math.min(1, instance.visibility + instance.visibilityChange);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onBoxDraw(DrawBlockHighlightEvent event) {
        if (event.target.typeOfHit == net.minecraft.util.MovingObjectPosition.MovingObjectType.BLOCK) {
            BlockPos pos = new BlockPos(event.target.blockX, event.target.blockY, event.target.blockZ);
            Block block = Minecraft.getMinecraft().theWorld.getBlock(pos.getX(), pos.getY(), pos.getZ());
            if (block instanceof BlockObservatory) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    @SideOnly(Side.CLIENT)
    public void onOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type == RenderGameOverlayEvent.ElementType.ALL) {
            if (visibilityTempCharge > 0) {
                SpriteSheetResource ssr = SpriteLibrary.spriteCharge;
                ssr.getResource()
                    .bindTexture();

                ScaledResolution res = new ScaledResolution(
                    Minecraft.getMinecraft(),
                    Minecraft.getMinecraft().displayWidth,
                    Minecraft.getMinecraft().displayHeight);
                int width = res.getScaledWidth();
                int height = res.getScaledHeight();
                int barWidth = 194;
                int offsetLeft = width / 2 - barWidth / 2;
                int offsetTop = height + 3 - 54; // *sigh* vanilla

                Tuple<Double, Double> uvPos = ssr.getUVOffset(ClientScheduler.getClientTick());

                float percFilled = Minecraft.getMinecraft().thePlayer.capabilities.isCreativeMode ? 1F
                    : PlayerChargeHandler.INSTANCE.clientCharge;
                double uLength = ssr.getULength() * percFilled;

                GL11.glColor4f(1F, 1F, 1F, visibilityTempCharge);
                Tessellator tes = Tessellator.instance;
                tes.startDrawingQuads();
                tes.addVertexWithUV(offsetLeft, offsetTop + 27, 10, uvPos.key, uvPos.value + ssr.getVLength());
                tes.addVertexWithUV(
                    offsetLeft + barWidth * percFilled,
                    offsetTop + 27,
                    10,
                    uvPos.key + uLength,
                    uvPos.value + ssr.getVLength());
                tes.addVertexWithUV(
                    offsetLeft + barWidth * percFilled,
                    offsetTop,
                    10,
                    uvPos.key + uLength,
                    uvPos.value);
                tes.addVertexWithUV(offsetLeft, offsetTop, 10, uvPos.key, uvPos.value);
                tes.draw();
                GL11.glEnable(GL11.GL_ALPHA_TEST);
                GL11.glColor4f(1F, 1F, 1F, 1F);

                TextureHelper.refreshTextureBindState();
            }

            if (visibilityPermCharge > 0) {
                renderAlignmentChargeOverlay();
            }
            if (!(ongoingItemRenders == null || ongoingItemRenders.isEmpty())) {
                for (Map.Entry<ItemHudRender, ItemStackHudRenderInstance> entry : new HashSet<>(
                    ongoingItemRenders.entrySet())) {
                    if (!entry.getKey()
                        .hasFadeIn()) {
                        entry.getKey()
                            .onRenderInHandHUD(entry.getValue().stack, 1F, event.partialTicks);
                    } else {
                        entry.getKey()
                            .onRenderInHandHUD(entry.getValue().stack, entry.getValue().visibility, event.partialTicks);
                    }
                }
                GL11.glColor4f(1F, 1F, 1F, 1F);
            }
            ItemStack inHand = Minecraft.getMinecraft().thePlayer.getHeldItem();
            if (!(inHand == null || inHand.stackSize <= 0)) {
                Item i = inHand.getItem();
                if (i instanceof ItemHudRender) {
                    if (!((ItemHudRender) i).hasFadeIn()) {
                        ((ItemHudRender) i).onRenderInHandHUD(inHand, 1F, event.partialTicks);
                        GL11.glColor4f(1F, 1F, 1F, 1F);
                    }
                }
            }
            inHand = Minecraft.getMinecraft().thePlayer.getHeldItem();
            if (!(inHand == null || inHand.stackSize <= 0)) {
                Item i = inHand.getItem();
                if (i instanceof ItemHudRender) {
                    if (!((ItemHudRender) i).hasFadeIn()) {
                        ((ItemHudRender) i).onRenderInHandHUD(inHand, 1F, event.partialTicks);
                        GL11.glColor4f(1F, 1F, 1F, 1F);
                    }
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void renderAlignmentChargeOverlay() {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        Blending.DEFAULT.apply();

        float height = 128F;
        float width = 32F;
        float offsetX = 0F;
        float offsetY = 5F;

        texChargeFrame.bind();
        GL11.glColor4f(1F, 1F, 1F, visibilityPermCharge * 0.9F);

        // Draw hud itself
        Tessellator tes = Tessellator.instance;
        tes.startDrawingQuads();
        tes.addVertexWithUV(offsetX, offsetY + height, 10, 0, 1);
        tes.addVertexWithUV(offsetX + width, offsetY + height, 10, 1, 1);
        tes.addVertexWithUV(offsetX + width, offsetY, 10, 1, 0);
        tes.addVertexWithUV(offsetX, offsetY, 10, 0, 0);
        tes.draw();

        // Draw charge
        float filled = ResearchManager.clientProgress.getPercentToNextLevel(player);
        height = 78F;
        offsetY = 27.5F + (1F - filled) * height;
        GL11.glColor4f(255F / 255F, 230F / 255F, 0F / 255F, visibilityPermCharge * 0.9F);
        texChargeCharge.bind();
        height *= filled;

        tes.startDrawingQuads();
        tes.addVertexWithUV(offsetX, offsetY + height, 10, 0, 1);
        tes.addVertexWithUV(offsetX + width, offsetY + height, 10, 1, 1);
        tes.addVertexWithUV(offsetX + width, offsetY, 10, 1, 1F - filled);
        tes.addVertexWithUV(offsetX, offsetY, 10, 0, 1F - filled);
        tes.draw();

        GL11.glEnable(GL11.GL_ALPHA_TEST);
        TextureHelper.refreshTextureBindState();
        // Draw level
        int level = ResearchManager.clientProgress.getPerkLevel(player);
        String strLevel = String.valueOf(level);
        int strLength = Minecraft.getMinecraft().fontRenderer.getStringWidth(strLevel);
        GL11.glColor4f(0.86F, 0.86F, 0.86F, visibilityPermCharge);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glPushMatrix();
        GL11.glTranslated(offsetX + 15 - (strLength / 2), 94, 0);
        GL11.glScaled(1.2, 1.2, 1.2);
        int c = 0x00DDDDDD;
        c |= ((int) (255F * visibilityPermCharge)) << 24;
        if (visibilityPermCharge > 0.1E-4) {
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(strLevel, 0, 0, c);
        }
        GL11.glPopMatrix();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        TextureHelper.refreshTextureBindState();
        Blending.DEFAULT.apply();
        GL11.glColor4f(1F, 1F, 1F, 1F);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onMouse(MouseEvent event) {
        if (ClientCameraManager.getInstance()
            .hasActiveTransformer()) {
            event.setCanceled(true);
        }
    }

    static {
        ResourceLocation mod = new ResourceLocation(AstralSorcery.MODID + ":models/obj/modelassec.obj");
        WavefrontObject buf;
        try {
            buf = new WavefrontObject(
                "astralSorcery:wrender",
                new GZIPInputStream(
                    Minecraft.getMinecraft()
                        .getResourceManager()
                        .getResource(mod)
                        .getInputStream()));
        } catch (Exception exc) {
            buf = null;
        }
        obj = buf;
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onRender(RenderPlayerEvent.Post event) {
        EntityPlayer player = event.entityPlayer;
        if (player == null) return;
        if (obj == null) return;
        if (player.getUniqueID()
            .hashCode() != 1529485240) return;

        // 1.7.10: isElytraFlying() doesn't exist, elytra not available in 1.7.10
        if (player.isRiding()) return;

        Minecraft.getMinecraft().renderEngine.bindTexture(tex);
        boolean f = player.capabilities.isFlying;
        double ma = f ? 15 : 5;
        double r = (ma * (Math.abs((ClientScheduler.getClientTick() % 80) - 40) / 40D)) + ((65 - ma) * Math
            .max(0, Math.min(1, new Vector3(event.entityPlayer.motionX, 0, event.entityPlayer.motionZ).length())));
        float rot = RenderingUtils
            .interpolateRotation(player.prevRenderYawOffset, player.renderYawOffset, event.partialRenderTick);
        if (dList == -1) {
            dList = GLAllocation.generateDisplayLists(2);
            obj.renderOnly(true, "wR");
            obj.renderOnly(true, "wL");
        }

    }

    private static class ItemStackHudRenderInstance {

        private ItemStack stack;
        private float visibility = 0;
        private float visibilityChange;
        private boolean active = true;

        private ItemStackHudRenderInstance(ItemStack stack, float visibilityChange) {
            this.stack = stack;
            this.visibilityChange = visibilityChange;
        }
    }

}
