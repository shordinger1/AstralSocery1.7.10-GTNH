/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.sky;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.VertexFormat;
import net.minecraft.client.renderer.VertexFormatElement;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.IRenderHandler;

import org.lwjgl.opengl.GL11;

import hellfirepvp.astralsorcery.common.util.WrapMathHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderDefaultSkybox
 * Created by HellFirePvP
 * Date: 07.05.2016 / 00:44
 */
public class RenderDefaultSkybox extends IRenderHandler {

    private static VertexBuffer skyVBO;
    private static VertexBuffer sky2VBO;
    private static VertexBuffer starVBO;

    private static int starGLCallList = -1;
    private static int glSkyList = -1;
    private static int glSkyList2 = -1;

    private static VertexFormat vertexBufferFormat;

    private static final ResourceLocation MC_DEF_SUN_PNG = new ResourceLocation("textures/environment/sun.png");
    private static final ResourceLocation MC_DEF_MOON_PHASES_PNG = new ResourceLocation(
        "textures/environment/moon_phases.png");

    public static void setupDefaultSkybox() {
        vertexBufferFormat = new VertexFormat();
        vertexBufferFormat.addElement(
            new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUsage.POSITION, 3));

        Tessellator tessellator = Tessellator.instance;

        setupStars(tessellator);
        setupSky1(tessellator);
        setupSky2(tessellator);
    }

    private static void setupSky2(Tessellator tessellator) {
        // VBOs are not used in 1.7.10, only display lists

        if (glSkyList2 >= 0) {
            GLAllocation.deleteDisplayLists(glSkyList2);
            glSkyList2 = -1;
        }

        // VBO support not available in 1.7.10, use display lists instead
        glSkyList2 = GLAllocation.generateDisplayLists(1);
        GL11.glNewList(glSkyList2, GL11.GL_COMPILE);
        setupSkyVertices(tessellator, -16.0F, true);
        tessellator.draw();
        GL11.glEndList();
    }

    private static void setupSky1(Tessellator tessellator) {
        // VBOs are not used in 1.7.10, only display lists

        if (glSkyList >= 0) {
            GLAllocation.deleteDisplayLists(glSkyList);
            glSkyList = -1;
        }

        // VBO support not available in 1.7.10, use display lists instead
        glSkyList = GLAllocation.generateDisplayLists(1);
        GL11.glNewList(glSkyList, GL11.GL_COMPILE);
        setupSkyVertices(tessellator, 16.0F, false);
        tessellator.draw();
        GL11.glEndList();
    }

    private static void setupStars(Tessellator tessellator) {
        // VBOs are not used in 1.7.10, only display lists

        if (starGLCallList >= 0) {
            GLAllocation.deleteDisplayLists(starGLCallList);
            starGLCallList = -1;
        }

        // VBO support not available in 1.7.10, use display lists instead
        starGLCallList = GLAllocation.generateDisplayLists(1);
        GL11.glNewList(starGLCallList, GL11.GL_COMPILE);
        setupStarVertices(tessellator);
        tessellator.draw();
        GL11.glEndList();
    }

    private static void setupSkyVertices(Tessellator tessellator, float y, boolean invert) {
        tessellator.startDrawingQuads();

        for (int k = -384; k <= 384; k += 64) {
            for (int l = -384; l <= 384; l += 64) {

                float f = (float) k;
                float f1 = (float) (k + 64);

                if (invert) {
                    f1 = (float) k;
                    f = (float) (k + 64);
                }

                tessellator.addVertex((double) f, (double) y, (double) l);
                tessellator.addVertex((double) f1, (double) y, (double) l);
                tessellator.addVertex((double) f1, (double) y, (double) (l + 64));
                tessellator.addVertex((double) f, (double) y, (double) (l + 64));
            }
        }
    }

    private static void setupStarVertices(Tessellator tessellator) {
        Random random = new Random(10842L);
        tessellator.startDrawingQuads();
        for (int i = 0; i < 1500; ++i) {
            double x = (double) (random.nextFloat() * 2.0F - 1.0F);
            double y = (double) (random.nextFloat() * 2.0F - 1.0F);
            double z = (double) (random.nextFloat() * 2.0F - 1.0F);
            double ovrSize = (double) (0.15F + random.nextFloat() * 0.1F); // Size flat increase.
            double d4 = x * x + y * y + z * z;
            if (d4 < 1.0D && d4 > 0.01D) {

                d4 = 1.0D / Math.sqrt(d4);
                x *= d4;
                y *= d4;
                z *= d4;

                double d5 = x * 100.0D;
                double d6 = y * 100.0D;
                double d7 = z * 100.0D;

                double d8 = Math.atan2(x, z);
                double d9 = Math.sin(d8);
                double d10 = Math.cos(d8);

                double d11 = Math.atan2(Math.sqrt(x * x + z * z), y);
                double d12 = Math.sin(d11);
                double d13 = Math.cos(d11);

                // Sizes
                double d14 = random.nextDouble() * Math.PI * 2.0D;
                double size = Math.sin(d14); // Size percentage increase.
                double d16 = Math.cos(d14);

                // Set 2D vertices
                for (int j = 0; j < 4; ++j) {
                    // double d17 = 0.0D;
                    double d18 = (double) ((j & 2) - 1) * ovrSize; // 0 = -1 * [0.15-0.25[
                    double d19 = (double) ((j + 1 & 2) - 1) * ovrSize; // 0 = -1 * [0.15-0.25[
                    // double d20 = 0.0D;

                    double d21 = d18 * d16 - d19 * size;
                    double d22 = d19 * d16 + d18 * size;
                    double d23 = d21 * d12 + 0.0D * d13;

                    double d24 = 0.0D * d12 - d21 * d13;

                    double d25 = d24 * d9 - d22 * d10;
                    double d26 = d22 * d9 + d24 * d10;
                    tessellator.addVertex(d5 + d25, d6 + d23, d7 + d26);
                }
            }
        }
    }

    @Override
    public void render(float partialTicks, WorldClient world, Minecraft mc) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        renderDefaultSkybox(partialTicks);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    private static void renderDefaultSkybox(float partialTicks) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        Vec3 vec3 = Minecraft.getMinecraft().theWorld
            .getSkyColor(Minecraft.getMinecraft().renderViewEntity, partialTicks);
        float f = (float) vec3.xCoord;
        float f1 = (float) vec3.yCoord;
        float f2 = (float) vec3.zCoord;

        if (Minecraft.getMinecraft().gameSettings.anaglyph) {
            float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
            float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
            float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
            f = f3;
            f1 = f4;
            f2 = f5;
        }

        GL11.glColor3f(f, f1, f2);
        Tessellator tessellator = Tessellator.instance;
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_FOG);
        GL11.glColor3f(f, f1, f2);

        // VBOs not used in 1.7.10, always use display lists
        GL11.glCallList(glSkyList);

        GL11.glDisable(GL11.GL_FOG);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(770, 771);
        GL11.glBlendFunc(770, 1);
        RenderHelper.disableStandardItemLighting();
        float[] afloat = Minecraft.getMinecraft().theWorld.provider
            .calcSunriseSunsetColors(Minecraft.getMinecraft().theWorld.getCelestialAngle(partialTicks), partialTicks);

        if (afloat != null) {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glShadeModel(GL11.GL_SMOOTH);
            GL11.glPushMatrix();
            GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            // 1.7.10: getCelestialAngleRadians() doesn't exist, convert getCelestialAngle() to radians
            float celestialAngle = (float) Math
                .toRadians(Minecraft.getMinecraft().theWorld.getCelestialAngle(partialTicks) * 360.0F);
            GL11.glRotatef(WrapMathHelper.sin(celestialAngle) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
            float f6 = afloat[0];
            float f7 = afloat[1];
            float f8 = afloat[2];

            if (Minecraft.getMinecraft().gameSettings.anaglyph) {
                float f9 = (f6 * 30.0F + f7 * 59.0F + f8 * 11.0F) / 100.0F;
                float f10 = (f6 * 30.0F + f7 * 70.0F) / 100.0F;
                float f11 = (f6 * 30.0F + f8 * 70.0F) / 100.0F;
                f6 = f9;
                f7 = f10;
                f8 = f11;
            }

            tessellator.startDrawing(6);
            // 1.7.10: setColorRGBA_F doesn't exist, use setColorRGBA_I with int conversion
            int rgb = ((int) (f6 * 255) << 16) | ((int) (f7 * 255) << 8) | (int) (f8 * 255);
            tessellator.setColorRGBA_I(rgb, (int) (afloat[3] * 255));
            tessellator.addVertex(0.0D, 100.0D, 0.0D);
            // int j = 16;

            for (int l = 0; l <= 16; ++l) {
                float f21 = (float) l * (float) Math.PI * 2.0F / 16.0F;
                float f12 = WrapMathHelper.sin(f21);
                float f13 = WrapMathHelper.cos(f21);
                // 1.7.10: setColorRGBA_F doesn't exist, use setColorRGBA_I with int conversion
                rgb = ((int) (afloat[0] * 255) << 16) | ((int) (afloat[1] * 255) << 8) | (int) (afloat[2] * 255);
                tessellator.setColorRGBA_I(rgb, 0);
                tessellator
                    .addVertex((double) (f12 * 120.0F), (double) (f13 * 120.0F), (double) (-f13 * 40.0F * afloat[3]));
            }

            tessellator.draw();
            GL11.glPopMatrix();
            GL11.glShadeModel(GL11.GL_FLAT);
        }

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(770, 1);
        GL11.glPushMatrix();
        float f16 = 1.0F - Minecraft.getMinecraft().theWorld.getRainStrength(partialTicks);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, f16);
        GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(Minecraft.getMinecraft().theWorld.getCelestialAngle(partialTicks) * 360.0F, 1.0F, 0.0F, 0.0F);
        float f17 = 30.0F;
        Minecraft.getMinecraft().renderEngine.bindTexture(MC_DEF_SUN_PNG);
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double) (-f17), 100.0D, (double) (-f17), 0.0D, 0.0D);
        tessellator.addVertexWithUV((double) f17, 100.0D, (double) (-f17), 1.0D, 0.0D);
        tessellator.addVertexWithUV((double) f17, 100.0D, (double) f17, 1.0D, 1.0D);
        tessellator.addVertexWithUV((double) (-f17), 100.0D, (double) f17, 0.0D, 1.0D);
        tessellator.draw();
        f17 = 20.0F;
        Minecraft.getMinecraft().renderEngine.bindTexture(MC_DEF_MOON_PHASES_PNG);
        int i = Minecraft.getMinecraft().theWorld.getMoonPhase();
        int k = i % 4;
        int i1 = i / 4 % 2;
        float f22 = (float) (k) / 4.0F;
        float f23 = (float) (i1) / 2.0F;
        float f24 = (float) (k + 1) / 4.0F;
        float f14 = (float) (i1 + 1) / 2.0F;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double) (-f17), -100.0D, (double) f17, (double) f24, (double) f14);
        tessellator.addVertexWithUV((double) f17, -100.0D, (double) f17, (double) f22, (double) f14);
        tessellator.addVertexWithUV((double) f17, -100.0D, (double) (-f17), (double) f22, (double) f23);
        tessellator.addVertexWithUV((double) (-f17), -100.0D, (double) (-f17), (double) f24, (double) f23);
        tessellator.draw();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        float f15 = Minecraft.getMinecraft().theWorld.getStarBrightness(partialTicks) * f16;

        if (f15 > 0.0F) {
            GL11.glColor4f(f15, f15, f15, f15);

            // VBOs not used in 1.7.10, always use display lists
            GL11.glCallList(starGLCallList);
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_FOG);
        GL11.glPopMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor3f(0.0F, 0.0F, 0.0F);
        double d0 = Minecraft.getMinecraft().thePlayer.posY - Minecraft.getMinecraft().theWorld.getHorizon();

        if (d0 < 0.0D) {
            GL11.glPushMatrix();
            GL11.glTranslatef(0.0F, 12.0F, 0.0F);

            // VBOs not used in 1.7.10, always use display lists
            GL11.glCallList(glSkyList2);

            GL11.glPopMatrix();
            // float f18 = 1.0F;
            float f19 = -((float) (d0 + 65.0D));
            // float f20 = -1.0F;
            tessellator.startDrawingQuads();
            tessellator.setColorRGBA(0, 0, 0, 255);
            tessellator.addVertex(-1.0D, (double) f19, 1.0D);
            tessellator.addVertex(1.0D, (double) f19, 1.0D);
            tessellator.addVertex(1.0D, -1.0D, 1.0D);
            tessellator.addVertex(-1.0D, -1.0D, 1.0D);
            tessellator.addVertex(-1.0D, -1.0D, -1.0D);
            tessellator.addVertex(1.0D, -1.0D, -1.0D);
            tessellator.addVertex(1.0D, (double) f19, -1.0D);
            tessellator.addVertex(-1.0D, (double) f19, -1.0D);
            tessellator.addVertex(1.0D, -1.0D, -1.0D);
            tessellator.addVertex(1.0D, -1.0D, 1.0D);
            tessellator.addVertex(1.0D, (double) f19, 1.0D);
            tessellator.addVertex(1.0D, (double) f19, -1.0D);
            tessellator.addVertex(-1.0D, (double) f19, -1.0D);
            tessellator.addVertex(-1.0D, (double) f19, 1.0D);
            tessellator.addVertex(-1.0D, -1.0D, 1.0D);
            tessellator.addVertex(-1.0D, -1.0D, -1.0D);
            tessellator.addVertex(-1.0D, -1.0D, -1.0D);
            tessellator.addVertex(-1.0D, -1.0D, 1.0D);
            tessellator.addVertex(1.0D, -1.0D, 1.0D);
            tessellator.addVertex(1.0D, -1.0D, -1.0D);
            tessellator.draw();
        }

        if (Minecraft.getMinecraft().theWorld.provider.isSkyColored()) {
            GL11.glColor3f(f * 0.2F + 0.04F, f1 * 0.2F + 0.04F, f2 * 0.6F + 0.1F);
        } else {
            GL11.glColor3f(f, f1, f2);
        }

        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, -((float) (d0 - 16.0D)), 0.0F);
        GL11.glCallList(glSkyList2);
        GL11.glPopMatrix();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDepthMask(true);
    }

}
