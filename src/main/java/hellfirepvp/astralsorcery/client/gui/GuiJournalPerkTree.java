/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui;

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cpw.mods.fml.relauncher.Side;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.gui.journal.*;
import hellfirepvp.astralsorcery.client.gui.journal.overlay.GuiJournalOverlayPerkStats;
import hellfirepvp.astralsorcery.client.gui.journal.page.IGuiRenderablePage;
import hellfirepvp.astralsorcery.client.gui.perk.BatchPerkContext;
import hellfirepvp.astralsorcery.client.gui.perk.DynamicPerkRender;
import hellfirepvp.astralsorcery.client.gui.perk.PerkRenderGroup;
import hellfirepvp.astralsorcery.client.util.*;
import hellfirepvp.astralsorcery.client.util.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.constellation.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.PerkConverter;
import hellfirepvp.astralsorcery.common.constellation.perk.ProgressGatedPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeConverterPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTree;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTreePoint;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.GemSlotPerk;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.item.gem.ItemPerkGem;
import hellfirepvp.astralsorcery.common.item.useables.ItemPerkSeal;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.client.PktPerkGemModification;
import hellfirepvp.astralsorcery.common.network.packet.client.PktRequestPerkSealAction;
import hellfirepvp.astralsorcery.common.network.packet.client.PktUnlockPerk;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.SoundHelper;
import hellfirepvp.astralsorcery.common.util.WrapMathHelper;
import hellfirepvp.astralsorcery.common.util.data.NonDuplicateArrayList;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiJournalPerkTree
 * Created by HellFirePvP
 * Date: 01.07.2018 / 01:14
 */
public class GuiJournalPerkTree extends GuiScreenJournal {

