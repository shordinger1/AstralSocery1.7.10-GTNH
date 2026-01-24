/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.data.KnowledgeFragmentData;
import hellfirepvp.astralsorcery.client.data.PersistentDataManager;
import hellfirepvp.astralsorcery.client.gui.base.GuiSkyScreen;
import hellfirepvp.astralsorcery.client.gui.base.GuiTileBase;
import hellfirepvp.astralsorcery.client.sky.RenderAstralSkybox;
import hellfirepvp.astralsorcery.client.util.*;
import hellfirepvp.astralsorcery.client.util.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.MoonPhase;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.constellation.distribution.WorldSkyHandler;
import hellfirepvp.astralsorcery.common.constellation.star.StarConnection;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.data.fragment.KnowledgeFragment;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.item.knowledge.ItemKnowledgeFragment;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.client.PktDiscoverConstellation;
import hellfirepvp.astralsorcery.common.tile.TileObservatory;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.WrapMathHelper;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiObservatory
 * Created by HellFirePvP
 * Date: 27.05.2018 / 07:29
 */
public class GuiObservatory extends GuiTileBase<TileObservatory> implements GuiSkyScreen {

    private static final Random random = new Random();

    private static final int frameSize = 16;
    private static final AbstractRenderableTexture texPartFrame = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.GUI, "observatoryframe");
    private static final AbstractRenderableTexture textureConnection = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.EFFECT, "connectionperks");

    private static final int randomStars = 220;
    private List<StarPosition> usedStars = new ArrayList<>(randomStars);
    private EntityPlayer owningPlayer;

    private Map<IConstellation, Map<StarLocation, Rectangle>> drawnStars = null;

    private boolean grabCursor = false;

    private LinkedList<GuiTelescope.Line> drawnLines = new LinkedList<>();
    private Point start, end;

    public GuiObservatory(EntityPlayer owningPlayer, TileObservatory te) {
        super(
            te,
            new ScaledResolution(Minecraft.getMinecraft(), mc.displayWidth, mc.displayHeight).getScaledHeight()
                - (frameSize * 2),
            new ScaledResolution(Minecraft.getMinecraft(), mc.displayWidth, mc.displayHeight).getScaledWidth()
                - (frameSize * 2));
        this.owningPlayer = owningPlayer;

        Optional<Long> currSeed = ConstellationSkyHandler.getInstance()
            .getSeedIfPresent(Minecraft.getMinecraft().theWorld);
        currSeed.ifPresent(new Consumer<Long>() {

            @Override
            public void accept(Long seed) {
                setupInitialStars(seed);
            }
        });
    }

    private void setupInitialStars(long seed) {
        Random rand = new Random(seed);

        int day = (int) (Minecraft.getMinecraft().theWorld.getWorldTime() / Config.dayLength);
        for (int i = 0; i < Math.abs(day); i++) {
            rand.nextLong(); // Flush
        }

        for (int i = 0; i < randomStars; i++) {
            usedStars.add(
                new StarPosition(frameSize + rand.nextFloat() * guiWidth, frameSize + rand.nextFloat() * guiHeight));
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();

        // 1.7.10: Container system is simpler, no manual packet needed
        // mc.thePlayer.closeScreen(); // Optional: let the GUI system handle it

        // 1.7.10: KeyBinding.updateKeyBindState() doesn't exist
        // if (!Minecraft.isRunningOnMac) {
        // KeyBinding.updateKeyBindState();
        // }
        ClientUtils.grabMouseCursor();
        mc.inGameHasFocus = true;

        mc.thePlayer.renderYawOffset = mc.thePlayer.rotationYawHead;
        mc.thePlayer.prevRenderYawOffset = mc.thePlayer.prevRotationYawHead;
    }

    @Override
    public void initGui() {
        super.initGui();

        this.mc.thePlayer.rotationPitch = getOwningTileEntity().observatoryPitch;
        this.mc.thePlayer.prevRotationPitch = getOwningTileEntity().prevObservatoryPitch;

        this.mc.thePlayer.rotationYaw = getOwningTileEntity().observatoryYaw;
        this.mc.thePlayer.rotationYawHead = getOwningTileEntity().observatoryYaw;
        this.mc.thePlayer.prevRotationYaw = this.mc.thePlayer.rotationYaw;
        this.mc.thePlayer.prevRotationYawHead = this.mc.thePlayer.rotationYaw;

        // 1.7.10: KeyBinding.updateKeyBindState() doesn't exist
        // if (!Minecraft.isRunningOnMac) {
        // KeyBinding.updateKeyBindState();
        // }
        ClientUtils.grabMouseCursor();
        mc.inGameHasFocus = true;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        World w = Minecraft.getMinecraft().theWorld;
        if (w == null) return;

        // 1.7.10: ScaledResolution needs (Minecraft, displayWidth, displayHeight)
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft(), mc.displayWidth, mc.displayHeight);

        Minecraft.getMinecraft().gameSettings.thirdPersonView = 0;
        this.drawnStars = null;

        handleMouseMovement(partialTicks);

        Blending.DEFAULT.applyStateManager();

        float pitch = Minecraft.getMinecraft().thePlayer.rotationPitch;
        float transparency = 0F;
        if (pitch < 0F) {
            transparency = 1F;
        } else if (pitch < 10F) {
            transparency = (Math.abs(pitch) + 10F) / 10F;
            if (ConstellationSkyHandler.getInstance()
                .isNight(w)) {
                transparency *= transparency;
            }
        }
        boolean canSeeSky = canTelescopeSeeSky(w);

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(
            (frameSize - 2) * sr.getScaleFactor(),
            (frameSize - 2) * sr.getScaleFactor(),
            (guiWidth + 2) * sr.getScaleFactor(),
            (guiHeight + 2) * sr.getScaleFactor());
        drawGridBackground(partialTicks, canSeeSky, transparency);

        drawEffectBackground(partialTicks, canSeeSky, transparency);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        zLevel += 10;
        drawFrame();
        zLevel -= 10;
    }

    private void drawEffectBackground(float partialTicks, boolean canSeeSky, float transparency) {
        if (usedStars.isEmpty()) {
            Optional<Long> currSeed = ConstellationSkyHandler.getInstance()
                .getSeedIfPresent(Minecraft.getMinecraft().theWorld);
            if (currSeed.isPresent()) {
                setupInitialStars(currSeed.get());

                zLevel += 5;
                drawCellWithEffects(partialTicks, canSeeSky, transparency);
                zLevel -= 5;
            }
        } else {
            zLevel += 5;
            drawCellWithEffects(partialTicks, canSeeSky, transparency);
            zLevel -= 5;
        }
    }

    private void drawCellWithEffects(float partialTicks, boolean canSeeSky, float transparency) {
        WorldSkyHandler handle = ConstellationSkyHandler.getInstance()
            .getWorldHandler(Minecraft.getMinecraft().theWorld);
        int lastTracked = handle == null ? 5 : handle.lastRecordedDay;
        Optional<Long> seed = ConstellationSkyHandler.getInstance()
            .getSeedIfPresent(Minecraft.getMinecraft().theWorld);
        long s = 0;
        if (seed.isPresent()) {
            s = seed.get();
        }
        Random r = new Random(s * 31 + lastTracked * 31);

        GL11.glEnable(GL11.GL_BLEND);
        Blending.DEFAULT.applyStateManager();

        if (canSeeSky) {
            int offsetX = guiLeft;
            int offsetZ = guiTop;
            zLevel += 1;
            drawnStars = drawCellEffect(offsetX, offsetZ, getGuiWidth(), getGuiHeight(), partialTicks, transparency);
            zLevel -= 1;
        } else {
            clearLines();
            abortDrawing();
        }

        zLevel += 2;
        drawDrawnLines(r, partialTicks);
        zLevel -= 2;

    }

    private void drawDrawnLines(final Random r, final float pTicks) {
        if (!canStartDrawing()) {
            clearLines();
            abortDrawing();
            return;
        }

        float linebreadth = 2F;
        RenderConstellation.BrightnessFunction func = new RenderConstellation.BrightnessFunction() {

            @Override
            public float getBrightness() {
                return RenderConstellation.conCFlicker(ClientScheduler.getClientTick(), pTicks, 5 + r.nextInt(15));
            }
        };

        textureConnection.bindTexture();

        for (int j = 0; j < 2; j++) {
            for (GuiTelescope.Line l : drawnLines) {
                drawLine(l.start, l.end, func, linebreadth, true);
            }

            if (start != null && end != null) {
                Point adjStart = new Point(start.x - guiLeft, start.y - guiTop);
                Point adjEnd = new Point(end.x - guiLeft, end.y - guiTop);
                drawLine(adjStart, adjEnd, func, linebreadth, false);
            }
        }
    }

    private void drawLine(Point start, Point end, RenderConstellation.BrightnessFunction func, float linebreadth,
        boolean applyFunc) {
        Tessellator tes = Tessellator.instance;
        tes.startDrawingQuads();

        float brightness;
        if (applyFunc) {
            brightness = func.getBrightness();
        } else {
            brightness = 1F;
        }
        float starBr = Minecraft.getMinecraft().theWorld.getStarBrightness(1.0F);
        if (starBr <= 0.0F) {
            return;
        }
        brightness *= (starBr * 2);
        GL11.glColor4f(brightness, brightness, brightness, brightness < 0 ? 0 : brightness);

        Vector3 fromStar = new Vector3(guiLeft + start.getX(), guiTop + start.getY(), zLevel);
        Vector3 toStar = new Vector3(guiLeft + end.getX(), guiTop + end.getY(), zLevel);

        Vector3 dir = toStar.clone()
            .subtract(fromStar);
        Vector3 degLot = dir.clone()
            .crossProduct(new Vector3(0, 0, 1))
            .normalize()
            .multiply(linebreadth);// .multiply(j == 0 ? 1 : -1);

        Vector3 vec00 = fromStar.clone()
            .add(degLot);
        Vector3 vecV = degLot.clone()
            .multiply(-2);

        for (int i = 0; i < 4; i++) {
            int u = ((i + 1) & 2) >> 1;
            int v = ((i + 2) & 2) >> 1;

            Vector3 pos = vec00.clone()
                .add(
                    dir.clone()
                        .multiply(u))
                .add(
                    vecV.clone()
                        .multiply(v));
            tes.addVertexWithUV(pos.getX(), pos.getY(), pos.getZ(), u, v);
        }

        tes.draw();
    }

    private Map<IConstellation, Map<StarLocation, Rectangle>> drawCellEffect(int offsetX, int offsetY, int width,
        int height, float partialTicks, float transparency) {

        WorldSkyHandler handle = ConstellationSkyHandler.getInstance()
            .getWorldHandler(Minecraft.getMinecraft().theWorld);
        int lastTracked = handle == null ? 5 : handle.lastRecordedDay;
        Random r = new Random();

        RenderAstralSkybox.TEX_STAR_1.bind();
        for (StarPosition stars : usedStars) {
            r.setSeed(stars.seed);
            float brightness = 0.3F
                + (RenderConstellation.stdFlicker(ClientScheduler.getClientTick(), partialTicks, 5 + r.nextInt(15)))
                    * 0.6F;
            brightness *= Minecraft.getMinecraft().theWorld.getStarBrightness(partialTicks) * 2 * transparency;
            brightness *= (1F - Minecraft.getMinecraft().theWorld.getRainStrength(partialTicks));
            int size = r.nextInt(4) + 2;
            drawRect(WrapMathHelper.floor(offsetX + stars.x), WrapMathHelper.floor(offsetY + stars.y), size, size);
        }

        Random cstRand = new Random(lastTracked * 31);
        for (int i = 0; i < 5 + cstRand.nextInt(10); i++) {
            cstRand.nextLong();
        }
        r.setSeed(lastTracked * 31);

        double playerYaw = (Minecraft.getMinecraft().thePlayer.rotationYaw + 180) % 360F;
        double playerPitch = Minecraft.getMinecraft().thePlayer.rotationPitch;

        if (playerYaw < 0) {
            playerYaw += 360F;
        }
        if (playerYaw >= 180F) {
            playerYaw -= 360F;
        }
        float cstSizeX = 55F;
        float cstSizeY = 35F;

        float rainBr = 1F - Minecraft.getMinecraft().theWorld.getRainStrength(partialTicks);

        Map<IConstellation, Map<StarLocation, Rectangle>> cstMap = new HashMap<>();
        if (handle != null && transparency > 0) {
            List<IConstellation> actives = handle.getActiveConstellations();
            List<IConstellation> scrollActives = new LinkedList<>();

            List<ItemStack> fragmentStacks = ItemKnowledgeFragment.gatherFragments(owningPlayer);
            List<KnowledgeFragment> fragList = new LinkedList<>();
            for (ItemStack item : fragmentStacks) {
                KnowledgeFragment frag = ItemKnowledgeFragment.resolveFragment(item);
                Optional<Long> seedOpt = ItemKnowledgeFragment.getSeed(item);
                if (seedOpt.isPresent() && frag != null && !fragList.contains(frag)) {
                    fragList.add(frag);

                    IConstellation cst = frag.getDiscoverConstellation(seedOpt.get());
                    List<MoonPhase> phases = frag.getShowupPhases(seedOpt.get());
                    if (cst != null && phases.contains(handle.getCurrentMoonPhase())) {
                        scrollActives.add(cst);
                    }
                }
            }

            Map<IConstellation, Point.Double> cstOffsets = generateOffsets(actives, scrollActives, cstRand);

            for (Map.Entry<IConstellation, Point.Double> constellationOffset : cstOffsets.entrySet()) {

                double diffYaw = playerYaw - ((float) constellationOffset.getValue().x);
                double diffPitch = playerPitch - ((float) constellationOffset.getValue().y);

                if ((Math.abs(diffYaw) <= cstSizeX || Math.abs(diffYaw += 360F) <= cstSizeX)
                    && Math.abs(diffPitch) <= cstSizeY) {

                    int wPart = ((int) (((float) width) * 0.1F));
                    int hPart = ((int) (((float) height) * 0.1F));

                    Map<StarLocation, Rectangle> rectangles = RenderConstellation.renderConstellationIntoGUI(
                        constellationOffset.getKey(),
                        offsetX + wPart + WrapMathHelper.floor((diffYaw / cstSizeX) * width),
                        offsetY + hPart + WrapMathHelper.floor((diffPitch / cstSizeY) * height),
                        zLevel,
                        ((int) (height * 0.6F)),
                        ((int) (height * 0.6F)),
                        2,
                        new RenderConstellation.BrightnessFunction() {

                            @Override
                            public float getBrightness() {
                                return (0.4F + 0.6F * RenderConstellation
                                    .conCFlicker(ClientScheduler.getClientTick(), partialTicks, 5 + r.nextInt(15)))
                                    * transparency
                                    * rainBr;
                            }
                        },
                        ResearchManager.clientProgress.hasConstellationDiscovered(
                            constellationOffset.getKey()
                                .getUnlocalizedName()),
                        true);

                    cstMap.put(constellationOffset.getKey(), rectangles);
                }
            }
        }

        return cstMap;
    }

    private Map<IConstellation, Point.Double> generateOffsets(List<IConstellation> actives,
        List<IConstellation> knowledgeActives, Random r) {
        float cstGap = 10F;

        r.nextLong();

        Map<IConstellation, Point.Double> offsets = new HashMap<>();
        for (IConstellation cst : actives) {
            Point.Double at;
            while (true) {
                float pitch = -6.5F + r.nextFloat() * -80F;
                float yaw = r.nextFloat() * 360F;
                at = new Point2D.Double(yaw, pitch);
                if (!cstCollides(offsets, at, cstGap)) {
                    break;
                }
            }
            offsets.put(cst, at);
        }
        for (IConstellation cst : knowledgeActives) {
            int attempts = 50;
            Point.Double found = null;
            while (attempts > 0) {
                attempts--;
                float pitch = -6.5F + r.nextFloat() * -80F;
                float yaw = r.nextFloat() * 360F;
                Point.Double at = new Point2D.Double(yaw, pitch);
                if (!cstCollides(offsets, at, cstGap)) {
                    found = at;
                    break;
                }
            }
            if (found != null) {
                offsets.put(cst, found);
            }
        }
        return offsets;
    }

    private boolean cstCollides(Map<IConstellation, Point2D.Double> offsets, Point2D.Double at, float cstGap) {
        double sq = Math.sqrt((cstGap * cstGap) * 2);
        for (Point2D.Double point : offsets.values()) {
            if (point.distance(at) <= sq) {
                return true;
            }
        }
        return false;
    }

    private void drawFrame() {
        texPartFrame.bindTexture();

        // Draw corners
        drawTexturedRectAtCurrentPos(frameSize, frameSize, 0, 0, 8F / 20F, 8F / 20F);
        drawTexturedRectAtCurrentPos(frameSize, frameSize, 8F / 20F, 0, 8F / 20F, 8F / 20F);
        drawTexturedRectAtCurrentPos(frameSize, frameSize, 8F / 20F, 8F / 20F, 8F / 20F, 8F / 20F);
        drawTexturedRectAtCurrentPos(frameSize, frameSize, 0, 8F / 20F, 8F / 20F, 8F / 20F);

        // Draw frame border
        drawTexturedRectAtCurrentPos(guiWidth, frameSize, 16F / 20F, 0, 1F / 20F, 8F / 20F);
        drawTexturedRectAtCurrentPos(frameSize, guiHeight, 0, 17F / 20F, 8F / 20F, 1F / 20F);
        drawTexturedRectAtCurrentPos(guiWidth, frameSize, 17F / 20F, 0, 1F / 20F, 8F / 20F);
        drawTexturedRectAtCurrentPos(frameSize, guiHeight, 0, 16F / 20F, 8F / 20F, 1F / 20F);

        TextureHelper.refreshTextureBindState();
    }

    private void handleMouseMovement(float pticks) {
        boolean ctrl = isShiftKeyDown();

        if (grabCursor && !ctrl) {
            // 1.7.10: KeyBinding.updateKeyBindState() doesn't exist
            // if (!Minecraft.isRunningOnMac) {
            // KeyBinding.updateKeyBindState();
            // }
            ClientUtils.grabMouseCursor();
            Minecraft.getMinecraft().inGameHasFocus = true;
            grabCursor = false;
            clearLines();
        }
        if (!grabCursor && ctrl) {
            ClientUtils.ungrabMouseCursor();
            Minecraft.getMinecraft().inGameHasFocus = false;
            grabCursor = true;
        }

        if (!ctrl) {

            float f = this.mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
            float f1 = f * f * f * 8.0F;
            float f2 = (float) this.mc.mouseHelper.deltaX * f1;
            float f3 = (float) this.mc.mouseHelper.deltaY * f1;
            int i = 1;

            if (this.mc.gameSettings.invertMouse) {
                i = -1;
            }

            float movementX;
            float movementY;
            EntityRenderer er = Minecraft.getMinecraft().entityRenderer;
            if (this.mc.gameSettings.smoothCamera) {
                er.smoothCamYaw += f2;
                er.smoothCamPitch += f3;
                float f4 = pticks - er.smoothCamPartialTicks;
                er.smoothCamPartialTicks = pticks;
                f2 = er.smoothCamFilterX * f4;
                f3 = er.smoothCamFilterY * f4;
                movementX = f2;
                movementY = f3 * i;
            } else {
                er.smoothCamYaw = 0.0F;
                er.smoothCamPitch = 0.0F;
                movementX = f2;
                movementY = f3 * i;
            }
            boolean nullify = this.mc.thePlayer.rotationPitch <= -89.99F && Math.abs(movementY) == movementY;
            // 1.7.10: EntityPlayer doesn't have turn() method, need to modify rotation directly
            this.mc.thePlayer.rotationYaw += movementX;
            this.mc.thePlayer.rotationPitch += movementY;
            if (this.mc.thePlayer.rotationPitch >= -10F) {
                this.mc.thePlayer.rotationPitch = -10F;
                nullify = true;
            } else if (this.mc.thePlayer.rotationPitch <= -75F) {
                this.mc.thePlayer.rotationPitch = -75F;
                nullify = true;
            }
            if (nullify) movementY = 0;

            moveIdleStars(WrapMathHelper.floor(movementX), WrapMathHelper.floor(movementY));
        }
    }

    private void moveIdleStars(int changeX, int changeY) {
        int width = guiWidth, height = guiHeight;

        for (StarPosition sl : usedStars) {
            sl.x -= changeX;
            sl.y += changeY;

            if (Math.abs(changeX) > 0) {
                if (sl.x < 0) {
                    sl.x += width;
                } else if (sl.x > width) {
                    sl.x -= width;
                }
            }
            if (Math.abs(changeY) > 0) {
                if (sl.y < 0) {
                    sl.y += height;
                } else if (sl.y > height) {
                    sl.y -= height;
                }
            }
        }
        /*
         * for (int i = 0; i < (randomStars - usedStars.size()); i++) {
         * usedStars.add(new StarPosition(offsetX + random.nextFloat() * width, offsetY + random.nextFloat() * height));
         * }
         */
    }

    private void drawGridBackground(float partialTicks, boolean canSeeSky, float angleTransparency) {
        Blending.PREALPHA.applyStateManager();
        Tuple<Color, Color> fromTo = GuiSkyScreen.getRBGFromTo(canSeeSky, angleTransparency, partialTicks);
        RenderingUtils.drawGradientRect(
            guiLeft,
            guiTop,
            zLevel,
            guiLeft + guiWidth,
            guiTop + guiHeight,
            fromTo.key,
            fromTo.value);
        Blending.DEFAULT.applyStateManager();
    }

    private boolean canTelescopeSeeSky(World renderWorld) {
        // In 1.7.10, TileEntity doesn't have getPos()
        TileEntity te = getOwningTileEntity();
        BlockPos pos = new BlockPos(te.xCoord, te.yCoord, te.zCoord);
        for (int xx = -1; xx <= 1; xx++) {
            for (int zz = -1; zz <= 1; zz++) {
                if (xx == 0 && zz == 0) continue;
                BlockPos other = pos.add(xx, 0, zz);
                if (!MiscUtils.canSeeSky(renderWorld, other, true, false)) {
                    return false;
                }
            }
        }
        return MiscUtils.canSeeSky(renderWorld, pos.up(), true, false);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseButton == 0) {
            tryStartDrawing(mouseX, mouseY);
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (clickedMouseButton == 0) {
            informMovement(mouseX, mouseY);
        }
    }

    // 1.7.10: mouseReleased doesn't exist in parent class
    protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            informRelease(mouseX, mouseY);
        }
    }

    private void tryStartDrawing(int mouseX, int mouseY) {
        if (!canStartDrawing()) return;

        start = new Point(mouseX, mouseY);
        end = new Point(mouseX, mouseY);
    }

    private boolean canStartDrawing() {
        return Minecraft.getMinecraft().theWorld.getStarBrightness(1.0F) >= 0.35F
            && Minecraft.getMinecraft().theWorld.getRainStrength(1.0F) <= 0.1F;
    }

    private void clearLines() {
        drawnLines.clear();
    }

    private void informMovement(int mouseX, int mouseY) {
        end = new Point(mouseX, mouseY);
    }

    private void informRelease(int mouseX, int mouseY) {
        if (start != null) {
            end = new Point(mouseX, mouseY);
            pushDrawnLine(start, end);
        } else {
            start = null;
            end = null;
        }
        abortDrawing();

        checkConstellation(drawnLines);
    }

    private void checkConstellation(List<GuiTelescope.Line> drawnLines) {
        lblInfos: for (Map.Entry<IConstellation, Map<StarLocation, Rectangle>> info : this.drawnStars.entrySet()) {
            IConstellation c = info.getKey();
            if (c == null || ResearchManager.clientProgress.hasConstellationDiscovered(c.getUnlocalizedName()))
                continue;
            PlayerProgress client = ResearchManager.clientProgress;
            if (client == null) return;

            boolean has = c instanceof ClientConstellationGenerator.ClientConstellation;
            for (String strConstellation : client.getSeenConstellations()) {
                IConstellation ce = ConstellationRegistry.getConstellationByName(strConstellation);
                if (ce != null && ce.equals(c)) {
                    has = true;
                    break;
                }
            }

            if (!has) continue;

            List<StarConnection> sc = c.getStarConnections();
            if (sc.size() != drawnLines.size()) continue; // Can't match otherwise anyway.
            if (!c.canDiscover(Minecraft.getMinecraft().thePlayer, ResearchManager.clientProgress)) continue;

            Map<StarLocation, Rectangle> stars = info.getValue();

            for (StarConnection connection : sc) {
                Rectangle fromRect = stars.get(connection.from);
                if (fromRect == null) {
                    AstralSorcery.log
                        .info("Could not check constellation of telescope drawing - starLocation is missing?");
                    continue lblInfos;
                }
                Rectangle toRect = stars.get(connection.to);
                if (toRect == null) {
                    AstralSorcery.log
                        .info("Could not check constellation of telescope drawing - starLocation is missing?");
                    continue lblInfos;
                }
                if (!containsMatch(drawnLines, fromRect, toRect)) {
                    continue lblInfos;
                }
            }

            // Don't sync mock constellations to server.
            if (c instanceof ClientConstellationGenerator.ClientConstellation) {
                KnowledgeFragment frag = ((ClientConstellationGenerator.ClientConstellation) c).getFragment();
                if (frag != null) {
                    ItemKnowledgeFragment.clearFragment(owningPlayer, frag);
                    KnowledgeFragmentData dat = PersistentDataManager.INSTANCE
                        .getData(PersistentDataManager.PersistentKey.KNOWLEDGE_FRAGMENTS);
                    if (dat.addFragment(frag)) {
                        String cName = c.getUnlocalizedName();
                        cName = (cName == null || cName.length() <= 0) ? ""
                            : Character.toUpperCase(cName.charAt(0)) + cName.substring(1);
                        owningPlayer.addChatMessage(
                            new ChatComponentText(
                                EnumChatFormatting.GREEN + I18n.format("misc.fragment.added.cst", cName)));
                        owningPlayer.addChatMessage(
                            new ChatComponentText(
                                EnumChatFormatting.GREEN
                                    + I18n.format("misc.fragment.added", frag.getLocalizedIndexName())));
                    }
                }
            } else {
                // We found a match. horray.
                PacketChannel.CHANNEL.sendToServer(new PktDiscoverConstellation(c.getUnlocalizedName()));
            }
            clearLines();
            abortDrawing();
            return;
        }
    }

    private boolean containsMatch(List<GuiTelescope.Line> drawnLines, Rectangle r1, Rectangle r2) {
        for (GuiTelescope.Line l : drawnLines) {
            Point start = l.start;
            Point end = l.end;
            start = new Point(start.x + guiLeft, start.y + guiTop);
            end = new Point(end.x + guiLeft, end.y + guiTop);
            if ((r1.contains(start) && r2.contains(end)) || (r2.contains(start) && r1.contains(end))) {
                return true;
            }
        }
        return false;
    }

    private void pushDrawnLine(Point start, Point end) {
        if (Math.abs(start.getX() - end.getX()) <= 2 && Math.abs(start.getY() - end.getY()) <= 2) {
            return; // Rather a point than a line. probably not the users intention...
        }
        Point adjStart = new Point(start.x - guiLeft, start.y - guiTop);
        Point adjEnd = new Point(end.x - guiLeft, end.y - guiTop);
        GuiTelescope.Line l = new GuiTelescope.Line(adjStart, adjEnd);
        this.drawnLines.addLast(l);
    }

    private void abortDrawing() {
        start = null;
        end = null;
    }

    private static class StarPosition {

        private float x;
        private float y;
        private long seed = random.nextLong(); // Bad on performance i know i know.

        private StarPosition(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

}
