/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IComplexEffect
 * Created by HellFirePvP
 * Date: 02.08.2016 / 12:31
 *
 * 1.7.10 Port:
 * - Interface for complex particle and visual effects
 * - Managed by EffectHandler
 */
public interface IComplexEffect {

    /**
     * Check if this effect should be removed from the effect handler
     * 
     * @return true if this effect can be removed
     */
    public boolean canRemove();

    /**
     * Check if this effect has been flagged as removed
     * 
     * @return true if removed
     */
    public boolean isRemoved();

    /**
     * Flag this effect as removed
     */
    public void flagAsRemoved();

    /**
     * Clear the removal flag
     */
    public void clearRemoveFlag();

    /**
     * Get the render target for this effect
     * 
     * @return the render target
     */
    public RenderTarget getRenderTarget();

    /**
     * Render this effect
     * 
     * @param pTicks partial ticks
     */
    public void render(float pTicks);

    /**
     * Update this effect (called each tick)
     */
    public void tick();

    /**
     * Get the render layer for this effect
     * Valid layers: 0, 1, 2
     * Lower layers are rendered first.
     * 
     * @return the layer (default 0)
     */
    default public int getLayer() {
        return 0;
    }

    /**
     * Render target enum
     */
    public static enum RenderTarget {

        OVERLAY_TEXT,
        RENDERLOOP

    }

    /**
     * Interface to prevent removal from particle limit settings
     */
    public static interface PreventRemoval {
    }

}
