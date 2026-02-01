/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * TESRNoOp - No-Operation TileEntitySpecialRenderer
 *
 * Used as a placeholder for TileEntities that don't need special rendering
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.renderer.tile;

import net.minecraft.tileentity.TileEntity;

import hellfirepvp.astralsorcery.client.renderer.AstralBaseTESR;

/**
 * TESRNoOp - No-operation renderer (1.7.10)
 * <p>
 * <b>Usage:</b>
 * <ul>
 * <li>Placeholder for TileEntities without special rendering</li>
 * <li>Can be used for testing TESR registration</li>
 * <li>Minimal performance overhead</li>
 * </ul>
 * <p>
 * This renderer does nothing, allowing the standard block model
 * to be rendered without any special effects.
 */
public class TESRNoOp extends AstralBaseTESR {

    public TESRNoOp() {
        super();
    }

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTick) {
        // No-op - standard block model rendering applies
    }
}
