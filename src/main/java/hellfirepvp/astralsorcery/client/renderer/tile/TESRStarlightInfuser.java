/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * TESRStarlightInfuser - Starlight Infuser TileEntitySpecialRenderer
 *
 * Renders the starlight infuser with infusion progress visualization
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.renderer.tile;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.tile.TileStarlightInfuser;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * TESR for Starlight Infuser
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Render infuser model</li>
 * <li>Infusion progress visualization</li>
 * <li>Starlight collection effect</li>
 * <li>Completed item glow</li>
 * </ul>
 */
@SideOnly(Side.CLIENT)
public class TESRStarlightInfuser extends TileEntitySpecialRenderer {

    // Static storage - shared across all instances
    private static net.minecraftforge.client.model.IModelCustom staticModel;
    private static net.minecraft.util.ResourceLocation staticTexture;

    private final net.minecraftforge.client.model.IModelCustom model;
    private final net.minecraft.util.ResourceLocation texture;

    /**
     * Default constructor
     */
    public TESRStarlightInfuser() {
        this.model = staticModel;
        this.texture = staticTexture;

        if (this.model == null) {
            LogHelper.warn("[TESRStarlightInfuser] Model is NULL in constructor!");
        }
    }

    /**
     * Set static model and texture
     */
    public static void setModelAndTexture(net.minecraftforge.client.model.IModelCustom model,
        net.minecraft.util.ResourceLocation texture) {
        TESRStarlightInfuser.staticModel = model;
        TESRStarlightInfuser.staticTexture = texture;

        if (model != null) {
            try {
                ClientRegistry.bindTileEntitySpecialRenderer(TileStarlightInfuser.class, new TESRStarlightInfuser());
                LogHelper.info("[TESRStarlightInfuser] Registered TESR with OBJ model support");
            } catch (Exception e) {
                LogHelper.error("[TESRStarlightInfuser] Failed to register TESR", e);
            }
        }
    }

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
        if (tile == null) return;
        if (!(tile instanceof TileStarlightInfuser)) return;

        TileStarlightInfuser infuser = (TileStarlightInfuser) tile;

        GL11.glPushMatrix();

