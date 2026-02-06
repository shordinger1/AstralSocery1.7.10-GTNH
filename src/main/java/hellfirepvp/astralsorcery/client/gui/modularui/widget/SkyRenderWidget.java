/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * SkyRenderWidget - Custom widget for rendering telescope sky view
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui.modularui.widget;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.cleanroommc.modularui.api.GuiAxis;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetTheme;
import com.cleanroommc.modularui.widget.Widget;

import hellfirepvp.astralsorcery.client.gui.modularui.renderer.ConstellationRenderer;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.tile.TileTelescope;
import hellfirepvp.astralsorcery.common.util.LogHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

/**
 * SkyRenderWidget - Custom widget for telescope constellation viewing
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Renders night sky background with gradient</li>
 * <li>Displays visible constellations with discovery state</li>
 * <li>Supports telescope rotation</li>
 * <li>Interactive constellation discovery (click to discover)</li>
 * </ul>
 * <p>
 * <b>Usage in TelescopeGui:</b>
 *
 * <pre>
 * panel.child(new SkyRenderWidget()
 *     .pos(20, 20)
 *     .size(240, 240)
 *     .setTelescope(telescope)
 *     .setPlayerProgress(playerProgress));
 * </pre>
 */
public class SkyRenderWidget extends Widget<SkyRenderWidget> implements Interactable {

    /** Constellation renderer */
    private ConstellationRenderer renderer;

    /** Telescope tile entity (for rotation state) */
    private TileTelescope telescope;

    /** Player progress (for discovery state) */
    private PlayerProgress playerProgress;

    /** Viewport dimensions */
    private int viewWidth = 240;
    private int viewHeight = 240;

    /** Mouse click handler */
    private ConstellationClickListener clickListener;

    /**
     * Create sky render widget
     */
    public SkyRenderWidget() {
        super();
    }

    /**
     * Set telescope tile entity
     *
     * @param telescope Telescope tile entity
     * @return this
     */
    public SkyRenderWidget setTelescope(TileTelescope telescope) {
        this.telescope = telescope;
        return this;
    }

    /**
     * Set player progress
     *
     * @param progress Player progress
     * @return this
     */
    public SkyRenderWidget setPlayerProgress(PlayerProgress progress) {
        this.playerProgress = progress;
        return this;
    }

    /**
     * Set constellation click listener
     *
     * @param listener Click listener
     * @return this
     */
    public SkyRenderWidget setClickListener(ConstellationClickListener listener) {
        this.clickListener = listener;
        return this;
    }

    @Override
    public void draw(ModularGuiContext context, WidgetTheme widgetTheme) {
        // Initialize renderer on first draw
        if (renderer == null) {
            int width = getArea().getSize(GuiAxis.X);
            int height = getArea().getSize(GuiAxis.Y);
            renderer = new ConstellationRenderer(0, 0, width, height);
            viewWidth = width;
            viewHeight = height;
        }

        // Get world from Minecraft
        World world = Minecraft.getMinecraft().theWorld;
        if (world == null) {
            return;
        }

        // Get visible constellations
        ConstellationSkyHandler skyHandler = ConstellationSkyHandler.getInstance();
        List<IConstellation> visibleConstellations = skyHandler.getVisibleConstellations(world);

        // Clear cache before rendering
        renderer.clearCache();

        // Render background stars
        renderer.renderStars(50, context.getPartialTicks());

        // Render constellations
        for (IConstellation constellation : visibleConstellations) {
            String name = constellation.getUnlocalizedName();
            // Center the constellation in view
            renderer.renderConstellation(name, viewWidth / 2, viewHeight / 2, 1.5F,
                                         context.getPartialTicks(), playerProgress, true);
        }
    }

    /**
     * Handle mouse click for constellation discovery
     */
    @Override
    public Interactable.Result onMousePressed(int mouseButton) {
        if (renderer == null || clickListener == null) {
            return Interactable.Result.ACCEPT;
        }

        // Get mouse position from context
        ModularGuiContext context = getContext();
        if (context == null) {
            return Interactable.Result.ACCEPT;
        }

        int mouseX = context.getMouseX();
        int mouseY = context.getMouseY();

        // Adjust mouse coordinates to view coordinates
        float relX = mouseX - getArea().getRelativePoint(GuiAxis.X);
        float relY = mouseY - getArea().getRelativePoint(GuiAxis.Y);

        // Check if any constellation was clicked
        IConstellation clicked = renderer.getClickedConstellation(relX, relY);
        if (clicked != null && playerProgress != null) {
            // Check if constellation is not yet discovered
            if (!playerProgress.hasConstellationDiscovered(clicked)) {
                // Trigger discovery
                clickListener.onConstellationClicked(clicked);
                return Interactable.Result.SUCCESS;
            }
        }

        return Interactable.Result.ACCEPT;
    }

    /**
     * Update viewport (called when widget is resized)
     */
    @Override
    public void onResized() {
        super.onResized();

        // Update viewport dimensions
        viewWidth = getArea().getSize(GuiAxis.X);
        viewHeight = getArea().getSize(GuiAxis.Y);

        // Recreate renderer with new dimensions
        if (renderer != null) {
            renderer = new ConstellationRenderer(0, 0, viewWidth, viewHeight);
        }
    }

    /**
     * Interface for constellation click handling
     */
    public interface ConstellationClickListener {
        /**
         * Called when a constellation is clicked
         *
         * @param constellation The constellation that was clicked
         */
        void onConstellationClicked(IConstellation constellation);
    }
}
