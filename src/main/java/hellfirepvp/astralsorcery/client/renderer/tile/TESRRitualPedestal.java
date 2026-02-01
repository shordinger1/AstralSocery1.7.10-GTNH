/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * TESRRitualPedestal - Ritual Pedestal TileEntitySpecialRenderer
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
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * TESRRitualPedestal - Ritual pedestal renderer (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Renders pedestal base model (OBJ)</li>
 * <li>Part of BlockStarlightNetwork in original version</li>
 * <li>Item display TODO for future</li>
 * </ul>
 */
@SideOnly(Side.CLIENT)
public class TESRRitualPedestal extends TileEntitySpecialRenderer {

    private final IModelCustom model;
    private final ResourceLocation texture;

    /**
     * Default constructor (for legacy registration)
     */
    public TESRRitualPedestal() {
        this(null, null);
    }

    /**
     * Constructor with model and texture
     */
    public TESRRitualPedestal(IModelCustom model, ResourceLocation texture) {
        this.model = model;
        this.texture = texture;

        // Auto-register if model is provided
        if (this.model != null) {
            try {
                ClientRegistry.bindTileEntitySpecialRenderer(TileRitualPedestal.class, this);
                LogHelper.info("[TESRRitualPedestal] Registered TESR with OBJ model support");
            } catch (Exception e) {
                LogHelper.error("[TESRRitualPedestal] Failed to register TESR", e);
            }
        }
    }

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {
        if (te == null) {
            LogHelper.warn("[TESRRitualPedestal] TileEntity is null");
            return;
        }

        if (!(te instanceof TileRitualPedestal)) {
            LogHelper.warn("[TESRRitualPedestal] TileEntity is not a TileRitualPedestal: " + te.getClass().getName());
            return;
        }

        TileRitualPedestal pedestal = (TileRitualPedestal) te;

        // Render the pedestal base model
        if (model != null) {
            renderPedestalModel(x, y, z);
        }

        // TODO: Render displayed item when pedestal has inventory
        // TODO: Render constellation effects when ritual is active
    }

    /**
     * Render the pedestal base model
     */
    private void renderPedestalModel(double x, double y, double z) {
        GL11.glPushMatrix();
        try {
            GL11.glTranslated(x, y, z);

            // Enable proper lighting and face culling
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_CULL_FACE);

            // Set full brightness
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);

            // Bind texture and render
            bindTexture(texture);
            model.renderAll();

        } catch (Exception e) {
            LogHelper.error("[TESRRitualPedestal] Error rendering base model", e);
        } finally {
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glPopMatrix();
        }
    }
}
