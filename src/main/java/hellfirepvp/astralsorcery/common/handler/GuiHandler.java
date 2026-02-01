/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * GUI handler for mod GUIs
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.IGuiHandler;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.Constants;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * GUI handler for Astral Sorcery
 *
 * Handles opening GUIs on both client and server.
 * Based on BartWorks GuiHandler.java
 */
public class GuiHandler implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        LogHelper.debug("Opening server GUI ID: " + ID);

        switch (ID) {
            case Constants.GUI_ID_ALTAR:
                // Return container for altar
                LogHelper.debug("Opening altar GUI at (" + x + ", " + y + ", " + z + ")");
                // return new ContainerAltar(player, world, x, y, z);
                return null;

            case Constants.GUI_ID_OBSERVATORY:
                // Return container for observatory
                LogHelper.debug("Opening observatory GUI at (" + x + ", " + y + ", " + z + ")");
                // return new ContainerObservatory(player, world, x, y, z);
                return null;

            case Constants.GUI_ID_TREE_BEACON:
                // Return container for tree beacon
                LogHelper.debug("Opening tree beacon GUI at (" + x + ", " + y + ", " + z + ")");
                // return new ContainerTreeBeacon(player, world, x, y, z);
                return null;

            case Constants.GUI_ID_CELESTIAL_GATEWAY:
                // Return container for celestial gateway
                LogHelper.debug("Opening celestial gateway GUI at (" + x + ", " + y + ", " + z + ")");
                // return new ContainerCelestialGateway(player, world, x, y, z);
                return null;

            default:
                LogHelper.warn("Unknown GUI ID: " + ID);
                return null;
        }
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        LogHelper.debug("Opening client GUI ID: " + ID);

        switch (ID) {
            case Constants.GUI_ID_ALTAR:
                // Return GUI for altar
                LogHelper.debug("Opening altar GUI at (" + x + ", " + y + ", " + z + ")");
                // return new GuiAltar(player, world, x, y, z);
                return null;

            case Constants.GUI_ID_OBSERVATORY:
                // Return GUI for observatory
                LogHelper.debug("Opening observatory GUI at (" + x + ", " + y + ", " + z + ")");
                // return new GuiObservatory(player, world, x, y, z);
                return null;

            case Constants.GUI_ID_TREE_BEACON:
                // Return GUI for tree beacon
                LogHelper.debug("Opening tree beacon GUI at (" + x + ", " + y + ", " + z + ")");
                // return new GuiTreeBeacon(player, world, x, y, z);
                return null;

            case Constants.GUI_ID_CELESTIAL_GATEWAY:
                // Return GUI for celestial gateway
                LogHelper.debug("Opening celestial gateway GUI at (" + x + ", " + y + ", " + z + ")");
                // return new GuiCelestialGateway(player, world, x, y, z);
                return null;

            default:
                LogHelper.warn("Unknown GUI ID: " + ID);
                return null;
        }
    }

    /**
     * Open a GUI by ID
     *
     * @param player The player
     * @param guiId  The GUI ID
     * @param world  The world
     * @param x      X coordinate
     * @param y      Y coordinate
     * @param z      Z coordinate
     */
    public static void openGui(EntityPlayer player, int guiId, World world, int x, int y, int z) {
        if (world.isRemote) {
            return; // Client side only
        }

        player.openGui(AstralSorcery.instance, guiId, world, x, y, z);
        LogHelper.debug("Opened GUI " + guiId + " for player: " + player.getCommandSenderName());
    }

    /**
     * Open the altar GUI
     */
    public static void openAltarGui(EntityPlayer player, World world, int x, int y, int z) {
        openGui(player, Constants.GUI_ID_ALTAR, world, x, y, z);
    }

    /**
     * Open the observatory GUI
     */
    public static void openObservatoryGui(EntityPlayer player, World world, int x, int y, int z) {
        openGui(player, Constants.GUI_ID_OBSERVATORY, world, x, y, z);
    }

    /**
     * Open the tree beacon GUI
     */
    public static void openTreeBeaconGui(EntityPlayer player, World world, int x, int y, int z) {
        openGui(player, Constants.GUI_ID_TREE_BEACON, world, x, y, z);
    }

    /**
     * Open the celestial gateway GUI
     */
    public static void openCelestialGatewayGui(EntityPlayer player, World world, int x, int y, int z) {
        openGui(player, Constants.GUI_ID_CELESTIAL_GATEWAY, world, x, y, z);
    }
}