        try {
            GL11.glTranslated(x, y, z);

            // Render base model
            if (this.model != null) {
                renderInfuserModel(infuser, partialTicks);
            }

            // Render infusion effects
            if (infuser.isInfusing()) {
                renderInfusionProgress(infuser, partialTicks);
            }

            // Render starlight collection
            // NOTE: canSeeSky() check removed - render collection effect based on other conditions
            renderCollectionEffect(infuser, partialTicks);

        } catch (Exception e) {
            LogHelper.error("[TESRStarlightInfuser] Error during render", e);
        } finally {
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glPopMatrix();
        }
    }

    /**
     * Render the infuser OBJ model
     */
    private void renderInfuserModel(TileStarlightInfuser infuser, float partialTicks) {
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        this.bindTexture(this.texture);

        // Brightness based on infusion progress
        float time = infuser.getTicksExisted() + partialTicks;
        float progress = infuser.getInfusionProgress() / 500F; // 0 to 1
        float brightness = 0.5F + progress * 0.5F;

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f * brightness, 240f * brightness);

        this.model.renderAll();
    }

    /**
     * Render infusion progress visualization
     */
    private void renderInfusionProgress(TileStarlightInfuser infuser, float partialTicks) {
        float progress = infuser.getInfusionProgress();
        float maxProgress = 500F; // INFUSION_TICKS
        float progressRatio = progress / maxProgress;

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);

        float time = infuser.getTicksExisted() + partialTicks;

        // Render progress ring around infuser
        renderProgressRing(progressRatio, time);

        // Render floating item being infused
        renderInfusingItem(infuser, time);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    /**
     * Render progress ring
     */
    private void renderProgressRing(float progress, float time) {
        float radius = 0.6F;
        float thickness = 0.05F;
        int segments = 64;
        float y = 1.0F;

        // Background ring (dim)
        GL11.glColor4f(0.3F, 0.3F, 0.3F, 0.3F);
        GL11.glBegin(GL11.GL_QUAD_STRIP);
        for (int i = 0; i <= segments; i++) {
            float angle = (float) (i * 2 * Math.PI / segments);
            float cos = (float) Math.cos(angle);
            float sin = (float) Math.sin(angle);

            GL11.glVertex3f(cos * radius + 0.5F, y, sin * radius + 0.5F);
            GL11.glVertex3f(cos * (radius + thickness) + 0.5F, y, sin * (radius + thickness) + 0.5F);
        }
        GL11.glEnd();

        // Progress ring (bright, rotates)
        float r = 0.4F + progress * 0.4F;
        float g = 0.6F + progress * 0.2F;
        float b = 1.0F;
        GL11.glColor4f(r, g, b, 0.8F);

        GL11.glRotatef(time * 10, 0, 1, 0);

        int progressSegments = (int) (segments * progress);
        if (progressSegments > 0) {
            GL11.glBegin(GL11.GL_QUAD_STRIP);
            for (int i = 0; i <= progressSegments; i++) {
                float angle = (float) (i * 2 * Math.PI / segments);
                float cos = (float) Math.cos(angle);
                float sin = (float) Math.sin(angle);

                GL11.glVertex3f(cos * radius + 0.5F, y, sin * radius + 0.5F);
                GL11.glVertex3f(cos * (radius + thickness) + 0.5F, y, sin * (radius + thickness) + 0.5F);
            }
            GL11.glEnd();
        }

        GL11.glRotatef(-time * 10, 0, 1, 0);
    }

    /**
     * Render infusing item floating above
     */
    private void renderInfusingItem(TileStarlightInfuser infuser, float time) {
        // Item floats and rotates
        float y = 1.5F + (float) Math.sin(time * 0.08F) * 0.1F;

        GL11.glPushMatrix();
        GL11.glTranslatef(0.5F, y, 0.5F);
        GL11.glRotatef(time * 1.5F, 0, 1, 0);

        // Render simple glow for now
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        float size = 0.15F;
        float pulse = (float) Math.sin(time * 0.1F) * 0.5F + 0.5F;
        GL11.glColor4f(0.8F, 0.9F, 1.0F, 0.6F + pulse * 0.2F);

        GL11.glBegin(GL11.GL_QUADS);
        // 6 faces of a cube
        for (int i = 0; i < 6; i++) {
            renderCubeFace(i, size);
        }
        GL11.glEnd();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();
    }

    /**
     * Render a single face of a cube
     */
    private void renderCubeFace(int face, float size) {
        float s = size / 2;

        switch (face) {
            case 0: // Top
                GL11.glVertex3f(-s, s, -s); GL11.glVertex3f(s, s, -s);
                GL11.glVertex3f(s, s, s); GL11.glVertex3f(-s, s, s);
                break;
            case 1: // Bottom
                GL11.glVertex3f(-s, -s, s); GL11.glVertex3f(s, -s, s);
                GL11.glVertex3f(s, -s, -s); GL11.glVertex3f(-s, -s, -s);
                break;
            case 2: // Front
                GL11.glVertex3f(-s, -s, s); GL11.glVertex3f(s, -s, s);
                GL11.glVertex3f(s, s, s); GL11.glVertex3f(-s, s, s);
                break;
            case 3: // Back
                GL11.glVertex3f(s, -s, -s); GL11.glVertex3f(-s, -s, -s);
                GL11.glVertex3f(-s, s, -s); GL11.glVertex3f(s, s, -s);
                break;
            case 4: // Left
                GL11.glVertex3f(-s, -s, -s); GL11.glVertex3f(-s, -s, s);
                GL11.glVertex3f(-s, s, s); GL11.glVertex3f(-s, s, -s);
                break;
            case 5: // Right
                GL11.glVertex3f(s, -s, s); GL11.glVertex3f(s, -s, -s);
                GL11.glVertex3f(s, s, -s); GL11.glVertex3f(s, s, s);
                break;
        }
    }

    /**
     * Render starlight collection effect
     */
    private void renderCollectionEffect(TileStarlightInfuser infuser, float partialTicks) {
        float time = infuser.getTicksExisted() + partialTicks;

        // Rising particles from sides
        for (int i = 0; i < 4; i++) {
            float offset = (i * 0.5F);
            float particleTime = (time * 0.15F + offset) % 2.0F;

            if (particleTime < 0 || particleTime > 1.0F) continue;

            float height = particleTime;
            float size = 0.02F * (1.0F - particleTime * 0.3F);
            float alpha = 0.4F * (1.0F - particleTime);

            // Position on 4 sides
            float angle = i * (float) Math.PI / 2;
            float radius = 0.4F;
            float px = (float) Math.cos(angle) * radius + 0.5F;
            float pz = (float) Math.sin(angle) * radius + 0.5F;

            GL11.glColor4f(0.6F, 0.8F, 1.0F, alpha);

            float halfSize = size / 2;
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex3f(px - halfSize, height, pz - halfSize);
            GL11.glVertex3f(px + halfSize, height, pz - halfSize);
            GL11.glVertex3f(px + halfSize, height, pz + halfSize);
            GL11.glVertex3f(px - halfSize, height, pz + halfSize);
            GL11.glEnd();
        }

        // Central glow
        float pulse = (float) Math.sin(time * 0.08F) * 0.5F + 0.5F;
        float size = 0.2F + pulse * 0.05F;
        float alpha = 0.3F + pulse * 0.2F;

        GL11.glColor4f(0.6F, 0.8F, 1.0F, alpha);

        int segments = 16;
        for (int i = 0; i < segments; i++) {
            float angle1 = (float) (i * 2 * Math.PI / segments);
            float angle2 = (float) ((i + 1) * 2 * Math.PI / segments);

            float x1 = (float) Math.cos(angle1) * size + 0.5F;
            float z1 = (float) Math.sin(angle1) * size + 0.5F;
            float x2 = (float) Math.cos(angle2) * size + 0.5F;
            float z2 = (float) Math.sin(angle2) * size + 0.5F;

            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex3f(x1, 0.1F, z1);
            GL11.glVertex3f(x2, 0.1F, z2);
            GL11.glVertex3f(x2, 0.1F + size, z2);
            GL11.glVertex3f(x1, 0.1F + size, z1);
            GL11.glEnd();
        }
    }
}
