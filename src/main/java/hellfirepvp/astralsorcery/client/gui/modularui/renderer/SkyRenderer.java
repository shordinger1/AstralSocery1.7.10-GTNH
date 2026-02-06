/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Sky Renderer - Base class for sky rendering in GUIs
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui.modularui.renderer;

import java.util.Random;

import hellfirepvp.astralsorcery.common.lib.Constants;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

/**
 * Base class for rendering sky/constellation views in GUIs
 * <p>
 * Provides shared rendering functionality for:
 * - Telescope GUI
 * - Observatory GUI
 * - Hand Telescope GUI
 */
public abstract class SkyRenderer {

    protected final Minecraft mc = Minecraft.getMinecraft();
    protected final Random random = new Random();

    /** Star texture resource locations */
    protected static final ResourceLocation STAR_TEXTURE = new ResourceLocation(
        Constants.MODID,
        "textures/effect/flarestar.png");
    protected static final ResourceLocation STAR1 = new ResourceLocation(
        Constants.MODID,
        "textures/environment/star1.png");
    protected static final ResourceLocation STAR2 = new ResourceLocation(
        Constants.MODID,
        "textures/environment/star2.png");
    protected static final ResourceLocation STAR3 = new ResourceLocation(
        Constants.MODID,
        "textures/environment/star3.png");

    /** View dimensions */
    protected int viewX;
    protected int viewY;
    protected int viewWidth;
    protected int viewHeight;

    /** Current world time for star positioning */
    protected long worldSeed;

    /** Render tick counter */
    protected int renderTicks;

    public SkyRenderer(int viewX, int viewY, int viewWidth, int viewHeight) {
        this.viewX = viewX;
        this.viewY = viewY;
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;

        if (mc.theWorld != null) {
            this.worldSeed = mc.theWorld.getSeed();
        }
    }

    /**
     * Render the sky view
     *
     * @param partialTicks Partial ticks for smooth animation
     */
    public abstract void render(float partialTicks);

    /**
     * Render background (gradient + grid texture)
     */
    protected void renderBackground(float partialTicks) {
        // TODO: Implement gradient background
        // Day/night cycle transition
        // Grid texture overlay
    }

    /**
     * Bind a texture for rendering
     *
     * @param location The texture location to bind
     */
    protected void bindTexture(ResourceLocation location) {
        TextureManager textureManager = mc.getTextureManager();
        textureManager.bindTexture(location);
    }

    /**
     * Render random stars in the background
     *
     * @param starCount Number of stars to render
     * @param partialTicks Partial ticks for flickering
     */
    public void renderStars(int starCount, float partialTicks) {
        if (mc.theWorld == null) return;

        // Set random seed based on world seed and day
        int day = (int) (mc.theWorld.getWorldTime() / 24000L);
        random.setSeed(worldSeed + day);

        // Enable blending for smooth rendering
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        for (int i = 0; i < starCount; i++) {
            float x = viewX + random.nextFloat() * viewWidth;
            float y = viewY + random.nextFloat() * viewHeight;
            float size = random.nextFloat() * 3 + 1; // 1-4 pixel size

            // Calculate brightness with flickering
            float brightness = 0.3F + (calculateFlicker(partialTicks, random.nextInt(20))) * 0.6F;
            brightness *= mc.theWorld.getStarBrightness(partialTicks) * 2;

            // Check weather and rain
            brightness *= (1.0F - mc.theWorld.getRainStrength(partialTicks));

            if (brightness > 0) {
                GL11.glColor4f(1.0F, 1.0F, 1.0F, brightness);
                renderStar((int) x, (int) y, (int) size);
            }
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_BLEND);
    }

    /**
     * Render a single star at the given position using textured quad
     *
     * @param x    X position
     * @param y    Y position
     * @param size Star size
     */
    protected void renderStar(int x, int y, int size) {
        // Select a random star texture
        ResourceLocation tex;
        switch (random.nextInt(3)) {
            case 0:
                tex = STAR1;
                break;
            case 1:
                tex = STAR2;
                break;
            case 2:
            default:
                tex = STAR3;
                break;
        }

        // Bind star texture
        bindTexture(tex);

        // Render textured quad
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0, 0);
        GL11.glVertex2f(x, y);
        GL11.glTexCoord2f(1, 0);
        GL11.glVertex2f(x + size, y);
        GL11.glTexCoord2f(1, 1);
        GL11.glVertex2f(x + size, y + size);
        GL11.glTexCoord2f(0, 1);
        GL11.glVertex2f(x, y + size);
        GL11.glEnd();
    }

    /**
     * Calculate flickering effect for stars/constellations
     *
     * @param partialTicks Partial ticks
     * @param offset Random offset for variation
     * @return Brightness multiplier (0.0 - 1.0)
     */
    protected float calculateFlicker(float partialTicks, int offset) {
        // Simple sine wave flicker
        float time = renderTicks + partialTicks;
        return (float) ((Math.sin(time * 0.1 + offset) + 1) / 2);
    }

    /**
     * Check if the sky is visible (not raining, etc.)
     */
    protected boolean canSeeSky() {
        if (mc.theWorld == null) return false;
        float rainStrength = mc.theWorld.getRainStrength(1.0F);
        float starBrightness = mc.theWorld.getStarBrightness(1.0F);
        return rainStrength < 0.3F && starBrightness > 0.1F;
    }

    /**
     * Update render tick counter
     */
    public void updateTicks(int ticks) {
        this.renderTicks = ticks;
    }

}
