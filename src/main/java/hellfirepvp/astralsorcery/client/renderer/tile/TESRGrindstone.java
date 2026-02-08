/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * TESR for Grindstone Block - TST Pattern
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.renderer.tile;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.block.BlockMachine;
import hellfirepvp.astralsorcery.common.tile.TileGrindstone;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * TESR for Grindstone and Telescope (BlockMachine)
 * <p>
 * Supports both machine types by checking block metadata
 */
@SideOnly(Side.CLIENT)
public class TESRGrindstone extends TileEntitySpecialRenderer {

    private final IModelCustom grindstoneModel;
    private final IModelCustom telescopeModel;
    private final ResourceLocation grindstoneTexture;
    private final ResourceLocation telescopeTexture;

    /**
     * Default constructor for legacy registration
     */
    public TESRGrindstone() {
        this(null, null, null, null);
    }

    /**
     * Constructor for two-machine support
     */
    public TESRGrindstone(IModelCustom grindstoneModel, ResourceLocation grindstoneTexture, IModelCustom telescopeModel,
        ResourceLocation telescopeTexture) {
        this.grindstoneModel = grindstoneModel;
        this.grindstoneTexture = grindstoneTexture;
        this.telescopeModel = telescopeModel;
        this.telescopeTexture = telescopeTexture;

        // Auto-register if models are provided
        if (this.grindstoneModel != null) {
            try {
                ClientRegistry.bindTileEntitySpecialRenderer(TileGrindstone.class, this);
                LogHelper.info("[TESRGrindstone] Registered TESR with dual machine support (Grindstone + Telescope)");
            } catch (Exception e) {
                LogHelper.error("[TESRGrindstone] Failed to register TESR", e);
            }
        }
    }

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
        if (tile == null) {
            LogHelper.warn("[TESRGrindstone] TileEntity is null");
            return;
        }

        if (!(tile instanceof TileGrindstone)) {
            LogHelper.warn(
                "[TESRGrindstone] TileEntity is not a TileGrindstone: " + tile.getClass()
                    .getName());
            return;
        }

        // Determine machine type from block metadata
        World world = tile.getWorldObj();
        int xPos = tile.xCoord;
        int yPos = tile.yCoord;
        int zPos = tile.zCoord;
        int metadata = world.getBlockMetadata(xPos, yPos, zPos);

        // Select model and texture based on machine type
        IModelCustom model;
        ResourceLocation texture;
        boolean isTelescope = (metadata == BlockMachine.META_TELESCOPE);

        if (isTelescope) {
            model = this.telescopeModel;
            texture = this.telescopeTexture;
            LogHelper.debug(
                String.format(
                    "[TESRGrindstone] Rendering TELESCOPE at x=%.2f y=%.2f z=%.2f, model=%s, texture=%s",
                    x,
                    y,
                    z,
                    model != null ? "loaded" : "NULL",
                    texture != null ? texture.toString() : "NULL"));
        } else {
            model = this.grindstoneModel;
            texture = this.grindstoneTexture;
            LogHelper.debug(
                String.format(
                    "[TESRGrindstone] Rendering GRINDSTONE at x=%.2f y=%.2f z=%.2f, model=%s, texture=%s",
                    x,
                    y,
                    z,
                    model != null ? "loaded" : "NULL",
                    texture != null ? texture.toString() : "NULL"));
        }

        if (model == null) {
            LogHelper.warn("[TESRGrindstone] Model is null, cannot render!");
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

            // Scale the model - 1.12.2 uses 0.0625
            GL11.glScaled(0.0625, 0.0625, 0.0625);

            // Set full brightness
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);

            // Bind texture
            this.bindTexture(texture);

            // Render the model
            model.renderAll();

        } catch (Exception e) {
            LogHelper.error("[TESRGrindstone] Error during render", e);
        } finally {
            // Restore OpenGL state
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glPopMatrix();
        }
    }
}
