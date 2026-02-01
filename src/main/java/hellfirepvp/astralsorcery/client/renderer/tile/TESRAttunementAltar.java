/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * TESR for Attunement Altar Block - TST Pattern
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
import hellfirepvp.astralsorcery.common.tile.TileAttunementAltar;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * TESR for Attunement Altar
 * <p>
 * TST Pattern: Receives pre-loaded model and texture
 */
@SideOnly(Side.CLIENT)
public class TESRAttunementAltar extends TileEntitySpecialRenderer {

    private final IModelCustom model;
    private final ResourceLocation texture;

    /**
     * Default constructor for legacy registration
     */
    public TESRAttunementAltar() {
        this(null, null);
    }

    /**
     * TST pattern constructor - receives pre-loaded model and texture
     * Registers this TESR for TileAttunementAltar
     */
    public TESRAttunementAltar(IModelCustom model, ResourceLocation texture) {
        this.model = model;
        this.texture = texture;

        // Auto-register if model is provided
        if (this.model != null) {
            try {
                ClientRegistry.bindTileEntitySpecialRenderer(TileAttunementAltar.class, this);
                LogHelper.info("[TESRAttunementAltar] Registered TESR with OBJ model support");
            } catch (Exception e) {
                LogHelper.error("[TESRAttunementAltar] Failed to register TESR", e);
            }
        }
    }

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
        if (tile == null) {
            return;
        }

        if (!(tile instanceof TileAttunementAltar)) {
            return;
        }

        if (this.model == null) {
            return;
        }

        // Save OpenGL state
        GL11.glPushMatrix();

        try {
            // Translate to block position
            GL11.glTranslated(x, y, z);

            // Enable proper lighting and face culling
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_CULL_FACE);

            // Scale the model
            GL11.glScaled(1.0, 1.0, 1.0);

            // Set full brightness for glow effect
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);

            // Bind texture
            this.bindTexture(this.texture);

            // Render the model
            this.model.renderAll();

        } catch (Exception e) {
            LogHelper.error("[TESRAttunementAltar] Error during render", e);
        } finally {
            // Restore OpenGL state
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glPopMatrix();
        }
    }
}
