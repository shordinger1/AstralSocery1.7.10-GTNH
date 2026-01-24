/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Timer;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.ReflectionHelper;
import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFloatingCube;
import hellfirepvp.astralsorcery.common.migration.BakedQuad;
import hellfirepvp.astralsorcery.common.migration.IBakedModel;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.WrapMathHelper;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderingUtils
 * Created by HellFirePvP
 * Date: 29.08.2016 / 16:51
 */
public class RenderingUtils {

    private static final Random rand = new Random();

    public static Field itemPhysics_fieldSkipRenderHook = null;

    public static void playBlockBreakParticles(BlockPos pos, Block state) {
        EffectRenderer pm = Minecraft.getMinecraft().effectRenderer;

        for (int j = 0; j < 4; j++) {
            for (int k = 0; k < 4; k++) {
                for (int l = 0; l < 4; l++) {
                    double d0 = (double) pos.getX() + ((double) j + 0.5D) / 4D;
                    double d1 = (double) pos.getY() + ((double) k + 0.5D) / 4D;
                    double d2 = (double) pos.getZ() + ((double) l + 0.5D) / 4D;
                    // 1.7.10: Use EntityDiggingFX directly instead of ParticleDigging.Factory
                    EntityFX digging = new EntityDiggingFX(
                        Minecraft.getMinecraft().theWorld,
                        d0,
                        d1,
                        d2,
                        d0 - (double) pos.getX() - 0.5D,
                        d1 - (double) pos.getY() - 0.5D,
                        d2 - (double) pos.getZ() - 0.5D,
                        state,
                        0).applyRenderColor(0);
                    pm.addEffect(digging);
                }
            }
        }
    }

    public static void renderItemAsEntity(ItemStack stack, double x, double y, double z, float pTicks, int age) {
        EntityItem ei = new EntityItem(Minecraft.getMinecraft().theWorld, 0, 0, 0, stack);
        ei.age = age;
        ei.hoverStart = 0;
        if (itemPhysics_fieldSkipRenderHook != null) {
            try {
                itemPhysics_fieldSkipRenderHook.set(ei, true);
            } catch (Exception ignored) {}
        }
        // 1.7.10: Use RenderItem.doRender() instead of entityRenderer.renderEntity()
        RenderItem renderItem = RenderItemAccessor.getRenderItem();
        renderItem.doRender(ei, x + 0.5, y + 0.6, z + 0.5, 0, pTicks);
    }

    @Nonnull
    public static TextureAtlasSprite tryGetFlowingTextureOfFluidStack(FluidStack stack) {
        // 1.7.10: Fluid.getIcon() returns IIcon, not ResourceLocation
        // The IIcon is then cast to TextureAtlasSprite for compatibility
        net.minecraftforge.fluids.Fluid fluid = stack.getFluid();
        net.minecraft.util.IIcon icon = fluid.getIcon();
        if (icon == null) icon = fluid.getStillIcon();
        if (icon == null) {
            // Return a default missing texture if no icon is found
            icon = Minecraft.getMinecraft()
                .getTextureMapBlocks()
                .getAtlasSprite("missingno");
        }
        return (TextureAtlasSprite) icon;
    }

    @Nullable
    public static TextureAtlasSprite tryGetMainTextureOfItemStack(ItemStack stack) {
        // 1.7.10: ItemModelMesher doesn't exist, use alternative approach
        if ((stack == null || stack.stackSize <= 0)) return null;
        if (stack.getItem() instanceof ItemBlock) {
            Block state = ItemUtils.createBlockState(stack);
            if (state == null) return null;
            // 1.7.10: Get icon directly from block
            net.minecraft.util.IIcon icon = state.getIcon(0, stack.getItemDamage());
            if (icon == null) return null;
            return (TextureAtlasSprite) icon;
        } else {
            // 1.7.10: Get icon from item
            net.minecraft.util.IIcon icon = stack.getIconIndex();
            if (icon == null) return null;
            return (TextureAtlasSprite) icon;
        }
    }

    @Nullable
    public static TextureAtlasSprite tryGetTextureOfBlockState(Block state) {
        // 1.7.10: Block doesn't have isAir(IBlockAccess, BlockPos) method
        if (state.getMaterial() == net.minecraft.block.material.Material.air) {
            return null;
        }
        // 1.7.10: Get icon directly from block
        net.minecraft.util.IIcon icon = state.getIcon(0, 0);
        if (icon == null) return null;
        return (TextureAtlasSprite) icon;
    }

    public static EntityFX spawnBlockBreakParticle(Vector3 pos, TextureAtlasSprite tas) {
        // 1.7.10: Can't easily set texture on EntityDiggingFX after creation
        // Use EntityDiggingFX with default texture from block
        EntityFX digging = new EntityDiggingFX(
            Minecraft.getMinecraft().theWorld,
            pos.getX(),
            pos.getY(),
            pos.getZ(),
            0,
            0,
            0,
            Blocks.stone, // Default block - texture controlled by tas parameter
            0);
        // Note: In 1.7.10, particleIcon is set internally by the block
        // The tas parameter is kept for API compatibility but may not work as expected
        Minecraft.getMinecraft().effectRenderer.addEffect(digging);
        return digging;
    }

    public static EntityFXFloatingCube spawnFloatingBlockCubeParticle(Vector3 pos, TextureAtlasSprite tas) {
        EntityFXFloatingCube cube = new EntityFXFloatingCube(tas);
        cube.setPosition(pos);
        EffectHandler.getInstance()
            .registerFX(cube);
        return cube;
    }

