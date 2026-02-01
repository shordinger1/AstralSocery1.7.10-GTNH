/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * TESR for Attunement Relay Block - TST Pattern
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.renderer.tile;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.tile.TileAttunementRelay;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * TESR for Attunement Relay
 * <p>
 * TST Pattern: Receives pre-loaded model and texture
 */
@SideOnly(Side.CLIENT)
public class TESRAttunementRelay extends TileEntitySpecialRenderer {

    private final IModelCustom model;
    private final ResourceLocation texture;

    /**
     * Default constructor for legacy registration
     */
    public TESRAttunementRelay() {
        this(null, null);
    }

    /**
     * TST pattern constructor - receives pre-loaded model and texture
     * Registers this TESR for TileAttunementRelay
     */
    public TESRAttunementRelay(IModelCustom model, ResourceLocation texture) {
        this.model = model;
        this.texture = texture;

        // Auto-register if model is provided
        if (this.model != null) {
            try {
                ClientRegistry.bindTileEntitySpecialRenderer(TileAttunementRelay.class, this);
                LogHelper.info("[TESRAttunementRelay] Registered TESR with OBJ model support");
            } catch (Exception e) {
                LogHelper.error("[TESRAttunementRelay] Failed to register TESR", e);
            }
        }
    }

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
        if (tile == null) {
            return;
        }

        if (!(tile instanceof TileAttunementRelay)) {
            return;
        }

        TileAttunementRelay relay = (TileAttunementRelay) tile;

        // Save OpenGL state
        GL11.glPushMatrix();

        try {
            // Translate to block position
            GL11.glTranslated(x, y, z);

            // Render base model
            if (this.model != null) {
                // Enable proper lighting and face culling
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_CULL_FACE);

                // Pulsing brightness
                float time = relay.getTicksExisted() + partialTicks;
                float pulse = (float) Math.sin(time * 0.05F) * 0.5F + 0.5F;
                float brightness = 0.5F + pulse * 0.3F;

                // NOTE: hasGlassLens() check removed - method doesn't exist in TileAttunementRelay yet

                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f * brightness, 240f * brightness);

                // Bind texture
                this.bindTexture(this.texture);

                // Render the model
                this.model.renderAll();
            }

            // Render collection effect
            // NOTE: hasGlassLens() and canSeeSky() checks removed - methods don't exist yet
            renderCollectionEffect(relay, partialTicks);

            // Render transmission beam to linked altar
            // NOTE: isLinked() check removed - method doesn't exist yet
            renderTransmissionBeam(relay, partialTicks);

        } catch (Exception e) {
            LogHelper.error("[TESRAttunementRelay] Error during render", e);
        } finally {
            // Restore OpenGL state
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glPopMatrix();
        }
    }

    /**
     * Render starlight collection effect
     */
    private void renderCollectionEffect(TileAttunementRelay relay, float partialTicks) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GL11.glDisable(GL11.GL_LIGHTING);

        float time = relay.getTicksExisted() + partialTicks;

        // Rising particles
        for (int i = 0; i < 6; i++) {
            float offset = (i * 0.6F);
            float particleTime = (time * 0.2F + offset) % 2.0F;

            if (particleTime < 0 || particleTime > 1.2F) continue;

            float height = particleTime * 1.5F;
            float size = 0.03F * (1.0F - particleTime * 0.4F);
            float alpha = 0.5F * (1.0F - particleTime * 0.5F);

            float angle = i * (float) Math.PI * 2 / 6;
            float radius = 0.2F + particleTime * 0.1F;
            float px = (float) Math.cos(angle) * radius + 0.5F;
            float pz = (float) Math.sin(angle) * radius + 0.5F;

            GL11.glColor4f(0.5F, 0.7F, 1.0F, alpha);

            float halfSize = size / 2;
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex3f(px - halfSize, height, pz - halfSize);
            GL11.glVertex3f(px + halfSize, height, pz - halfSize);
            GL11.glVertex3f(px + halfSize, height, pz + halfSize);
            GL11.glVertex3f(px - halfSize, height, pz + halfSize);
            GL11.glEnd();
        }

        // Glowing orb on top
        float pulse = (float) Math.sin(time * 0.08F) * 0.5F + 0.5F;
        float size = 0.15F + pulse * 0.05F;
        float alpha = 0.6F + pulse * 0.2F;
        float y = 1.2F + (float) Math.sin(time * 0.06F) * 0.05F;

        GL11.glColor4f(0.5F, 0.7F, 1.0F, alpha);

        int segments = 12;
        for (int i = 0; i < segments; i++) {
            float angle1 = (float) (i * 2 * Math.PI / segments);
            float angle2 = (float) ((i + 1) * 2 * Math.PI / segments);

            float x1 = (float) Math.cos(angle1) * size + 0.5F;
            float z1 = (float) Math.sin(angle1) * size + 0.5F;
            float x2 = (float) Math.cos(angle2) * size + 0.5F;
            float z2 = (float) Math.sin(angle2) * size + 0.5F;

            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex3f(x1, y - size, z1);
            GL11.glVertex3f(x2, y - size, z2);
            GL11.glVertex3f(x2, y + size, z2);
            GL11.glVertex3f(x1, y + size, z1);
            GL11.glEnd();
        }

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    /**
     * Render transmission beam to linked altar
     */
    private void renderTransmissionBeam(TileAttunementRelay relay, float partialTicks) {
        // NOTE: Simplified - getLinked() method exists but returns BlockPos
        hellfirepvp.astralsorcery.common.util.math.BlockPos linked = relay.getLinked();
        if (linked == null) return;

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GL11.glDisable(GL11.GL_CULL_FACE);

        float time = relay.getTicksExisted() + partialTicks;

        int linkedX = linked.getX();
        int linkedY = linked.getY();
        int linkedZ = linked.getZ();

        float dx = linkedX - relay.xCoord;
        float dy = linkedY - relay.yCoord;
        float dz = linkedZ - relay.zCoord;
        float distance = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (distance > 32) {
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);
            return;
        }

        int segments = (int) (distance * 5);
        float alpha = 0.4F * (1.0F - distance / 64F);

        GL11.glColor4f(0.5F, 0.7F, 1.0F, alpha);

        for (int i = 0; i < segments; i++) {
            float t1 = (float) i / segments;
            float t2 = (float) (i + 1) / segments;

            float x1 = dx * t1;
            float y1 = dy * t1 + 1.2F;
            float z1 = dz * t1;
            float x2 = dx * t2;
            float y2 = dy * t2 + 1.2F;
            float z2 = dz * t2;

            float beamWidth = 0.03F;
            GL11.glBegin(GL11.GL_QUADS);

            for (int j = 0; j < 4; j++) {
                float angle = (float) (j * Math.PI / 2) + time * 0.02F;
                float ox = (float) Math.cos(angle) * beamWidth;
                float oz = (float) Math.sin(angle) * beamWidth;

                GL11.glVertex3f(x1 + ox, y1, z1 + oz);
                GL11.glVertex3f(x2 + ox, y2, z2 + oz);

                angle = (float) ((j + 1) * Math.PI / 2) + time * 0.02F;
                ox = (float) Math.cos(angle) * beamWidth;
                oz = (float) Math.sin(angle) * beamWidth;

                GL11.glVertex3f(x2 + ox, y2, z2 + oz);
                GL11.glVertex3f(x1 + ox, y1, z1 + oz);
            }

            GL11.glEnd();
        }

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }
}
