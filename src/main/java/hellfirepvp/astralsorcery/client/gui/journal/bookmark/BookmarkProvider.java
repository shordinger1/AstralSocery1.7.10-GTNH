/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Bookmark provider for journal navigation
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui.journal.bookmark;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.util.Provider;

/**
 * BookmarkProvider - Journal bookmark (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Navigates to different journal screens</li>
 * <li>Can be hidden based on research progress</li>
 * <li>Has custom texture and position</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * 
 * <pre>
 * BookmarkProvider bookmark = new BookmarkProvider(
 *     "journal.bookmark.progression", // Unlocalized name
 *     0, // Index
 *     () -> new GuiJournalProgression(), // GUI provider
 *     () -> ResearchManager.getProgress()
 *         .hasProgression() // Visibility test
 * );
 * GuiScreenJournal.addBookmark(bookmark);
 * </pre>
 */
@SideOnly(Side.CLIENT)
public class BookmarkProvider {

    private final Provider<GuiScreen> guiProvider;
    private final int index;
    private final String unlocName;
    private final Provider<Boolean> canSeeTest;

    /**
     * Constructor
     *
     * @param unlocName   Unlocalized name for tooltip
     * @param index       Bookmark index (for ordering)
     * @param guiProvider Provider for the GUI screen
     * @param canSeeTest  Provider for visibility test
     */
    public BookmarkProvider(String unlocName, int index, Provider<GuiScreen> guiProvider,
        Provider<Boolean> canSeeTest) {
        this.unlocName = unlocName;
        this.index = index;
        this.guiProvider = guiProvider;
        this.canSeeTest = canSeeTest;
    }

    /**
     * Get the GUI screen for this bookmark
     *
     * @return The GUI screen
     */
    public GuiScreen getGuiScreen() {
        return guiProvider.provide();
    }

    /**
     * Check if this bookmark should be visible
     *
     * @return true if visible
     */
    public boolean canSee() {
        return canSeeTest != null && canSeeTest.provide();
    }

    /**
     * Get bookmark index
     *
     * @return The index
     */
    public int getIndex() {
        return index;
    }

    /**
     * Get unlocalized name
     *
     * @return The unlocalized name
     */
    public String getUnlocalizedName() {
        return unlocName;
    }

    /**
     * Get bookmark texture
     *
     * @return The texture resource
     */
    public ResourceLocation getTextureBookmark() {
        return new ResourceLocation("astralsorcery", "textures/gui/guijbookmark.png");
    }

    /**
     * Get stretched bookmark texture
     *
     * @return The texture resource
     */
    public ResourceLocation getTextureBookmarkStretched() {
        return new ResourceLocation("astralsorcery", "textures/gui/guijbookmarkstretched.png");
    }
}