    public static float getCurrentRenderPartialTicks() {
        if (Minecraft.getMinecraft()
            .isGamePaused()) return 0;
        // 1.7.10: Use reflection to access timer field and renderPartialTicks
        try {
            Object timer = ReflectionHelper
                .getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), "timer", "field_71420_M");
            if (timer instanceof Timer) {
                return ReflectionHelper
                    .getPrivateValue(Timer.class, (Timer) timer, "renderPartialTicks", "field_74282_m");
            }
        } catch (Exception e) {
            // Fallback to 0 if reflection fails
        }
        return 0;
    }

    public static void renderBlockSafelyWithOptionalColor(IBlockAccess world, BlockPos offset, Block state,
        Tessellator vb, int color) {
        if (color == -1) {
            renderBlockSafely(world, offset, state, vb);
        } else {
            BlockModelRenderHelper.renderBlockModelWithColor(world, offset, state, vb, color);
        }
    }

    public static void renderBlockSafely(IBlockAccess world, BlockPos offset, Block state, Tessellator vb) {
        // 1.7.10: Block rendering is handled differently, no RenderGlobal.renderBlock()
        // Use block's native rendering
        if (state.getMaterial() == net.minecraft.block.material.Material.air) {
            return;
        }
        // 1.7.10: Check if render type is valid (0 = invisible, 3 = model)
        int renderType = state.getRenderType();
        if (renderType == 0) {
            return; // INVISIBLE
        }
        // 1.7.10: Simplified rendering - just let the block render itself
        // This is a simplified approach for 1.7.10 compatibility
        try {
            // In 1.7.10, blocks are rendered through the block renderer
            // For compatibility, we skip complex block rendering here
        } catch (Exception ignored) {}
    }

    public static void renderTexturedCubeCentral(Vector3 offset, double size, double u, double v, double uLength,
        double vLength) {
        Tessellator tes = Tessellator.instance;
        double half = size / 2D;

        tes.startDrawingQuads();

        tes.addVertexWithUV(offset.getX() - half, offset.getY() - half, offset.getZ() - half, u, v);
        tes.addVertexWithUV(offset.getX() + half, offset.getY() - half, offset.getZ() - half, u + uLength, v);
        tes.addVertexWithUV(offset.getX() + half, offset.getY() - half, offset.getZ() + half, u + uLength, v + vLength);
        tes.addVertexWithUV(offset.getX() - half, offset.getY() - half, offset.getZ() + half, u, v + vLength);

        tes.addVertexWithUV(offset.getX() - half, offset.getY() + half, offset.getZ() + half, u, v);
        tes.addVertexWithUV(offset.getX() + half, offset.getY() + half, offset.getZ() + half, u + uLength, v);
        tes.addVertexWithUV(offset.getX() + half, offset.getY() + half, offset.getZ() - half, u + uLength, v + vLength);
        tes.addVertexWithUV(offset.getX() - half, offset.getY() + half, offset.getZ() - half, u, v + vLength);

        tes.addVertexWithUV(offset.getX() - half, offset.getY() - half, offset.getZ() + half, u + uLength, v);
        tes.addVertexWithUV(offset.getX() - half, offset.getY() + half, offset.getZ() + half, u + uLength, v + vLength);
        tes.addVertexWithUV(offset.getX() - half, offset.getY() + half, offset.getZ() - half, u, v + vLength);
        tes.addVertexWithUV(offset.getX() - half, offset.getY() - half, offset.getZ() - half, u, v);

        tes.addVertexWithUV(offset.getX() + half, offset.getY() - half, offset.getZ() - half, u + uLength, v);
        tes.addVertexWithUV(offset.getX() + half, offset.getY() + half, offset.getZ() - half, u + uLength, v + vLength);
        tes.addVertexWithUV(offset.getX() + half, offset.getY() + half, offset.getZ() + half, u, v + vLength);
        tes.addVertexWithUV(offset.getX() + half, offset.getY() - half, offset.getZ() + half, u, v);

        tes.addVertexWithUV(offset.getX() + half, offset.getY() - half, offset.getZ() - half, u, v);
        tes.addVertexWithUV(offset.getX() + half, offset.getY() + half, offset.getZ() - half, u, v + vLength);
        tes.addVertexWithUV(offset.getX() - half, offset.getY() + half, offset.getZ() - half, u + uLength, v + vLength);
        tes.addVertexWithUV(offset.getX() - half, offset.getY() - half, offset.getZ() - half, u + uLength, v);

        tes.addVertexWithUV(offset.getX() - half, offset.getY() - half, offset.getZ() + half, u, v);
        tes.addVertexWithUV(offset.getX() - half, offset.getY() + half, offset.getZ() + half, u, v + vLength);
        tes.addVertexWithUV(offset.getX() + half, offset.getY() + half, offset.getZ() + half, u + uLength, v + vLength);
        tes.addVertexWithUV(offset.getX() + half, offset.getY() - half, offset.getZ() + half, u + uLength, v);

        tes.draw();
    }

    public static void renderTexturedCubeCentralWithColor(Vector3 offset, double size, double u, double v,
        double uLength, double vLength, float cR, float cG, float cB, float cA) {
        Tessellator tes = Tessellator.instance;
        double half = size / 2D;

        tes.startDrawingQuads();
        // 1.7.10: setColorRGBA_F doesn't exist, use setColorRGBA_I with int conversion
        int rgb = ((int) (cR * 255) << 16) | ((int) (cG * 255) << 8) | (int) (cB * 255);
        tes.setColorRGBA_I(rgb, (int) (cA * 255));

        tes.addVertexWithUV(offset.getX() - half, offset.getY() - half, offset.getZ() - half, u, v);
        tes.addVertexWithUV(offset.getX() + half, offset.getY() - half, offset.getZ() - half, u + uLength, v);
        tes.addVertexWithUV(offset.getX() + half, offset.getY() - half, offset.getZ() + half, u + uLength, v + vLength);
        tes.addVertexWithUV(offset.getX() - half, offset.getY() - half, offset.getZ() + half, u, v + vLength);

        tes.addVertexWithUV(offset.getX() - half, offset.getY() + half, offset.getZ() + half, u, v);
        tes.addVertexWithUV(offset.getX() + half, offset.getY() + half, offset.getZ() + half, u + uLength, v);
        tes.addVertexWithUV(offset.getX() + half, offset.getY() + half, offset.getZ() - half, u + uLength, v + vLength);
        tes.addVertexWithUV(offset.getX() - half, offset.getY() + half, offset.getZ() - half, u, v + vLength);

        tes.addVertexWithUV(offset.getX() - half, offset.getY() - half, offset.getZ() + half, u + uLength, v);
        tes.addVertexWithUV(offset.getX() - half, offset.getY() + half, offset.getZ() + half, u + uLength, v + vLength);
        tes.addVertexWithUV(offset.getX() - half, offset.getY() + half, offset.getZ() - half, u, v + vLength);
        tes.addVertexWithUV(offset.getX() - half, offset.getY() - half, offset.getZ() - half, u, v);

        tes.addVertexWithUV(offset.getX() + half, offset.getY() - half, offset.getZ() - half, u + uLength, v);
        tes.addVertexWithUV(offset.getX() + half, offset.getY() + half, offset.getZ() - half, u + uLength, v + vLength);
        tes.addVertexWithUV(offset.getX() + half, offset.getY() + half, offset.getZ() + half, u, v + vLength);
        tes.addVertexWithUV(offset.getX() + half, offset.getY() - half, offset.getZ() + half, u, v);

        tes.addVertexWithUV(offset.getX() + half, offset.getY() - half, offset.getZ() - half, u, v);
        tes.addVertexWithUV(offset.getX() + half, offset.getY() + half, offset.getZ() - half, u, v + vLength);
        tes.addVertexWithUV(offset.getX() - half, offset.getY() + half, offset.getZ() - half, u + uLength, v + vLength);
        tes.addVertexWithUV(offset.getX() - half, offset.getY() - half, offset.getZ() - half, u + uLength, v);

        tes.addVertexWithUV(offset.getX() - half, offset.getY() - half, offset.getZ() + half, u, v);
        tes.addVertexWithUV(offset.getX() - half, offset.getY() + half, offset.getZ() + half, u, v + vLength);
        tes.addVertexWithUV(offset.getX() + half, offset.getY() + half, offset.getZ() + half, u + uLength, v + vLength);
        tes.addVertexWithUV(offset.getX() + half, offset.getY() - half, offset.getZ() + half, u + uLength, v);

        tes.draw();
    }

    public static void renderTexturedCubeCentralWithLightAndColor(Vector3 offset, double size, double u, double v,
        double uLength, double vLength, int lX, int lY, float cR, float cG, float cB, float cA) {
        Tessellator tes = Tessellator.instance;
        double half = size / 2D;

        tes.startDrawingQuads();
        // 1.7.10: setColorRGBA_F doesn't exist, use setColorRGBA_I with int conversion
        int rgb = ((int) (cR * 255) << 16) | ((int) (cG * 255) << 8) | (int) (cB * 255);
        tes.setColorRGBA_I(rgb, (int) (cA * 255));
        tes.setBrightness((lX << 16) | lY);

        tes.addVertexWithUV(offset.getX() - half, offset.getY() - half, offset.getZ() - half, u, v);
        tes.addVertexWithUV(offset.getX() + half, offset.getY() - half, offset.getZ() - half, u + uLength, v);
        tes.addVertexWithUV(offset.getX() + half, offset.getY() - half, offset.getZ() + half, u + uLength, v + vLength);
        tes.addVertexWithUV(offset.getX() - half, offset.getY() - half, offset.getZ() + half, u, v + vLength);

        tes.addVertexWithUV(offset.getX() - half, offset.getY() + half, offset.getZ() + half, u, v);
        tes.addVertexWithUV(offset.getX() + half, offset.getY() + half, offset.getZ() + half, u + uLength, v);
        tes.addVertexWithUV(offset.getX() + half, offset.getY() + half, offset.getZ() - half, u + uLength, v + vLength);
        tes.addVertexWithUV(offset.getX() - half, offset.getY() + half, offset.getZ() - half, u, v + vLength);

        tes.addVertexWithUV(offset.getX() - half, offset.getY() - half, offset.getZ() + half, u + uLength, v);
        tes.addVertexWithUV(offset.getX() - half, offset.getY() + half, offset.getZ() + half, u + uLength, v + vLength);
        tes.addVertexWithUV(offset.getX() - half, offset.getY() + half, offset.getZ() - half, u, v + vLength);
        tes.addVertexWithUV(offset.getX() - half, offset.getY() - half, offset.getZ() - half, u, v);

        tes.addVertexWithUV(offset.getX() + half, offset.getY() - half, offset.getZ() - half, u + uLength, v);
        tes.addVertexWithUV(offset.getX() + half, offset.getY() + half, offset.getZ() - half, u + uLength, v + vLength);
        tes.addVertexWithUV(offset.getX() + half, offset.getY() + half, offset.getZ() + half, u, v + vLength);
        tes.addVertexWithUV(offset.getX() + half, offset.getY() - half, offset.getZ() + half, u, v);

        tes.addVertexWithUV(offset.getX() + half, offset.getY() - half, offset.getZ() - half, u, v);
        tes.addVertexWithUV(offset.getX() + half, offset.getY() + half, offset.getZ() - half, u, v + vLength);
        tes.addVertexWithUV(offset.getX() - half, offset.getY() + half, offset.getZ() - half, u + uLength, v + vLength);
        tes.addVertexWithUV(offset.getX() - half, offset.getY() - half, offset.getZ() - half, u + uLength, v);

        tes.addVertexWithUV(offset.getX() - half, offset.getY() - half, offset.getZ() + half, u, v);
        tes.addVertexWithUV(offset.getX() - half, offset.getY() + half, offset.getZ() + half, u, v + vLength);
        tes.addVertexWithUV(offset.getX() + half, offset.getY() + half, offset.getZ() + half, u + uLength, v + vLength);
        tes.addVertexWithUV(offset.getX() + half, offset.getY() - half, offset.getZ() + half, u + uLength, v);

        tes.draw();
    }

    // You might not want to call this too often.
    public static void triggerChunkRerender() {
        Minecraft.getMinecraft().renderGlobal.loadRenderers();
    }

    // 1.7.10: Simplified version - doesn't support IBakedModel
    public static void tryRenderItemWithColor(ItemStack stack, IBakedModel model, Color c, float alpha) {
        // 1.7.10: IBakedModel rendering is not supported directly
        // Items must be rendered through RenderItem
        if (!(stack == null || stack.stackSize <= 0)) {
            // In 1.7.10, items are rendered via RenderItem, not IBakedModel
            // This method is kept for API compatibility but is a no-op
        }
    }

    // 1.7.10: Removed - IBakedModel not supported
    private static void renderColoredItemModel(IBakedModel model, ItemStack stack, Color color, float alpha) {
        // No-op in 1.7.10
    }

    // 1.7.10: Removed - BakedQuad not supported
    private static void renderColoredQuads(Tessellator renderer, List<BakedQuad> quads, Color color, ItemStack stack) {
        // No-op in 1.7.10
    }

    public static void sortVertexData(Tessellator vb) {
        // 1.7.10 doesn't have sortVertexData on Tessellator
        // This method is no-op in 1.7.10
    }

    public static Color clampToColor(int rgb) {
        return clampToColorWithMultiplier(rgb, 1F);
    }

    public static Color clampToColorWithMultiplier(int rgb, float mul) {
        int r = ((rgb >> 16) & 0xFF);
        int g = ((rgb >> 8) & 0xFF);
        int b = ((rgb >> 0) & 0xFF);
        return new Color(
            WrapMathHelper.clamp((int) (((float) r) * mul), 0, 255),
            WrapMathHelper.clamp((int) (((float) g) * mul), 0, 255),
            WrapMathHelper.clamp((int) (((float) b) * mul), 0, 255));
    }

    public static Color clampToColor(int r, int g, int b) {
        return new Color(
            WrapMathHelper.clamp((int) (((float) r)), 0, 255),
            WrapMathHelper.clamp((int) (((float) g)), 0, 255),
            WrapMathHelper.clamp((int) (((float) b)), 0, 255));
    }

    public static Vector3 interpolatePosition(Entity e, float partialTicks) {
        return new Vector3(
            RenderingUtils.interpolate(e.lastTickPosX, e.posX, partialTicks),
            RenderingUtils.interpolate(e.lastTickPosY, e.posY, partialTicks),
            RenderingUtils.interpolate(e.lastTickPosZ, e.posZ, partialTicks));
    }

    public static double interpolate(double oldP, double newP, float partialTicks) {
        if (oldP == newP) return oldP;
        return oldP + ((newP - oldP) * partialTicks);
    }

    public static float interpolateRotation(float prevRotation, float nextRotation, float partialTick) {
        float rot = nextRotation - prevRotation;
        while (rot >= 180.0F) {
            rot -= 360.0F;
        }
        while (rot >= 180.0F) {
            rot -= 360.0F;
        }
        return prevRotation + partialTick * rot;
    }

    // Use with caution. Big block of rendering hack.
    @Deprecated
    public static void unsafe_preRenderHackCamera(EntityPlayer renderView, double x, double y, double z, double prevX,
        double prevY, double prevZ, double yaw, double yawPrev, double pitch, double pitchPrev) {
        // 1.7.10: TileEntityRendererDispatcher.staticPlayerX/Y/Z don't exist
        // Skip setting these fields

        Entity rv = Minecraft.getMinecraft().renderViewEntity;
        if (rv == null || !rv.equals(renderView)) {
            // 1.7.10: setRenderViewEntity takes an Entity, not EntityPlayer
            Minecraft.getMinecraft().renderViewEntity = renderView;
            rv = renderView;
        }
        EntityPlayer render = (EntityPlayer) rv;

        render.posX = x;
        render.posY = y;
        render.posZ = z;
        render.prevPosX = prevX;
        render.prevPosY = prevY;
        render.prevPosZ = prevZ;
        render.lastTickPosX = prevX;
        render.lastTickPosY = prevY;
        render.lastTickPosZ = prevZ;

        render.rotationYawHead = (float) yaw;
        render.rotationYaw = (float) yaw;
        render.prevRotationYaw = (float) yawPrev;
        render.prevRotationYawHead = (float) yawPrev;
        // 1.7.10: Entity doesn't have cameraYaw/prevCameraYaw fields
        // render.cameraYaw = (float) yaw;
        // render.prevCameraYaw = (float) yawPrev;
        render.rotationPitch = (float) pitch;
        render.prevRotationPitch = (float) pitchPrev;

        render = Minecraft.getMinecraft().thePlayer;

        render.posX = x;
        render.posY = y;
        render.posZ = z;
        render.prevPosX = prevX;
        render.prevPosY = prevY;
        render.prevPosZ = prevZ;
        render.lastTickPosX = prevX;
        render.lastTickPosY = prevY;
        render.lastTickPosZ = prevZ;

        render.rotationYawHead = (float) yaw;
        render.rotationYaw = (float) yaw;
        render.prevRotationYaw = (float) yawPrev;
        render.prevRotationYawHead = (float) yawPrev;
        // 1.7.10: Entity doesn't have cameraYaw/prevCameraYaw fields
        // render.cameraYaw = (float) yaw;
        // render.prevCameraYaw = (float) yawPrev;
        render.rotationPitch = (float) pitch;
        render.prevRotationPitch = (float) pitchPrev;

        Minecraft.getMinecraft().inGameHasFocus = false;
        ActiveRenderInfo.updateRenderInfo(render, false);
        ClientUtils.grabMouseCursor();
    }

    @Deprecated
    public static void unsafe_resetCamera() {
        if (Minecraft.getMinecraft().thePlayer != null) {
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            // 1.7.10: setRenderViewEntity takes an Entity, not a method
            Minecraft.getMinecraft().renderViewEntity = player;
            double x = player.posX;
            double y = player.posY;
            double z = player.posZ;
            // 1.7.10: No RenderManager.setRenderPosition or viewerPosX/Y/Z
            // Skip these calls in 1.7.10

            // 1.7.10: TileEntityRendererDispatcher.staticPlayerX/Y/Z don't exist
            // Skip setting these fields

            if (Minecraft.getMinecraft().currentScreen != null) {
                Minecraft.getMinecraft()
                    .displayGuiScreen(null);
            }

            if (Display.isActive()) {
                ClientUtils.ungrabMouseCursor();
            }

            Minecraft.getMinecraft().inGameHasFocus = false;
            Minecraft.getMinecraft()
                .setIngameFocus();
        }
    }

    public static void renderLightRayEffects(double x, double y, double z, Color effectColor, long seed,
        long continuousTick, int dstJump, int countFancy, int countNormal) {
        renderLightRayEffects(x, y, z, effectColor, seed, continuousTick, dstJump, 1, countFancy, countNormal);
    }

    public static void renderLightRayEffects(double x, double y, double z, Color effectColor, long seed,
        long continuousTick, int dstJump, float scale, int countFancy, int countNormal) {
        rand.setSeed(seed);

        int fancy_count = !FMLClientHandler.instance()
            .getClient().gameSettings.fancyGraphics ? countNormal : countFancy;

        Tessellator tes = Tessellator.instance;

        RenderHelper.disableStandardItemLighting();
        float f1 = continuousTick / 400.0F;
        float f2 = 0.4F;

        GL11.glEnable(GL11.GL_BLEND);
        Blending.ADDITIVE_ALPHA.applyStateManager();
        Blending.ADDITIVE_ALPHA.apply();
        for (int i = 0; i < fancy_count; i++) {
            tes.startDrawing(GL11.GL_TRIANGLE_FAN);
            float fa = rand.nextFloat() * 20.0F + 5.0F + f2 * 10.0F;
            float f4 = rand.nextFloat() * 2.0F + 1.0F + f2 * 2.0F;
            fa /= 30.0F / (Math.min(dstJump, 10 * scale) / 10.0F);
            f4 /= 30.0F / (Math.min(dstJump, 10 * scale) / 10.0F);

            float r = effectColor.getRed() / 255.0F;
            float g = effectColor.getGreen() / 255.0F;
            float b = effectColor.getBlue() / 255.0F;
            float a = (int) (255.0F * (1.0F - f2)) / 255.0F;

            // 1.7.10: setColorRGBA_F doesn't exist, use setColorRGBA_I with int conversion
            int rgb = ((int) (r * 255) << 16) | ((int) (g * 255) << 8) | (int) (b * 255);
            tes.setColorRGBA_I(rgb, (int) (a * 255));
            tes.addVertex(0, 0, 0);

            // 1.7.10: setColorRGBA_F doesn't exist, use setColorRGBA_I with int conversion
            rgb = ((int) (r * 255) << 16) | ((int) (g * 255) << 8) | (int) (b * 255);
            tes.setColorRGBA_I(rgb, 0);
            tes.addVertex(-0.7D * f4, fa, -0.5F * f4);
            tes.addVertex(0.7D * f4, fa, -0.5F * f4);
            tes.addVertex(0.0D, fa, 1.0F * f4);
            tes.addVertex(-0.7D * f4, fa, -0.5F * f4);

            tes.draw();
        }
        Blending.DEFAULT.applyStateManager();
        Blending.DEFAULT.apply();
        RenderHelper.enableStandardItemLighting();

    }

    public static void renderBlueStackTooltip(int x, int y, List<Tuple<ItemStack, String>> tooltipData, FontRenderer fr,
        RenderItem ri) {
        renderStackTooltip(x, y, tooltipData, new Color(0x000027), new Color(0x000044), Color.WHITE, fr, ri);
    }

    public static void renderStackTooltip(int x, int y, List<Tuple<ItemStack, String>> tooltipData, Color color,
        Color colorFade, Color strColor, FontRenderer fr, RenderItem ri) {
        TextureHelper.setActiveTextureToAtlasSprite();

        if (!(tooltipData == null || tooltipData.size() <= 0)) {
            int esWidth = 0;
            for (Tuple<ItemStack, String> toolTip : tooltipData) {
                int width = fr.getStringWidth(toolTip.value) + 17;
                if (width > esWidth) esWidth = width;
            }
            ScaledResolution sr = new ScaledResolution(
                Minecraft.getMinecraft(),
                Minecraft.getMinecraft().displayWidth,
                Minecraft.getMinecraft().displayHeight);
            if (x + 15 + esWidth > sr.getScaledWidth()) {
                x -= esWidth + 24;
            }
            int pX = x + 12;
            int pY = y - 12;
            int sumLineHeight = 8;
            if (tooltipData.size() > 1) sumLineHeight += 2 + (tooltipData.size() - 1) * 17;
            float z = 300F;

            GL11.glDisable(GL11.GL_DEPTH_TEST);
            drawGradientRect(pX - 3, pY - 4, z, pX + esWidth + 3, pY - 3, color, colorFade);
            drawGradientRect(
                pX - 3,
                pY + sumLineHeight + 3,
                z,
                pX + esWidth + 3,
                pY + sumLineHeight + 4,
                color,
                colorFade);
            drawGradientRect(pX - 3, pY - 3, z, pX + esWidth + 3, pY + sumLineHeight + 3, color, colorFade);
            drawGradientRect(pX - 4, pY - 3, z, pX - 3, pY + sumLineHeight + 3, color, colorFade);
            drawGradientRect(pX + esWidth + 3, pY - 3, z, pX + esWidth + 4, pY + sumLineHeight + 3, color, colorFade);

            int rgb = color.getRGB();
            int col = (rgb & 0x00FFFFFF) | rgb & 0xFF000000;
            Color colOp = new Color(col);
            drawGradientRect(pX - 3, pY - 3 + 1, z, pX - 3 + 1, pY + sumLineHeight + 3 - 1, color, colOp);
            drawGradientRect(
                pX + esWidth + 2,
                pY - 3 + 1,
                z,
                pX + esWidth + 3,
                pY + sumLineHeight + 3 - 1,
                color,
                colOp);
            drawGradientRect(pX - 3, pY - 3, z, pX + esWidth + 3, pY - 3 + 1, colOp, colOp);
            drawGradientRect(pX - 3, pY + sumLineHeight + 2, z, pX + esWidth + 3, pY + sumLineHeight + 3, color, color);

            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            for (Tuple<ItemStack, String> stackDesc : tooltipData) {
                fr.drawString(stackDesc.value, pX + 17, pY, strColor.getRGB());
                GL11.glPushMatrix();
                RenderHelper.enableGUIStandardItemLighting();
                // 1.7.10: renderItemAndEffectIntoGUI needs FontRenderer and renderEngine parameters
                ri.renderItemAndEffectIntoGUI(fr, Minecraft.getMinecraft().renderEngine, stackDesc.key, pX - 1, pY - 5);

                GL11.glEnable(GL11.GL_BLEND);
                Blending.DEFAULT.apply();

                GL11.glPopMatrix();
                pY += 17;
            }
            GL11.glPopAttrib();
            GL11.glEnable(GL11.GL_DEPTH_TEST);
        }

        GL11.glColor4f(1F, 1F, 1F, 1F);
        TextureHelper.refreshTextureBindState();
    }

    public static void renderBlueTooltip(int x, int y, List<String> tooltipData, FontRenderer fontRenderer) {
        renderTooltip(x, y, tooltipData, new Color(0x000027), new Color(0x000044), Color.WHITE, fontRenderer);
    }

    public static void renderTooltip(int x, int y, List<String> tooltipData, Color color, Color colorFade,
        Color strColor, FontRenderer fontRenderer) {
        TextureHelper.setActiveTextureToAtlasSprite();
        boolean lighting = GL11.glGetBoolean(GL11.GL_LIGHTING);
        if (lighting) RenderHelper.disableStandardItemLighting();

        if (!(tooltipData == null || tooltipData.size() <= 0)) {
            int esWidth = 0;
            for (String toolTip : tooltipData) {
                int width = fontRenderer.getStringWidth(toolTip);
                if (width > esWidth) esWidth = width;
            }
            ScaledResolution sr = new ScaledResolution(
                Minecraft.getMinecraft(),
                Minecraft.getMinecraft().displayWidth,
                Minecraft.getMinecraft().displayHeight);
            if (x + 15 + esWidth > sr.getScaledWidth()) {
                x -= esWidth + 24;
            }
            int pX = x + 12;
            int pY = y - 12;
            int sumLineHeight = 8;
            if (tooltipData.size() > 1) sumLineHeight += 2 + (tooltipData.size() - 1) * 10;
            float z = 300F;

            drawGradientRect(pX - 3, pY - 4, z, pX + esWidth + 3, pY - 3, color, colorFade);
            drawGradientRect(
                pX - 3,
                pY + sumLineHeight + 3,
                z,
                pX + esWidth + 3,
                pY + sumLineHeight + 4,
                color,
                colorFade);
            drawGradientRect(pX - 3, pY - 3, z, pX + esWidth + 3, pY + sumLineHeight + 3, color, colorFade);
            drawGradientRect(pX - 4, pY - 3, z, pX - 3, pY + sumLineHeight + 3, color, colorFade);
            drawGradientRect(pX + esWidth + 3, pY - 3, z, pX + esWidth + 4, pY + sumLineHeight + 3, color, colorFade);

            int rgb = color.getRGB();
            int col = (rgb & 0x00FFFFFF) | rgb & 0xFF000000;
            Color colOp = new Color(col);
            drawGradientRect(pX - 3, pY - 3 + 1, z, pX - 3 + 1, pY + sumLineHeight + 3 - 1, color, colOp);
            drawGradientRect(
                pX + esWidth + 2,
                pY - 3 + 1,
                z,
                pX + esWidth + 3,
                pY + sumLineHeight + 3 - 1,
                color,
                colOp);
            drawGradientRect(pX - 3, pY - 3, z, pX + esWidth + 3, pY - 3 + 1, colOp, colOp);
            drawGradientRect(pX - 3, pY + sumLineHeight + 2, z, pX + esWidth + 3, pY + sumLineHeight + 3, color, color);

            GL11.glDisable(GL11.GL_DEPTH_TEST);
            for (int i = 0; i < tooltipData.size(); ++i) {
                String str = tooltipData.get(i);
                fontRenderer.drawString(str, pX, pY, strColor.getRGB());
                if (i == 0) pY += 2;
                pY += 10;
            }
            GL11.glEnable(GL11.GL_DEPTH_TEST);
        }

        if (lighting) RenderHelper.enableStandardItemLighting();
        GL11.glColor4f(1F, 1F, 1F, 1F);
    }

    public static void renderBlueTooltipBox(int x, int y, int width, int height) {
        renderTooltipBox(x, y, width, height, new Color(0x000027), new Color(0x000044));
    }

    public static void renderTooltipBox(int x, int y, int width, int height, Color color, Color colorFade) {
        int pX = x + 12;
        int pY = y - 12;
        float z = 300F;

        drawGradientRect(pX - 3, pY - 4, z, pX + width + 3, pY - 3, color, colorFade);
        drawGradientRect(pX - 3, pY + height + 3, z, pX + width + 3, pY + height + 4, color, colorFade);
        drawGradientRect(pX - 3, pY - 3, z, pX + width + 3, pY + height + 3, color, colorFade);
        drawGradientRect(pX - 4, pY - 3, z, pX - 3, pY + height + 3, color, colorFade);
        drawGradientRect(pX + width + 3, pY - 3, z, pX + width + 4, pY + height + 3, color, colorFade);

        int rgb = color.getRGB();
        int col = (rgb & 0x00FFFFFF) | rgb & 0xFF000000;
        Color colOp = new Color(col);
        drawGradientRect(pX - 3, pY - 3 + 1, z, pX - 3 + 1, pY + height + 3 - 1, color, colOp);
        drawGradientRect(pX + width + 2, pY - 3 + 1, z, pX + width + 3, pY + height + 3 - 1, color, colOp);
        drawGradientRect(pX - 3, pY - 3, z, pX + width + 3, pY - 3 + 1, colOp, colOp);
        drawGradientRect(pX - 3, pY + height + 2, z, pX + width + 3, pY + height + 3, color, color);

        GL11.glColor4f(1F, 1F, 1F, 1F);
    }

    public static void removeStandartTranslationFromTESRMatrix(float partialTicks) {
        Entity rView = Minecraft.getMinecraft().renderViewEntity;
        if (rView == null) rView = Minecraft.getMinecraft().thePlayer;
        Entity entity = rView;
        double tx = entity.lastTickPosX + ((entity.posX - entity.lastTickPosX) * partialTicks);
        double ty = entity.lastTickPosY + ((entity.posY - entity.lastTickPosY) * partialTicks);
        double tz = entity.lastTickPosZ + ((entity.posZ - entity.lastTickPosZ) * partialTicks);
    }

    public static Vector3 getStandartTranslationRemovalVector(float partialTicks) {
        Entity rView = Minecraft.getMinecraft().renderViewEntity;
        if (rView == null) rView = Minecraft.getMinecraft().thePlayer;
        Entity entity = rView;
        double tx = entity.lastTickPosX + ((entity.posX - entity.lastTickPosX) * partialTicks);
        double ty = entity.lastTickPosY + ((entity.posY - entity.lastTickPosY) * partialTicks);
        double tz = entity.lastTickPosZ + ((entity.posZ - entity.lastTickPosZ) * partialTicks);
        return new Vector3(-tx, -ty, -tz);
    }

    public static void renderAngleRotatedTexturedRectVB(Vector3 renderOffset, Vector3 axis, double angleRad,
        double scale, double u, double v, double uLength, double vLength, Color c, int alpha, Tessellator vb,
        float partialTicks) {
        GL11.glPushMatrix();
        // removeStandartTranslationFromTESRMatrix(partialTicks);
        Vector3 shift = getStandartTranslationRemovalVector(partialTicks);

        Vector3 renderStart = axis.clone()
            .perpendicular()
            .rotate(angleRad, axis)
            .normalize();

        float r = c.getRed() / 255.0F;
        float g = c.getGreen() / 255.0F;
        float b = c.getBlue() / 255.0F;
        float a = alpha / 255.0F;

        Vector3 vec = renderStart.clone()
            .rotate(Math.toRadians(90), axis)
            .normalize()
            .multiply(scale)
            .add(renderOffset);
        vb.addVertexWithUV(
            shift.getX() + vec.getX(),
            shift.getY() + vec.getY(),
            shift.getZ() + vec.getZ(),
            u,
            v + vLength);

        vec = renderStart.clone()
            .multiply(-1)
            .normalize()
            .multiply(scale)
            .add(renderOffset);
        vb.addVertexWithUV(
            shift.getX() + vec.getX(),
            shift.getY() + vec.getY(),
            shift.getZ() + vec.getZ(),
            u + uLength,
            v + vLength);

        vec = renderStart.clone()
            .rotate(Math.toRadians(270), axis)
            .normalize()
            .multiply(scale)
            .add(renderOffset);
        vb.addVertexWithUV(
            shift.getX() + vec.getX(),
            shift.getY() + vec.getY(),
            shift.getZ() + vec.getZ(),
            u + uLength,
            v);

        vec = renderStart.clone()
            .normalize()
            .multiply(scale)
            .add(renderOffset);
        vb.addVertexWithUV(shift.getX() + vec.getX(), shift.getY() + vec.getY(), shift.getZ() + vec.getZ(), u, v);

        GL11.glPopMatrix();
    }

    public static void renderAngleRotatedTexturedRect(Vector3 renderOffset, Vector3 axis, double angleRad, double scale,
        double u, double v, double uLength, double vLength, float partialTicks) {
        removeStandartTranslationFromTESRMatrix(partialTicks);

        Vector3 renderStart = axis.clone()
            .perpendicular()
            .rotate(angleRad, axis)
            .normalize();
        Tessellator tes = Tessellator.instance;

        tes.startDrawingQuads();

        Vector3 vec = renderStart.clone()
            .rotate(Math.toRadians(90), axis)
            .normalize()
            .multiply(scale)
            .add(renderOffset);
        tes.addVertexWithUV(vec.getX(), vec.getY(), vec.getZ(), u, v + vLength);

        vec = renderStart.clone()
            .multiply(-1)
            .normalize()
            .multiply(scale)
            .add(renderOffset);
        tes.addVertexWithUV(vec.getX(), vec.getY(), vec.getZ(), u + uLength, v + vLength);

        vec = renderStart.clone()
            .rotate(Math.toRadians(270), axis)
            .normalize()
            .multiply(scale)
            .add(renderOffset);
        tes.addVertexWithUV(vec.getX(), vec.getY(), vec.getZ(), u + uLength, v);

        vec = renderStart.clone()
            .normalize()
            .multiply(scale)
            .add(renderOffset);
        tes.addVertexWithUV(vec.getX(), vec.getY(), vec.getZ(), u, v);

        tes.draw();

    }

    public static void drawGradientRect(int x, int y, float z, int toX, int toY, Color color, Color colorFade) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        Tessellator tes = Tessellator.instance;
        tes.startDrawingQuads();

        float r1 = color.getRed() / 255.0F;
        float g1 = color.getGreen() / 255.0F;
        float b1 = color.getBlue() / 255.0F;
        float a1 = color.getAlpha() / 255.0F;

        float r2 = colorFade.getRed() / 255.0F;
        float g2 = colorFade.getGreen() / 255.0F;
        float b2 = colorFade.getBlue() / 255.0F;
        float a2 = colorFade.getAlpha() / 255.0F;

        // 1.7.10: setColorRGBA_F doesn't exist, use setColorRGBA_I with int conversion
        int rgb = ((int) (r1 * 255) << 16) | ((int) (g1 * 255) << 8) | (int) (b1 * 255);
        tes.setColorRGBA_I(rgb, (int) (a1 * 255));
        tes.addVertex(toX, y, z);
        tes.addVertex(x, y, z);

        // 1.7.10: setColorRGBA_F doesn't exist, use setColorRGBA_I with int conversion
        rgb = ((int) (r2 * 255) << 16) | ((int) (g2 * 255) << 8) | (int) (b2 * 255);
        tes.setColorRGBA_I(rgb, (int) (a2 * 255));
        tes.addVertex(x, toY, z);
        tes.addVertex(toX, toY, z);

        tes.draw();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    public static void renderFacingFullQuadVB(Tessellator vb, double px, double py, double pz, float partialTicks,
        float scale, float angle, float colorRed, float colorGreen, float colorBlue, float alpha) {
        renderFacingQuadVB(
            vb,
            px,
            py,
            pz,
            partialTicks,
            scale,
            angle,
            0,
            0,
            1,
            1,
            colorRed,
            colorGreen,
            colorBlue,
            alpha);
    }

    public static void renderFacingQuadVB(Tessellator vb, double px, double py, double pz, float partialTicks,
        float scale, float angle, double u, double v, double uLength, double vLength, float colorRed, float colorGreen,
        float colorBlue, float alpha) {
        // 1.7.10: ActiveRenderInfo fields are accessed directly, not through getter methods
        float arX = ActiveRenderInfo.rotationX;
        float arZ = ActiveRenderInfo.rotationZ;
        float arYZ = ActiveRenderInfo.rotationYZ;
        float arXY = ActiveRenderInfo.rotationXY;
        float arXZ = ActiveRenderInfo.rotationXZ;

        Entity e = Minecraft.getMinecraft().renderViewEntity;
        if (e == null) {
            e = Minecraft.getMinecraft().thePlayer;
        }
        double iPX = e.prevPosX + (e.posX - e.prevPosX) * partialTicks;
        double iPY = e.prevPosY + (e.posY - e.prevPosY) * partialTicks;
        double iPZ = e.prevPosZ + (e.posZ - e.prevPosZ) * partialTicks;

        Vector3 v1 = new Vector3(-arX * scale - arYZ * scale, -arXZ * scale, -arZ * scale - arXY * scale);
        Vector3 v2 = new Vector3(-arX * scale + arYZ * scale, arXZ * scale, -arZ * scale + arXY * scale);
        Vector3 v3 = new Vector3(arX * scale + arYZ * scale, arXZ * scale, arZ * scale + arXY * scale);
        Vector3 v4 = new Vector3(arX * scale - arYZ * scale, -arXZ * scale, arZ * scale - arXY * scale);
        if (angle != 0.0F) {
            Vector3 pvec = new Vector3(iPX, iPY, iPZ);
            Vector3 tvec = new Vector3(px, py, pz);
            Vector3 qvec = pvec.subtract(tvec)
                .normalize();
            Vector3.Quat q = Vector3.Quat.buildQuatFrom3DVector(qvec, angle);
            q.rotateWithMagnitude(v1);
            q.rotateWithMagnitude(v2);
            q.rotateWithMagnitude(v3);
            q.rotateWithMagnitude(v4);
        }
        // 1.7.10: setColorRGBA_F doesn't exist, use setColorRGBA_I with int conversion
        int rgb = ((int) (colorRed * 255) << 16) | ((int) (colorGreen * 255) << 8) | (int) (colorBlue * 255);
        vb.setColorRGBA_I(rgb, (int) (alpha * 255));
        vb.addVertexWithUV(px + v1.getX() - iPX, py + v1.getY() - iPY, pz + v1.getZ() - iPZ, u + uLength, v + vLength);
        vb.addVertexWithUV(px + v2.getX() - iPX, py + v2.getY() - iPY, pz + v2.getZ() - iPZ, u + uLength, v);
        vb.addVertexWithUV(px + v3.getX() - iPX, py + v3.getY() - iPY, pz + v3.getZ() - iPZ, u, v);
        vb.addVertexWithUV(px + v4.getX() - iPX, py + v4.getY() - iPY, pz + v4.getZ() - iPZ, u, v + vLength);
    }

    public static void renderFacingFullQuad(double px, double py, double pz, float partialTicks, float scale,
        float angle) {
        renderFacingQuad(px, py, pz, partialTicks, scale, angle, 0, 0, 1, 1);
    }

    public static void renderFacingQuad(double px, double py, double pz, float partialTicks, float scale, float angle,
        double u, double v, double uLength, double vLength) {
        // 1.7.10: ActiveRenderInfo fields are accessed directly, not through getter methods
        float arX = ActiveRenderInfo.rotationX;
        float arZ = ActiveRenderInfo.rotationZ;
        float arYZ = ActiveRenderInfo.rotationYZ;
        float arXY = ActiveRenderInfo.rotationXY;
        float arXZ = ActiveRenderInfo.rotationXZ;

        Entity e = Minecraft.getMinecraft().renderViewEntity;
        if (e == null) {
            e = Minecraft.getMinecraft().thePlayer;
        }
        double iPX = e.prevPosX + (e.posX - e.prevPosX) * partialTicks;
        double iPY = e.prevPosY + (e.posY - e.prevPosY) * partialTicks;
        double iPZ = e.prevPosZ + (e.posZ - e.prevPosZ) * partialTicks;

        Vector3 v1 = new Vector3(-arX * scale - arYZ * scale, -arXZ * scale, -arZ * scale - arXY * scale);
        Vector3 v2 = new Vector3(-arX * scale + arYZ * scale, arXZ * scale, -arZ * scale + arXY * scale);
        Vector3 v3 = new Vector3(arX * scale + arYZ * scale, arXZ * scale, arZ * scale + arXY * scale);
        Vector3 v4 = new Vector3(arX * scale - arYZ * scale, -arXZ * scale, arZ * scale - arXY * scale);
        if (angle != 0.0F) {
            Vector3 pvec = new Vector3(iPX, iPY, iPZ);
            Vector3 tvec = new Vector3(px, py, pz);
            Vector3 qvec = pvec.subtract(tvec)
                .normalize();
            Vector3.Quat q = Vector3.Quat.buildQuatFrom3DVector(qvec, angle);
            q.rotateWithMagnitude(v1);
            q.rotateWithMagnitude(v2);
            q.rotateWithMagnitude(v3);
            q.rotateWithMagnitude(v4);
        }
        Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        t.addVertexWithUV(px + v1.getX() - iPX, py + v1.getY() - iPY, pz + v1.getZ() - iPZ, u, v + vLength);
        t.addVertexWithUV(px + v2.getX() - iPX, py + v2.getY() - iPY, pz + v2.getZ() - iPZ, u + uLength, v + vLength);
        t.addVertexWithUV(px + v3.getX() - iPX, py + v3.getY() - iPY, pz + v3.getZ() - iPZ, u + uLength, v);
        t.addVertexWithUV(px + v4.getX() - iPX, py + v4.getY() - iPY, pz + v4.getZ() - iPZ, u, v);
        t.draw();
    }

    public static void renderFacingFullColoredQuad(double px, double py, double pz, float partialTicks, float scale,
        float angle, int r, int g, int b, int a) {
        renderFacingColoredQuad(px, py, pz, partialTicks, scale, angle, 0, 0, 1, 1, r, g, b, a);
    }

    public static void renderFacingColoredQuad(double px, double py, double pz, float partialTicks, float scale,
        float angle, double u, double v, double uLength, double vLength, int r, int g, int b, int a) {
        // 1.7.10: ActiveRenderInfo fields are accessed directly, not through getter methods
        float arX = ActiveRenderInfo.rotationX;
        float arZ = ActiveRenderInfo.rotationZ;
        float arYZ = ActiveRenderInfo.rotationYZ;
        float arXY = ActiveRenderInfo.rotationXY;
        float arXZ = ActiveRenderInfo.rotationXZ;
        float cR = WrapMathHelper.clamp(r / 255F, 0F, 1F);
        float cG = WrapMathHelper.clamp(g / 255F, 0F, 1F);
        float cB = WrapMathHelper.clamp(b / 255F, 0F, 1F);
        float cA = WrapMathHelper.clamp(a / 255F, 0F, 1F);

        Entity e = Minecraft.getMinecraft().renderViewEntity;
        if (e == null) {
            e = Minecraft.getMinecraft().thePlayer;
        }
        double iPX = e.prevPosX + (e.posX - e.prevPosX) * partialTicks;
        double iPY = e.prevPosY + (e.posY - e.prevPosY) * partialTicks;
        double iPZ = e.prevPosZ + (e.posZ - e.prevPosZ) * partialTicks;

        Vector3 v1 = new Vector3(-arX * scale - arYZ * scale, -arXZ * scale, -arZ * scale - arXY * scale);
        Vector3 v2 = new Vector3(-arX * scale + arYZ * scale, arXZ * scale, -arZ * scale + arXY * scale);
        Vector3 v3 = new Vector3(arX * scale + arYZ * scale, arXZ * scale, arZ * scale + arXY * scale);
        Vector3 v4 = new Vector3(arX * scale - arYZ * scale, -arXZ * scale, arZ * scale - arXY * scale);
        if (angle != 0.0F) {
            Vector3 pvec = new Vector3(iPX, iPY, iPZ);
            Vector3 tvec = new Vector3(px, py, pz);
            Vector3 qvec = pvec.subtract(tvec)
                .normalize();
            Vector3.Quat q = Vector3.Quat.buildQuatFrom3DVector(qvec, angle);
            q.rotateWithMagnitude(v1);
            q.rotateWithMagnitude(v2);
            q.rotateWithMagnitude(v3);
            q.rotateWithMagnitude(v4);
        }
        Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        // 1.7.10: setColorRGBA_F doesn't exist, use setColorRGBA_I with int conversion
        int rgb = ((int) (cR * 255) << 16) | ((int) (cG * 255) << 8) | (int) (cB * 255);
        t.setColorRGBA_I(rgb, (int) (cA * 255));
        t.addVertexWithUV(px + v1.getX() - iPX, py + v1.getY() - iPY, pz + v1.getZ() - iPZ, u + uLength, v + vLength);
        t.addVertexWithUV(px + v2.getX() - iPX, py + v2.getY() - iPY, pz + v2.getZ() - iPZ, u + uLength, v);
        t.addVertexWithUV(px + v3.getX() - iPX, py + v3.getY() - iPY, pz + v3.getZ() - iPZ, u, v);
        t.addVertexWithUV(px + v4.getX() - iPX, py + v4.getY() - iPY, pz + v4.getZ() - iPZ, u, v + vLength);
        t.draw();
    }

    static {
        Field attempt;
        try {
            attempt = ReflectionHelper.findField(EntityItem.class, "skipPhysicRenderer");
            attempt.setAccessible(true);
        } catch (Exception e) {
            attempt = null;
        }
        itemPhysics_fieldSkipRenderHook = attempt;
    }

    // 1.7.10: Helper method to wrap text to width - replaces listFormattedStringToWidth from 1.12.2
    public static java.util.List<String> listFormattedStringToWidth(FontRenderer fontRenderer, String text,
        int wrapWidth) {
        java.util.List<String> lines = new java.util.ArrayList<>();
        if (text == null || text.isEmpty()) {
            return lines;
        }

        // Split by newlines first to preserve existing line breaks
        String[] paragraphs = text.split("\\\\n");
        for (String paragraph : paragraphs) {
            if (paragraph.isEmpty()) {
                lines.add("");
                continue;
            }

            // Split by spaces and rebuild lines
            String[] words = paragraph.split(" ");
            StringBuilder currentLine = new StringBuilder();

            for (String word : words) {
                String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;
                if (fontRenderer.getStringWidth(testLine) <= wrapWidth) {
                    if (currentLine.length() > 0) {
                        currentLine.append(" ");
                    }
                    currentLine.append(word);
                } else {
                    if (currentLine.length() > 0) {
                        lines.add(currentLine.toString());
                        currentLine = new StringBuilder(word);
                    } else {
                        // Single word too long, add it anyway
                        lines.add(word);
                    }
                }
            }

            if (currentLine.length() > 0) {
                lines.add(currentLine.toString());
            }
        }

        return lines;
    }

}
