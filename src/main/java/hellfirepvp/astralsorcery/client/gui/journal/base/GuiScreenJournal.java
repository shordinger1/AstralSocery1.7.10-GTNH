/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Base class for all Journal GUI screens
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui.journal.base;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.gui.journal.bookmark.BookmarkProvider;

/**
 * GuiScreenJournal - Base class for all journal screens (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Bookmark system for navigation</li>
 * <li>Journal texture binding</li>
 * <li>Fragment-based rendering</li>
 * <li>Research progression tracking</li>
 * </ul>
 * <p>
 * <b>Bookmark System:</b>
 * <ul>
 * <li>Bookmarks are registered statically</li>
 * <li>Rendered on left side of journal</li>
 * <li>Click to navigate between screens</li>
 * <li>Can be hidden based on research progress</li>
 * </ul>
 */
@SideOnly(Side.CLIENT)
public abstract class GuiScreenJournal extends GuiWHScreen {

    protected final int bookmarkIndex;

    protected static List<BookmarkProvider> bookmarks = new LinkedList<>();

    protected Map<Point, BookmarkProvider> drawnBookmarks = null;

    /**
     * Constructor
     *
     * @param bookmarkIndex The bookmark index for this screen (-1 for none)
     */
    public GuiScreenJournal(int bookmarkIndex) {
        super(270, 420); // Standard journal size
        this.bookmarkIndex = bookmarkIndex;
    }

    /**
     * Register a bookmark
     *
     * @param bookmark The bookmark provider
     * @return true if registered, false if index already exists
     */
    public static boolean addBookmark(BookmarkProvider bookmark) {
        int index = bookmark.getIndex();
        for (BookmarkProvider bm : bookmarks) {
            if (bm.getIndex() == index) {
                return false;
            }
        }
        return bookmarks.add(bookmark);
    }

    /**
     * Draw bookmarks
     *
     * @param zLevel     Z level for rendering
     * @param mousePoint Mouse position
     */
    protected void drawBookmarks(float zLevel, Point mousePoint) {
        if (drawnBookmarks == null) {
            return;
        }

        for (Map.Entry<Point, BookmarkProvider> entry : drawnBookmarks.entrySet()) {
            Point pos = entry.getKey();
            BookmarkProvider bookmark = entry.getValue();

            // Check if player can see this bookmark
            if (!bookmark.canSee()) {
                continue;
            }

            // Draw bookmark
            ResourceLocation texture = bookmark.getTextureBookmark();
            mc.renderEngine.bindTexture(texture);
            drawTexturedRect(pos.x, pos.y, 12, 20, 0.0F, 0.0F, 1.0F, 1.0F);
        }
    }

    /**
     * Handle bookmark clicks
     *
     * @param mousePoint Mouse position
     * @return true if a bookmark was clicked
     */
    protected boolean handleBookmarkClick(Point mousePoint) {
        if (drawnBookmarks == null) {
            return false;
        }

        for (Map.Entry<Point, BookmarkProvider> entry : drawnBookmarks.entrySet()) {
            Point pos = entry.getKey();
            BookmarkProvider bookmark = entry.getValue();

            if (!bookmark.canSee()) {
                continue;
            }

            // Check if mouse is over bookmark (12x20 size)
            if (mousePoint.x >= pos.x && mousePoint.x < pos.x + 12
                && mousePoint.y >= pos.y
                && mousePoint.y < pos.y + 20) {
                // Open bookmark's GUI
                mc.displayGuiScreen(bookmark.getGuiScreen());
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean handleRightClickClose(int mouseX, int mouseY) {
        // Check if clicking on a bookmark
        Point mousePoint = new Point(mouseX, mouseY);
        if (handleBookmarkClick(mousePoint)) {
            return true;
        }
        return false;
    }
}