    private static final AbstractRenderableTexture textureResBack = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.GUI, "guiresbg2");
    private static final AbstractRenderableTexture texturePerkConnection = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.EFFECT, "connectionperks");
    private static final AbstractRenderableTexture textureSearchTextBG = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.GUI, "guijtextarea");
    private static final AbstractRenderableTexture textureSlotContext = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.GUI, "slotgemcontext");
    private static final AbstractRenderableTexture textureSearchMark = SpriteLibrary.spriteHalo4;

    private static Rectangle rectSealBox = new Rectangle(29, 16, 16, 16);
    private static Rectangle rectSearchTextEntry = new Rectangle(300, 16, 88, 15);

    private static final BufferBatch drawBufferConnections = BufferBatch.make();
    private static BatchPerkContext drawBuffer;
    private static BatchPerkContext.TextureObjectGroup searchContext;
    private static BatchPerkContext.TextureObjectGroup sealContext;

    private SizeHandler sizeHandler;
    private GuiRenderBoundingBox guiBox;

    private ScalingPoint mousePosition, previousMousePosition;
    private int mouseBufferX, mouseBufferY;
    private boolean mouseDragging = false;

    private AbstractPerk unlockPrimed = null;

    private AbstractPerk sealBreakPrimed = null;
    private int tickSealBreak = 0;

    private int guiOffsetX, guiOffsetY;
    public boolean expectReinit = false;

    private Map<AbstractPerk, Rectangle.Double> thisFramePerks = Maps.newHashMap();
    private Map<AbstractPerk, Long> unlockEffects = Maps.newHashMap();
    private Map<AbstractPerk, Long> breakEffects = Maps.newHashMap();

    private GuiTextEntry searchTextEntry = new GuiTextEntry();
    private List<AbstractPerk> searchMatches = Lists.newArrayList();

    private GemSlotPerk socketMenu = null;
    private Rectangle rSocketMenu = null;
    private Map<Rectangle, Integer> slotsSocketMenu = Maps.newHashMap();
    private Rectangle rStatStar = null;

    private ItemStack mouseSealStack = null;
    private ItemStack foundSeals = null;

    public GuiJournalPerkTree() {
        super(30);
        this.closeWithInventoryKey = false;
        this.searchTextEntry.setChangeCallback(new Runnable() {

            @Override
            public void run() {
                updateSearchHighlight();
            }
        });

        buildTree();
    }

    private void buildTree() {
        this.guiBox = new GuiRenderBoundingBox(10, 10, guiWidth - 10, guiHeight - 10);

        this.sizeHandler = new PerkTreeSizeHandler(this.guiHeight - 40, this.guiWidth - 20);
        this.sizeHandler.setScaleSpeed(0.04F);
        this.sizeHandler.setMaxScale(1F);
        this.sizeHandler.setMinScale(0.1F);
        this.sizeHandler.updateSize();

        this.mousePosition = ScalingPoint.createPoint(0, 0, this.sizeHandler.getScalingFactor(), false);
    }

    public static void initializeDrawBuffer() {
        drawBuffer = new BatchPerkContext();

        searchContext = drawBuffer.addContext(textureSearchMark, BatchPerkContext.PRIORITY_OVERLAY);
        sealContext = drawBuffer.addContext(SpriteLibrary.spritePerkSeal, BatchPerkContext.PRIORITY_FOREGROUND);
        NonDuplicateArrayList<PerkRenderGroup> groups = new NonDuplicateArrayList<>();
        for (PerkTreePoint<?> p : PerkTree.PERK_TREE.getPerkPoints()) {
            p.addGroups(groups);
        }
        for (PerkRenderGroup group : groups) {
            group.batchRegister(drawBuffer);
        }
    }

    @Override
    public void initGui() {
        super.initGui();

        if (this.expectReinit) {
            this.expectReinit = false;
            return;
        }

        this.guiOffsetX = guiLeft + 10;
        this.guiOffsetY = guiTop + 10;

        boolean shifted = false;
        PlayerProgress progress = ResearchManager.clientProgress;
        if (progress != null) {
            IMajorConstellation attunement = progress.getAttunedConstellation();
            if (attunement != null) {
                AbstractPerk root = PerkTree.PERK_TREE.getRootPerk(attunement);
                if (root != null) {
                    Point.Double shift = this.sizeHandler.evRelativePos(root.getOffset());
                    this.moveMouse(WrapMathHelper.floor(shift.x), WrapMathHelper.floor(shift.y));
                    shifted = true;
                }
            }
        }

        if (!shifted) {
            this.moveMouse(
                WrapMathHelper.floor(this.sizeHandler.getTotalWidth() / 2),
                WrapMathHelper.floor(this.sizeHandler.getTotalHeight() / 2));
        }

        this.applyMovedMouseOffset();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.thisFramePerks.clear();

        int dWheelChange = Mouse.getDWheel();
        if (dWheelChange < 0) {
            this.sizeHandler.handleZoomOut();
            this.rescaleMouse();
        }
        if (dWheelChange > 0) {
            this.sizeHandler.handleZoomIn();
            this.rescaleMouse();
        }

        handleMouseMovement(mouseX, mouseY);

        GL11.glEnable(GL11.GL_BLEND);
        Blending.DEFAULT.applyStateManager();
        Blending.DEFAULT.apply();
        GL11.glColor4f(1F, 1F, 1F, 1F);

        drawDefault(textureResShell, new Point(mouseX, mouseY));
        drawBackground(zLevel - 50);

        ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(
            (guiLeft + 27) * res.getScaleFactor(),
            (guiTop + 27) * res.getScaleFactor(),
            (guiWidth - 54) * res.getScaleFactor(),
            (guiHeight - 54) * res.getScaleFactor());
        drawPerkTree(partialTicks);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        drawSearchBox();
        drawMiscInfo(mouseX, mouseY, partialTicks);
        drawSocketContextMenu();
        drawSealBox();
        drawHoverTooltips(mouseX, mouseY);

        TextureHelper.refreshTextureBindState();
        TextureHelper.setActiveTextureToAtlasSprite();

        if (mouseSealStack != null && mouseSealStack.stackSize > 0) {
            // 1.7.10: renderItemAndEffectIntoGUI needs FontRenderer and renderEngine parameters
            this.itemRender.renderItemAndEffectIntoGUI(
                fontRendererObj,
                mc.renderEngine,
                this.mouseSealStack,
                mouseX - 8,
                mouseY - 8);
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        if (Minecraft.getMinecraft().thePlayer != null) {
            int count = ItemPerkSeal.getPlayerSealCount(Minecraft.getMinecraft().thePlayer);
            if (count > 0) {
                this.foundSeals = new ItemStack(ItemsAS.perkSeal, count);
            } else {
                this.foundSeals = null;
            }
        } else {
            this.foundSeals = null;
        }

        this.tickSealBreak--;
        if (this.tickSealBreak <= 0) {
            this.tickSealBreak = 0;
            this.sealBreakPrimed = null;
        }
    }

    private void drawSocketContextMenu() {
        this.rSocketMenu = null;
        this.slotsSocketMenu.clear();

        if (socketMenu != null) {
            AbstractPerk sMenuPerk = (AbstractPerk) socketMenu;
            Map<Integer, ItemStack> found = ItemUtils.findItemsIndexedInPlayerInventory(
                Minecraft.getMinecraft().thePlayer,
                s -> s != null && s.stackSize > 0
                    && s.getItem() instanceof ItemPerkGem
                    && !ItemPerkGem.getModifiers(s)
                        .isEmpty());
            if (found == null || found.isEmpty()) { // Close then.
                closeSocketMenu();
                return;
            }

            Point offset = sMenuPerk.getPoint()
                .getOffset();
            double x = this.sizeHandler.evRelativePosX(offset.x);
            double y = this.sizeHandler.evRelativePosY(offset.y);
            Point.Double scaledOffset = shift2DOffset(x, y);
            int offsetX = WrapMathHelper.floor(scaledOffset.x);
            int offsetY = WrapMathHelper.floor(scaledOffset.y);

            double scale = this.sizeHandler.getScalingFactor();

            int scaledSlotSize = (int) Math.round(18 * scale);

            int realWidth = Math.min(5, found.size());
            int realHeight = (found.size() / 5 + (found.size() % 5 == 0 ? 0 : 1));

            int width = realWidth * scaledSlotSize;
            int height = realHeight * scaledSlotSize;
            rSocketMenu = new Rectangle(
                (int) (offsetX + (12 * scale) - 4),
                (int) (offsetY - (12 * scale) - 4),
                width + 4,
                height + 4);

            if (!this.guiBox.isInBox(rSocketMenu.x - guiLeft, rSocketMenu.y - guiTop) || !this.guiBox
                .isInBox(rSocketMenu.x + rSocketMenu.width - guiLeft, rSocketMenu.y + rSocketMenu.height - guiTop)) {
                closeSocketMenu();
                return;
            }

            RenderingUtils.renderBlueTooltipBox(0, 0, realWidth * 18, realHeight * 18);

            offsetX += 12 * scale;
            offsetY -= 12 * scale;

            textureSlotContext.bindTexture();

            GL11.glColor4f(1F, 1F, 1F, 1F);
            GL11.glEnable(GL11.GL_BLEND);

            GL11.glDisable(GL11.GL_DEPTH_TEST);
            for (int index = 0; index < found.size(); index++) {
                int addedX = (index % 5) * scaledSlotSize;
                int addedY = (index / 5) * scaledSlotSize;
                drawTexturedRect(offsetX + addedX, offsetY + addedY, scaledSlotSize, scaledSlotSize, 0, 0, 1, 1);
            }

            TextureHelper.refreshTextureBindState();
            RenderHelper.enableGUIStandardItemLighting();

            int index = 0;
            for (Integer slotId : found.keySet()) {
                ItemStack stack = found.get(slotId);
                int addedX = (index % 5) * scaledSlotSize;
                int addedY = (index / 5) * scaledSlotSize;
                Rectangle r = new Rectangle(offsetX + addedX, offsetY + addedY, scaledSlotSize, scaledSlotSize);
                drawItemStack(stack, 0, 0);
                slotsSocketMenu.put(r, slotId);
                index++;
            }
        }
    }

    private void drawItemStack(ItemStack stack, int x, int y) {
        FontRenderer fr = stack.getItem()
            .getFontRenderer(stack);
        if (fr == null) fr = fontRendererObj;
        this.zLevel += 500;
        this.itemRender.zLevel += 500;
        // 1.7.10: renderItemAndEffectIntoGUI needs FontRenderer and renderEngine parameters
        this.itemRender.renderItemAndEffectIntoGUI(fr, mc.renderEngine, stack, x, y);
        // 1.7.10: renderItemOverlayIntoGUI needs 5 parameters
        this.itemRender.renderItemOverlayIntoGUI(fr, mc.renderEngine, stack, x, y);
        this.itemRender.zLevel -= 500;
        this.zLevel -= 500;
    }

    private void drawSealBox() {
        GL11.glColor4f(1F, 1F, 1F, 1F);

        AbstractRenderableTexture tex = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "gridslot");
        tex.bindTexture();
        drawTexturedRect(
            guiLeft + rectSealBox.x - 1,
            guiTop + rectSealBox.y - 1,
            rectSealBox.width + 2,
            rectSealBox.height + 2,
            tex);
        TextureHelper.refreshTextureBindState();
        TextureHelper.setActiveTextureToAtlasSprite();

        // 1.7.10: isEmpty() doesn't exist, check stackSize > 0
        if (this.foundSeals != null && this.foundSeals.stackSize > 0) {
            // 1.7.10: renderItemAndEffectIntoGUI needs FontRenderer and renderEngine parameters
            this.itemRender.renderItemAndEffectIntoGUI(
                Minecraft.getMinecraft().fontRenderer,
                mc.renderEngine,
                this.foundSeals,
                guiLeft + rectSealBox.x,
                guiTop + rectSealBox.y);
            // 1.7.10: renderItemOverlayIntoGUI needs 5 parameters
            this.itemRender.renderItemOverlayIntoGUI(
                Minecraft.getMinecraft().fontRenderer,
                mc.renderEngine,
                this.foundSeals,
                guiLeft + rectSealBox.x,
                guiTop + rectSealBox.y);
        }
    }

    private void drawMiscInfo(int mouseX, int mouseY, float pTicks) {
        PlayerProgress prog = ResearchManager.clientProgress;
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;

        int availablePerks;
        if (prog.getAttunedConstellation() != null && (availablePerks = prog.getAvailablePerkPoints(player)) > 0) {
            // 1.7.10: drawString doesn't have shadow parameter
            fontRendererObj
                .drawString(I18n.format("perk.info.points", availablePerks), 0, 0, new Color(0xCCCCCC).getRGB());

            GL11.glColor4f(1F, 1F, 1F, 1F);
            TextureHelper.refreshTextureBindState();
        }

        rStatStar = IGuiRenderablePage.GUI_INTERFACE.drawInfoStar(guiLeft + 288, guiTop + 20, zLevel + 100, 16, pTicks);
        if (rStatStar.contains(mouseX, mouseY)) {
            RenderingUtils
                .renderBlueTooltip(0, 0, Lists.newArrayList(I18n.format("perk.reader.infostar")), fontRendererObj);

            GL11.glColor4f(1F, 1F, 1F, 1F);
            TextureHelper.refreshTextureBindState();
            GL11.glEnable(GL11.GL_BLEND);
        }
    }

    private void drawSearchBox() {

        textureSearchTextBG.bindTexture();
        drawTexturedRectAtCurrentPos(88.5, 15);

        String text = this.searchTextEntry.getText();

        int length = fontRendererObj.getStringWidth(text);
        boolean addDots = length > 75;
        while (length > 75) {
            text = text.substring(1);
            length = fontRendererObj.getStringWidth("..." + text);
        }
        if (addDots) {
            text = "..." + text;
        }

        if ((ClientScheduler.getClientTick() % 20) > 10) {
            text += "_";
        }

        // 1.7.10: drawString doesn't have shadow parameter
        fontRendererObj.drawString(text, 0, 0, new Color(0xCCCCCC).getRGB());

        GL11.glColor4f(1F, 1F, 1F, 1F);
        TextureHelper.refreshTextureBindState();
    }

    private void rescaleMouse() {
        this.mousePosition.rescale(this.sizeHandler.getScalingFactor());
        if (this.previousMousePosition != null) {
            this.previousMousePosition.rescale(this.sizeHandler.getScalingFactor());
        }
        this.moveMouse(0, 0);
    }

    private void drawHoverTooltips(int mouseX, int mouseY) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;

        for (Rectangle r : this.slotsSocketMenu.keySet()) {
            if (r.contains(mouseX, mouseY)) {
                Integer slot = this.slotsSocketMenu.get(r);
                ItemStack in = player.inventory.getStackInSlot(slot);
                if (!(in == null || in.stackSize <= 0)) {
                    renderToolTip(in, mouseX, mouseY);
                }
                return;
            }
        }

        GL11.glDisable(GL11.GL_DEPTH_TEST);

        // 1.7.10: ItemStack == null || ItemStack.stackSize <= 0 doesn't exist, use stackSize check
        if (this.foundSeals != null && this.foundSeals.stackSize > 0
            && rectSealBox.contains(mouseX - guiLeft, mouseY - guiTop)) {
            List<String> toolTip = this.foundSeals.getTooltip(
                Minecraft.getMinecraft().thePlayer,
                Minecraft.getMinecraft().gameSettings.advancedItemTooltips);
            toolTip.add("");
            toolTip.add(EnumChatFormatting.GRAY + I18n.format("perk.info.sealed.usage"));
            RenderingUtils.renderBlueTooltip(mouseX, mouseY, toolTip, Minecraft.getMinecraft().fontRenderer);
            GL11.glColor4f(1F, 1F, 1F, 1F);
        } else {
            for (Map.Entry<AbstractPerk, Rectangle.Double> rctPerk : this.thisFramePerks.entrySet()) {
                if (rctPerk.getValue()
                    .contains(mouseX, mouseY) && this.guiBox.isInBox(mouseX - guiLeft, mouseY - guiTop)) {
                    List<String> toolTip = new LinkedList<>();
                    AbstractPerk perk = rctPerk.getKey();
                    PlayerProgress prog = ResearchManager.clientProgress;

                    for (String line : perk.getLocalizedTooltip()) {
                        toolTip.add(EnumChatFormatting.GRAY.toString() + EnumChatFormatting.ITALIC.toString() + line);
                    }

                    if (prog.isPerkSealed(perk)) {
                        toolTip.add(EnumChatFormatting.RED + I18n.format("perk.info.sealed"));
                        toolTip.add(EnumChatFormatting.RED + I18n.format("perk.info.sealed.break"));
                    } else if (prog.hasPerkUnlocked(perk)) {
                        toolTip.add(EnumChatFormatting.GREEN + I18n.format("perk.info.active"));
                    } else if (perk.mayUnlockPerk(prog, player)) {
                        toolTip.add(EnumChatFormatting.BLUE + I18n.format("perk.info.available"));
                    } else {
                        toolTip.add(EnumChatFormatting.GRAY + I18n.format("perk.info.locked"));
                    }

                    if (Minecraft.getMinecraft().gameSettings.advancedItemTooltips) {
                        String loc = perk.getCategory()
                            .getLocalizedName();
                        if (loc != null) {
                            toolTip.add(
                                EnumChatFormatting.GRAY.toString() + EnumChatFormatting.ITALIC.toString()
                                    + "["
                                    + loc
                                    + "]");
                        }
                    }
                    Collection<String> modInfo = perk.getSource();
                    if (modInfo != null) {
                        for (String line : modInfo) {
                            toolTip
                                .add(EnumChatFormatting.BLUE.toString() + EnumChatFormatting.ITALIC.toString() + line);
                        }
                    }
                    if (Minecraft.getMinecraft().gameSettings.showDebugInfo) {
                        toolTip.add("");
                        toolTip.add(
                            EnumChatFormatting.GRAY + perk.getRegistryName()
                                .toString());
                        toolTip.add(EnumChatFormatting.GRAY + I18n.format("misc.ctrlcopy"));
                    }
                    RenderingUtils.renderBlueTooltip(mouseX, mouseY, toolTip, Minecraft.getMinecraft().fontRenderer);
                    GL11.glColor4f(1F, 1F, 1F, 1F);
                    break;
                }
            }
        }

        GL11.glEnable(GL11.GL_ALPHA_TEST);
    }

    private void drawPerkTree(float partialTicks) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;

        GL11.glColor4f(1F, 1F, 1F, 1F);

        Tessellator connBuffer = Tessellator.instance;
        connBuffer.startDrawingQuads();
        texturePerkConnection.bindTexture();
        PlayerProgress progress = ResearchManager.getProgress(player, Side.CLIENT);

        for (Tuple<AbstractPerk, AbstractPerk> perkConnection : PerkTree.PERK_TREE.getConnections()) {
            PerkTreePoint.AllocationStatus status;

            int alloc = 0;
            if (progress.hasPerkUnlocked(perkConnection.key)) {
                alloc++;
            }
            if (progress.hasPerkUnlocked(perkConnection.value)) {
                alloc++;
            }
            if (alloc == 2) {
                status = PerkTreePoint.AllocationStatus.ALLOCATED;
            } else if (alloc == 1 && progress.hasFreeAllocationPoint(player)) {
                status = PerkTreePoint.AllocationStatus.UNLOCKABLE;
            } else {
                status = PerkTreePoint.AllocationStatus.UNALLOCATED;
            }

            Point offsetOne = perkConnection.key.getPoint()
                .getOffset();
            Point offsetTwo = perkConnection.value.getPoint()
                .getOffset();
            Point.Double shiftOne = this.sizeHandler.evRelativePos(offsetOne);
            Point.Double shiftTwo = this.sizeHandler.evRelativePos(offsetTwo);
            drawConnection(
                connBuffer,
                status,
                shiftOne,
                shiftTwo,
                ClientScheduler.getClientTick() + offsetOne.x + offsetOne.y + offsetTwo.x + offsetTwo.y);
        }
        drawBufferConnections.draw();

        drawBuffer.beginDrawingPerks();

        List<Runnable> renderDynamic = Lists.newArrayList();
        for (PerkTreePoint perkPoint : PerkTree.PERK_TREE.getPerkPoints()) {
            Point offset = perkPoint.getOffset();
            double x = this.sizeHandler.evRelativePosX(offset.x);
            double y = this.sizeHandler.evRelativePosY(offset.y);
            Rectangle.Double perkRect = drawPerk(
                drawBuffer,
                perkPoint,
                x,
                y,
                partialTicks,
                ClientScheduler.getClientTick() + offset.x + offset.y,
                progress.isPerkSealed(perkPoint.getPerk()),
                renderDynamic);
            if (perkRect != null) {
                this.thisFramePerks.put(perkPoint.getPerk(), perkRect);
            }
        }
        drawBuffer.draw();

        for (Runnable r : renderDynamic) {
            r.run();
        }

        Iterator<Map.Entry<AbstractPerk, Long>> itUnlock = this.unlockEffects.entrySet()
            .iterator();
        while (itUnlock.hasNext()) {
            Map.Entry<AbstractPerk, Long> entry = itUnlock.next();
            if (!drawPerkUnlock(entry.getKey(), entry.getValue())) {
                itUnlock.remove();
            }
        }
        Iterator<Map.Entry<AbstractPerk, Long>> itBreak = this.breakEffects.entrySet()
            .iterator();
        while (itBreak.hasNext()) {
            Map.Entry<AbstractPerk, Long> entry = itBreak.next();
            if (!drawPerkSealBreak(entry.getKey(), entry.getValue(), partialTicks)) {
                itBreak.remove();
            }
        }
        TextureHelper.refreshTextureBindState();
    }

    private boolean drawPerkSealBreak(AbstractPerk perk, long tick, float pTicks) {
        int count = (int) (ClientScheduler.getClientTick() - tick);
        SpriteSheetResource sprite = SpriteLibrary.spriteBurstEffect3;
        if (count >= sprite.getFrameCount()) {
            return false;
        }
        Point.Double oPos = this.sizeHandler.evRelativePos(perk.getOffset());
        Point.Double offset = shift2DOffset(oPos.x, oPos.y);

        GL11.glColor4f(1F, 1F, 1F, 1F);

        float sealFade = 1.0F - (((float) count) + pTicks) / ((float) sprite.getFrameCount());
        double width = 22;
        Rectangle.Double rct;
        if ((rct = thisFramePerks.get(perk)) != null) {
            width = rct.width;
        }
        width *= 0.75;

        Tessellator tes = Tessellator.instance;
        tes.startDrawingQuads();
        SpriteLibrary.spritePerkSeal.bindTexture();

        Point pOffset = perk.getPoint()
            .getOffset();
        drawSeal(tes, width, 0, 0, ClientScheduler.getClientTick() + pOffset.x + pOffset.y, sealFade * 0.75F);

        tes.draw();
        tes.startDrawingQuads();

        sprite.bindTexture();

        Vector3 starVec = new Vector3(-width, -width, 0);
        double uLength = sprite.getUWidth();
        double vLength = sprite.getVWidth();
        Tuple<Double, Double> off = sprite.getUVOffset(count);
        Point.Double frameUV = new Point.Double(off.key, off.value);

        for (int i = 0; i < 4; i++) {
            int u = ((i + 1) & 2) >> 1;
            int v = ((i + 2) & 2) >> 1;

            Vector3 pos = starVec.clone()
                .addX(width * u * 2)
                .addY(width * v * 2);
            tes.addVertexWithUV(pos.getX(), pos.getY(), pos.getZ(), frameUV.x + uLength * u, frameUV.y + vLength * v);
        }
        tes.draw();

        TextureHelper.refreshTextureBindState();
        return true;
    }

    private boolean drawPerkUnlock(AbstractPerk perk, long tick) {
        int count = (int) (ClientScheduler.getClientTick() - tick);
        SpriteSheetResource sprite = SpriteLibrary.spritePerkActivate;
        if (count >= sprite.getFrameCount()) {
            return false;
        }
        Point.Double oPos = this.sizeHandler.evRelativePos(perk.getOffset());
        Point.Double offset = shift2DOffset(oPos.x, oPos.y);

        double width = 22;
        Rectangle.Double rct;
        if ((rct = thisFramePerks.get(perk)) != null) {
            width = rct.width;
        }
        width *= 2.5;

        Tessellator tes = Tessellator.instance;
        tes.startDrawingQuads();
        sprite.bindTexture();

        Vector3 starVec = new Vector3(-width, -width, 0);
        double uLength = sprite.getUWidth();
        double vLength = sprite.getVWidth();
        Tuple<Double, Double> off = sprite.getUVOffset(count);
        Point.Double frameUV = new Point.Double(off.key, off.value);

        for (int i = 0; i < 4; i++) {
            int u = ((i + 1) & 2) >> 1;
            int v = ((i + 2) & 2) >> 1;

            Vector3 pos = starVec.clone()
                .addX(width * u * 2)
                .addY(width * v * 2);
            tes.addVertexWithUV(pos.getX(), pos.getY(), pos.getZ(), frameUV.x + uLength * u, frameUV.y + vLength * v);
        }

        tes.draw();

        TextureHelper.refreshTextureBindState();
        return true;
    }

    private void drawConnection(Tessellator tes, PerkTreePoint.AllocationStatus status, Point.Double offset,
        Point.Double target, long effectTick) {
        Point.Double offsetSrc = shift2DOffset(offset.x, offset.y);
        Point.Double offsetDst = shift2DOffset(target.x, target.y);
        Color overlay = Color.WHITE;
        switch (status) {
            case UNALLOCATED:
                overlay = new Color(0xBBBBFF);
                break;
            case ALLOCATED:
                overlay = new Color(0x00EEEE00);
                break;
            case UNLOCKABLE:
                overlay = new Color(0x0071FF);
                break;
            default:
                break;
        }

        double effectPart = (Math.sin(Math.toRadians(((effectTick) * 8) % 360D)) + 1D) / 4D;
        float br = 0.1F + 0.4F * (2F - ((float) effectPart));
        float rR = (overlay.getRed() / 255F) * br;
        float rG = (overlay.getGreen() / 255F) * br;
        float rB = (overlay.getBlue() / 255F) * br;
        float rA = (overlay.getAlpha() / 255F) * br;

        Vector3 fromStar = new Vector3(offsetSrc.x, offsetSrc.y, 0);
        Vector3 toStar = new Vector3(offsetDst.x, offsetDst.y, 0);

        double width = 4.0D * this.sizeHandler.getScalingFactor();

        Vector3 dir = toStar.clone()
            .subtract(fromStar);
        Vector3 degLot = dir.clone()
            .crossProduct(new Vector3(0, 0, 1))
            .normalize()
            .multiply(width);// .multiply(j == 0 ? 1 : -1);

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
            // 1.7.10: setColorRGBA_F doesn't exist, use setColorRGBA_I with int conversion
            int rgb = ((int) (rR * 255) << 16) | ((int) (rG * 255) << 8) | (int) (rB * 255);
            tes.setColorRGBA_I(rgb, (int) (rA * 255));
            tes.addVertexWithUV(pos.getX(), pos.getY(), pos.getZ(), u, v);
        }
    }

    @Nullable
    private Rectangle.Double drawPerk(BatchPerkContext ctx, PerkTreePoint perkPoint, double lowX, double lowY,
        float pTicks, long effectTick, boolean renderSeal, Collection<Runnable> outRenderDynamic) {
        Point.Double offset = shift2DOffset(lowX, lowY);

        double scale = this.sizeHandler.getScalingFactor();
        PerkTreePoint.AllocationStatus status = perkPoint.getPerk()
            .getPerkStatus(Minecraft.getMinecraft().thePlayer, Side.CLIENT);

        Rectangle.Double drawSize = perkPoint
            .renderPerkAtBatch(ctx, status, effectTick, pTicks, offset.x, offset.y, scale);

        if (perkPoint instanceof DynamicPerkRender) {
            outRenderDynamic.add(
                () -> ((DynamicPerkRender) perkPoint).renderAt(status, effectTick, pTicks, offset.x, offset.y, scale));
        }

        if (drawSize == null) {
            return null;
        }

        if (renderSeal) {
            this.drawSeal(ctx, drawSize.width * 0.75, offset.x, offset.y, effectTick);
        }

        if (this.searchMatches.contains(perkPoint.getPerk())) {
            drawSearchMarkHalo(ctx, drawSize, offset.x, offset.y);
        }

        double mapDrawSize = 28;
        if (perkPoint.getPerk() instanceof AttributeConverterPerk) {
            for (PerkConverter converter : ((AttributeConverterPerk) perkPoint.getPerk())
                .provideConverters(Minecraft.getMinecraft().thePlayer, Side.CLIENT)) {
                if (converter instanceof PerkConverter.Radius) {
                    double radius = ((PerkConverter.Radius) converter).getRadius();

                    // TODO rework design/visuals
                    drawSearchHalo(ctx, mapDrawSize * radius * scale, offset.x, offset.y);
                }
            }
        }

        return new Rectangle.Double(
            offset.x - (drawSize.width / 2),
            offset.y - (drawSize.height / 2),
            drawSize.width,
            drawSize.height);
    }

    private void drawSeal(BatchPerkContext ctx, double size, double x, double y, long spriteOffsetTick) {
        BufferBatch batch = ctx.getContext(sealContext);
        Tessellator vb = Tessellator.instance;
        drawSeal(vb, size, x, y, spriteOffsetTick, 1F);
    }

    private void drawSeal(Tessellator vb, double size, double x, double y, long spriteOffsetTick, float alpha) {
        SpriteSheetResource tex = SpriteLibrary.spritePerkSeal;
        if (tex == null) {
            return;
        }

        double uLength = tex.getULength();
        double vLength = tex.getVLength();
        Tuple<Double, Double> frameUV = tex.getUVOffset(spriteOffsetTick);
        Vector3 starVec = new Vector3(x - size, y - size, 0);

        for (int i = 0; i < 4; i++) {
            int u = ((i + 1) & 2) >> 1;
            int v = ((i + 2) & 2) >> 1;

            Vector3 pos = starVec.clone()
                .addX(size * u * 2)
                .addY(size * v * 2);
            // 1.7.10: setColorRGBA_F doesn't exist, use setColorRGBA_I with int conversion
            int rgb = (255 << 16) | (255 << 8) | 255;
            vb.setColorRGBA_I(rgb, (int) (alpha * 255));
            vb.addVertexWithUV(
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                frameUV.key + uLength * u,
                frameUV.value + vLength * v);
        }
    }

    private void drawSearchMarkHalo(BatchPerkContext ctx, Rectangle.Double draw, double x, double y) {
        drawSearchHalo(ctx, draw.width, x, y);
    }

    private void drawSearchHalo(BatchPerkContext ctx, double size, double x, double y) {
        BufferBatch batch = ctx.getContext(searchContext);
        Tessellator vb = Tessellator.instance;
        vb.startDrawingQuads();

        Vector3 starVec = new Vector3(x - size, y - size, 0);
        double uLength = textureSearchMark.getUWidth();
        double vLength = textureSearchMark.getVWidth();
        Point.Double frameUV = textureSearchMark.getUVOffset();

        for (int i = 0; i < 4; i++) {
            int u = ((i + 1) & 2) >> 1;
            int v = ((i + 2) & 2) >> 1;

            Vector3 pos = starVec.clone()
                .addX(size * u * 2)
                .addY(size * v * 2);
            // 1.7.10: setColorRGBA_F doesn't exist, use setColorRGBA_I with int conversion
            int rgb = ((int) (0.8F * 255) << 16) | ((int) (0.1F * 255) << 8) | (int) (0.1F * 255);
            vb.setColorRGBA_I(rgb, 255);
            vb.addVertexWithUV(pos.getX(), pos.getY(), pos.getZ(), frameUV.x + uLength * u, frameUV.y + vLength * v);
        }
    }

    private Point.Double shift2DOffset(double x, double y) {
        double scaledLeft = this.mousePosition.getScaledPosX() - sizeHandler.widthToBorder;
        double scaledTop = this.mousePosition.getScaledPosY() - sizeHandler.heightToBorder;
        double xAdd = x - scaledLeft;
        double yAdd = y - scaledTop;
        double offsetX = guiOffsetX + xAdd;
        double offsetY = guiOffsetY + yAdd;
        return new Point.Double(offsetX, offsetY);
    }

    private void handleMouseMovement(int mouseX, int mouseY) {
        int guiMouseX = mouseX - guiLeft;
        int guiMouseY = mouseY - guiTop;

        if (mouseSealStack == null
            || mouseSealStack.stackSize <= 0 && Mouse.isButtonDown(0) && guiBox.isInBox(guiMouseX, guiMouseY)) {
            if (mouseDragging) {
                moveMouse(-(guiMouseX - mouseBufferX), -(guiMouseY - mouseBufferY));
            } else {
                mouseBufferX = guiMouseX;
                mouseBufferY = guiMouseY;
                mouseDragging = true;
            }
        } else {
            applyMovedMouseOffset();
            mouseDragging = false;
        }
    }

    private void moveMouse(int changeX, int changeY) {
        if (this.previousMousePosition != null) {
            mousePosition.updateScaledPos(
                sizeHandler.clampX(previousMousePosition.getScaledPosX() + changeX),
                sizeHandler.clampY(previousMousePosition.getScaledPosY() + changeY),
                sizeHandler.getScalingFactor());
        } else {
            mousePosition.updateScaledPos(
                sizeHandler.clampX(changeX),
                sizeHandler.clampY(changeY),
                sizeHandler.getScalingFactor());
        }
    }

    private void applyMovedMouseOffset() {
        this.previousMousePosition = ScalingPoint.createPoint(
            this.mousePosition.getScaledPosX(),
            this.mousePosition.getScaledPosY(),
            this.sizeHandler.getScalingFactor(),
            true);
    }

    private void drawBackground(float zLevel) {
        float br = 0.8F;

        textureResBack.bindTexture();
        Tessellator vb = Tessellator.instance;
        vb.startDrawingQuads();
        vb.addVertexWithUV(guiLeft + 10, guiTop - 10 + guiHeight, zLevel, 0, 1);
        vb.addVertexWithUV(guiLeft - 10 + guiWidth, guiTop - 10 + guiHeight, zLevel, 1, 1);
        vb.addVertexWithUV(guiLeft - 10 + guiWidth, guiTop + 10, zLevel, 1, 0);
        vb.addVertexWithUV(guiLeft + 10, guiTop + 10, zLevel, 0, 0);
        vb.draw();

    }

    private void updateSearchHighlight() {
        this.searchMatches.clear();

        String matchText = this.searchTextEntry.getText()
            .toLowerCase();
        if (matchText.length() < 3) return;
        for (PerkTreePoint point : PerkTree.PERK_TREE.getPerkPoints()) {
            AbstractPerk perk = point.getPerk();
            if (perk instanceof ProgressGatedPerk && !((ProgressGatedPerk) perk).canSeeClient()) {
                continue;
            }
            String catStr = perk.getCategory()
                .getLocalizedName();
            if (catStr != null && catStr.toLowerCase()
                .contains(matchText)) {
                this.searchMatches.add(perk);
            } else {
                // 1.7.10: getLocalizedTooltip returns Collection, need to convert to List or use Collection
                Collection<String> tooltips = perk.getLocalizedTooltip();
                for (String tooltip : tooltips) {
                    if (tooltip.toLowerCase()
                        .contains(matchText)) {
                        this.searchMatches.add(perk);
                        break;
                    }
                }
            }
        }
        if (I18n.format("perk.info.sealed")
            .toLowerCase()
            .contains(matchText)) {
            PlayerProgress prog = ResearchManager.clientProgress;
            for (AbstractPerk sealed : prog.getSealedPerks()) {
                if (!this.searchMatches.contains(sealed)) {
                    this.searchMatches.add(sealed);
                }
            }
        }
    }

    private void closeSocketMenu() {
        this.socketMenu = null;
        this.rSocketMenu = null;
        this.slotsSocketMenu.clear();
    }

    // 1.7.10: mouseReleased doesn't have @Override in GuiScreen
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        // 1.7.10: No super.mouseReleased in GuiScreen

        EntityPlayer player = Minecraft.getMinecraft().thePlayer;

        // 1.7.10: Fixed logic operator
        if (mouseSealStack == null || mouseSealStack.stackSize <= 0) {
            this.mouseSealStack = null;
            if (Minecraft.getMinecraft().thePlayer == null) {
                return;
            }

            PlayerProgress prog = ResearchManager.clientProgress;
            for (Map.Entry<AbstractPerk, Rectangle.Double> rctPerk : this.thisFramePerks.entrySet()) {
                if (rctPerk.getValue()
                    .contains(mouseX, mouseY) && this.guiBox.isInBox(mouseX - guiLeft, mouseY - guiTop)) {
                    if (prog.hasPerkUnlocked(rctPerk.getKey()) && !prog.isPerkSealed(rctPerk.getKey())
                        && ItemPerkSeal.useSeal(player, true)) {
                        PktRequestPerkSealAction pkt = new PktRequestPerkSealAction(rctPerk.getKey(), true);
                        PacketChannel.CHANNEL.sendToServer(pkt);
                        break;
                    }
                }
            }
            return;
        }

        if (this.unlockPrimed == null) {
            return;
        }

        for (Map.Entry<AbstractPerk, Rectangle.Double> rctPerk : this.thisFramePerks.entrySet()) {
            if (this.unlockPrimed.equals(rctPerk.getKey()) && rctPerk.getValue()
                .contains(mouseX, mouseY) && this.guiBox.isInBox(mouseX - guiLeft, mouseY - guiTop)) {
                if (rctPerk.getKey()
                    .mayUnlockPerk(ResearchManager.clientProgress, player)) {
                    PktUnlockPerk pkt = new PktUnlockPerk(false, rctPerk.getKey());
                    PacketChannel.CHANNEL.sendToServer(pkt);
                    break;
                }
            }
        }

        this.unlockPrimed = null;
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);

        this.unlockPrimed = null;
    }

    @Override
    protected boolean handleRightClickClose(int mouseX, int mouseY) {
        if (rectSearchTextEntry.contains(mouseX - guiLeft, mouseY - guiTop)) {
            searchTextEntry.setText("");
            return true;
        }
        if (socketMenu != null && rSocketMenu != null && !rSocketMenu.contains(mouseX, mouseY)) {
            closeSocketMenu();
            return true;
        }

        for (Map.Entry<AbstractPerk, Rectangle.Double> rctPerk : this.thisFramePerks.entrySet()) {
            if (rctPerk.getValue()
                .contains(mouseX, mouseY) && this.guiBox.isInBox(mouseX - guiLeft, mouseY - guiTop)) {
                AbstractPerk perk = rctPerk.getKey();
                if (perk instanceof GemSlotPerk) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        Point p = new Point(mouseX, mouseY);

        if (socketMenu != null && (mouseButton == 0 || mouseButton == 1)
            && rSocketMenu != null
            && !rSocketMenu.contains(p)) {
            closeSocketMenu();
        }

        if (mouseButton == 0) {
            if (socketMenu != null) {
                for (Rectangle r : slotsSocketMenu.keySet()) {
                    if (r.contains(p) && !socketMenu.hasItem(Minecraft.getMinecraft().thePlayer, Side.CLIENT)) {
                        int slotId = slotsSocketMenu.get(r);
                        ItemStack potentialStack = Minecraft.getMinecraft().thePlayer.inventory.getStackInSlot(slotId);
                        if (!(potentialStack == null || potentialStack.stackSize <= 0)
                            && !ItemPerkGem.getModifiers(potentialStack)
                                .isEmpty()) {
                            PktPerkGemModification pkt = PktPerkGemModification
                                .insertItem((AbstractPerk) socketMenu, slotId);
                            PacketChannel.CHANNEL.sendToServer(pkt);
                            closeSocketMenu();
                            SoundHelper
                                .playSoundClient(null /* TODO: SoundEvents - needs 1.7.10 sound string */, .35F, 9f);
                        }
                        return;
                    }
                }
            }

            if (handleBookmarkClick(p)) {
                return;
            }

            if (rectSealBox.contains(mouseX - guiLeft, mouseY - guiTop)) {
                // 1.7.10: ItemStack == null || ItemStack.stackSize <= 0 doesn't exist, use stackSize check
                if (this.foundSeals != null && this.foundSeals.stackSize > 0) {
                    this.mouseSealStack = new ItemStack(ItemsAS.perkSeal);
                }
                return;
            }

            if (rStatStar.contains(mouseX, mouseY)) {
                this.expectReinit = true;
                Minecraft.getMinecraft()
                    .displayGuiScreen(new GuiJournalOverlayPerkStats(this));

                return;
            }
        }

        PlayerProgress prog = ResearchManager.clientProgress;
        for (Map.Entry<AbstractPerk, Rectangle.Double> rctPerk : this.thisFramePerks.entrySet()) {
            if (rctPerk.getValue()
                .contains(mouseX, mouseY) && this.guiBox.isInBox(mouseX - guiLeft, mouseY - guiTop)) {
                AbstractPerk perk = rctPerk.getKey();
                if (mouseButton == 0 && Minecraft.getMinecraft().gameSettings.showDebugInfo && isCtrlKeyDown()) {
                    String perkKey = perk.getRegistryName()
                        .toString();
                    GuiScreen.setClipboardString(perkKey);
                    // 1.7.10: Use StatCollector.translateToLocal instead of ChatComponentTranslation
                    Minecraft.getMinecraft().thePlayer.addChatMessage(
                        new net.minecraft.util.ChatComponentText(
                            StatCollector.translateToLocalFormatted("misc.ctrlcopy.copied", perkKey)));
                    break;
                }
                if (mouseButton == 1) {
                    if (prog.hasPerkEffect(perk) && perk instanceof GemSlotPerk) {
                        if (((GemSlotPerk) perk).hasItem(Minecraft.getMinecraft().thePlayer, Side.CLIENT)) {
                            PktPerkGemModification pkt = PktPerkGemModification.dropItem(perk);
                            PacketChannel.CHANNEL.sendToServer(pkt);
                            AstralSorcery.proxy.scheduleClientside(() -> {
                                if (Minecraft.getMinecraft().currentScreen == this) { // Only if user hasn't closed
                                    updateSearchHighlight();
                                }
                            }, 10);
                            SoundHelper
                                .playSoundClient(null /* TODO: SoundEvents - needs 1.7.10 sound string */, .35F, 9f);
                            return;
                        } else {
                            this.socketMenu = (GemSlotPerk) perk;
                            return;
                        }
                    }
                } else if (mouseButton == 0) {
                    if (perk.handleMouseClick(this, mouseX, mouseY)) {
                        return;
                    }

                    if (!prog.hasPerkUnlocked(perk) && perk.mayUnlockPerk(prog, Minecraft.getMinecraft().thePlayer)) {
                        this.unlockPrimed = perk;
                        break;
                    } else if (this.sealBreakPrimed != null && this.tickSealBreak > 0) {
                        PktRequestPerkSealAction pkt = new PktRequestPerkSealAction(perk, false);
                        PacketChannel.CHANNEL.sendToServer(pkt);
                        return;
                    } else if (prog.isPerkSealed(perk)) {
                        this.sealBreakPrimed = perk;
                        this.tickSealBreak = 4;
                    }
                }
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        super.keyTyped(typedChar, keyCode);

        if (keyCode != Keyboard.KEY_ESCAPE) {
            searchTextEntry.textboxKeyTyped(typedChar, keyCode);
        }
    }

    public void playUnlockAnimation(AbstractPerk perk) {
        this.unlockEffects.put(perk, ClientScheduler.getClientTick());
    }

    public void playSealBreakAnimation(AbstractPerk perk) {
        this.updateSearchHighlight();
        this.breakEffects.put(perk, ClientScheduler.getClientTick());
    }

}
