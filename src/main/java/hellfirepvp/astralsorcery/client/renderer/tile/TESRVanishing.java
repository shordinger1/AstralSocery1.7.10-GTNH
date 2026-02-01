/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * TESRVanishing - Vanishing Block TileEntitySpecialRenderer
 *
 * Renders a subtle outline for the vanishing block
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.renderer.tile;

import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import hellfirepvp.astralsorcery.client.renderer.AstralBaseTESR;
import hellfirepvp.astralsorcery.common.tile.TileVanishing;

/**
 * TESRVanishing - Vanishing block renderer (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Renders a faint, transparent outline when the block is present</li>
 * <li>Opacity pulses based on world time</li>
 * <li>Only renders when close to player (optimization)</li>
 * </ul>
 * <p>
 * <b>Note:</b> The vanishing block itself is normally invisible.
 * This TESR provides a subtle visual hint for players with special wands
 * or for debugging purposes.
 */
public class TESRVanishing extends AstralBaseTESR {

    public TESRVanishing() {
        super();
    }

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTick) {
        if (!(te instanceof TileVanishing)) {
            return;
        }

        TileVanishing vanishing = (TileVanishing) te;
        if (te == null) {
            return;
        }

        // Optimization: Only render when close to player
        double distance = getDistanceToPlayer(te);
        if (distance > 16) {
            return;
        }

        renderVanishingOutline(vanishing, x, y, z, partialTick);
    }

    /**
     * Render faint outline of the vanishing block
     */
    private void renderVanishingOutline(TileVanishing te, double x, double y, double z, float partialTick) {
        saveState();

        // Translate to block position
        translateToTileEntity(x, y, z);

        // Enable additive blending for glowing effect
        enableBlend();
        setAdditiveBlend();
        disableLighting();
        disableDepthMask();

        // Calculate pulsing opacity
        float time = getWorldTime(te, partialTick);
        float pulse = (float) (Math.sin(time * 0.1) * 0.5 + 0.5); // 0 to 1
        float alpha = 0.05F + (pulse * 0.1F); // 0.05 to 0.15

        // Set color (white/cyan tint)
        setColor4f(0.8F, 0.9F, 1.0F, alpha);

        // Draw wireframe cube
        drawWireframeCube();

        restoreState();
    }

    /**
     * Draw wireframe cube outline
     */
    private void drawWireframeCube() {
        GL11.glBegin(GL11.GL_LINES);

        // Bottom face
        GL11.glVertex3d(0, 0, 0);
        GL11.glVertex3d(1, 0, 0);
        GL11.glVertex3d(1, 0, 0);
        GL11.glVertex3d(1, 0, 1);
        GL11.glVertex3d(1, 0, 1);
        GL11.glVertex3d(0, 0, 1);
        GL11.glVertex3d(0, 0, 1);
        GL11.glVertex3d(0, 0, 0);

        // Top face
        GL11.glVertex3d(0, 1, 0);
        GL11.glVertex3d(1, 1, 0);
        GL11.glVertex3d(1, 1, 0);
        GL11.glVertex3d(1, 1, 1);
        GL11.glVertex3d(1, 1, 1);
        GL11.glVertex3d(0, 1, 1);
        GL11.glVertex3d(0, 1, 1);
        GL11.glVertex3d(0, 1, 0);

        // Vertical edges
        GL11.glVertex3d(0, 0, 0);
        GL11.glVertex3d(0, 1, 0);
        GL11.glVertex3d(1, 0, 0);
        GL11.glVertex3d(1, 1, 0);
        GL11.glVertex3d(1, 0, 1);
        GL11.glVertex3d(1, 1, 1);
        GL11.glVertex3d(0, 0, 1);
        GL11.glVertex3d(0, 1, 1);

        GL11.glEnd();
    }

    /**
     * Get distance to nearest player
     * Optimization: Don't render if too far away
     */
    private double getDistanceToPlayer(TileEntity te) {
        if (te.getWorldObj() == null || te.getWorldObj().playerEntities == null) {
            return Double.MAX_VALUE;
        }

        double minDist = Double.MAX_VALUE;
        for (Object playerObj : te.getWorldObj().playerEntities) {
            if (playerObj instanceof net.minecraft.entity.player.EntityPlayer) {
                net.minecraft.entity.player.EntityPlayer player = (net.minecraft.entity.player.EntityPlayer) playerObj;
                double dx = player.posX - (te.xCoord + 0.5);
                double dy = player.posY - (te.yCoord + 0.5);
                double dz = player.posZ - (te.zCoord + 0.5);
                double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
                if (dist < minDist) {
                    minDist = dist;
                }
            }
        }
        return minDist;
    }

    /**
     * Math helper for 1.7.10
     */
    private static class MathHelper {

        public static double sin(double value) {
            return Math.sin(value);
        }
    }
}
