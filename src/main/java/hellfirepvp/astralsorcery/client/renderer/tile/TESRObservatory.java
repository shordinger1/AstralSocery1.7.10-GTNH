/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * TESR for Observatory Block - TST Pattern
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
import hellfirepvp.astralsorcery.common.tile.TileObservatory;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * TESR for Observatory
 * <p>
 * TST Pattern: Receives pre-loaded model and textures
 * <p>
 * Observatory uses dual textures:
 * - texture: observatory_base (main structure)
 * - texture1: observatory_seat_tube (seat parts)
 */
@SideOnly(Side.CLIENT)
public class TESRObservatory extends TileEntitySpecialRenderer {

    private final IModelCustom model;
    private final ResourceLocation textureBase;
    private final ResourceLocation textureSeatTube;

    /**
     * Default constructor for legacy registration
     */
    public TESRObservatory() {
        this(null, null, null);
    }

    /**
     * TST pattern constructor - receives pre-loaded model and dual textures
     * Registers this TESR for TileObservatory
     */
    public TESRObservatory(IModelCustom model, ResourceLocation textureBase) {
        this(model, textureBase, null);
    }

    /**
     * Full constructor with dual textures
     */
    public TESRObservatory(IModelCustom model, ResourceLocation textureBase, ResourceLocation textureSeatTube) {
        this.model = model;
        this.textureBase = textureBase;
        this.textureSeatTube = textureSeatTube;

        // Auto-register if model is provided
        if (this.model != null) {
            try {
                ClientRegistry.bindTileEntitySpecialRenderer(TileObservatory.class, this);
                LogHelper.info("[TESRObservatory] Registered TESR with dual-texture OBJ model support");
            } catch (Exception e) {
                LogHelper.error("[TESRObservatory] Failed to register TESR", e);
            }
        }
    }

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
        if (tile == null) {
            return;
        }

        if (!(tile instanceof TileObservatory)) {
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

            // Multi-material rendering: Render each material group separately
            // First, render texture1 groups (observatory_seat_tube)
            if (textureSeatTube != null) {
                bindTexture(textureSeatTube);
                renderMaterialGroups("texture1");
            }

            // Then, render texture groups (observatory_base)
            if (textureBase != null) {
                bindTexture(textureBase);
                renderMaterialGroups("texture");
            }

            // Fallback: render all if single texture mode
            if (textureSeatTube == null && textureBase != null) {
                bindTexture(textureBase);
                model.renderAll();
            }

        } catch (Exception e) {
            LogHelper.error("[TESRObservatory] Error during render", e);
        } finally {
            // Restore OpenGL state
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glPopMatrix();
        }
    }

    /**
     * Render all groups that start with the specified material name
     * For example, renderMaterialGroups("texture1") will render:
     * - texture1_element_0
     * - texture1_element_1
     * - texture1_element_2
     * etc.
     */
    private void renderMaterialGroups(String materialName) {
        try {
            // Try to render each element group with this material
            // Observatory has elements 0-48 (based on the JSON)
            for (int i = 0; i < 50; i++) {
                String groupName = materialName + "_element_" + i;
                try {
                    model.renderPart(groupName);
                } catch (Exception e) {
                    // Group doesn't exist or can't be rendered, skip it
                    // This is normal - not all element indices exist for each material
                }
            }
        } catch (Exception e) {
            LogHelper.debug("[TESRObservatory] Error rendering material groups: " + materialName);
        }
    }
}
