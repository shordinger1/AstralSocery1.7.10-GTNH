/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.util.item.ItemRenderRegistry;
import hellfirepvp.astralsorcery.common.integrations.mods.geolosys.BlockGeolosysSampleCluster;
import hellfirepvp.astralsorcery.common.integrations.mods.geolosys.TESRGeolosysSampleCluster;
import hellfirepvp.astralsorcery.common.integrations.mods.geolosys.TileGeolosysSampleCluster;
import hellfirepvp.astralsorcery.common.registry.RegistryBlocks;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ModIntegrationGeolosys
 * Created by HellFirePvP
 * Date: 03.10.2017 / 17:21
 */
public class ModIntegrationGeolosys {

    public static Block geolosysSample;

    public static void registerGeolosysSampleBlock() {
        geolosysSample = RegistryBlocks.registerBlock(new BlockGeolosysSampleCluster());
        RegistryBlocks.queueDefaultItemBlock(geolosysSample);
        RegistryBlocks.registerTile(TileGeolosysSampleCluster.class);
    }

    @SideOnly(Side.CLIENT)
    public static void registerGeolosysSampleRender() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileGeolosysSampleCluster.class, new TESRGeolosysSampleCluster());
    }

    @SideOnly(Side.CLIENT)
    public static void registerGeolosysSampleItemRenderer() {
        ItemRenderRegistry.register(Item.getItemFromBlock(geolosysSample), new TESRGeolosysSampleCluster());
    }

    /**
     * Hide Geolosys sample from NEI using IMC
     * 1.7.10: Uses NEI instead of JEI (which doesn't exist for 1.7.10)
     */
    @Optional.Method(modid = "NotEnoughItems")
    public static void hideNEIGeolosysSample() {
        try {
            String itemName = geolosysSample.getUnlocalizedName();
            FMLInterModComms.sendMessage("NotEnoughItems", "hide", itemName);
        } catch (Exception e) {
            // Silently fail if item can't be hidden
        }
    }

}
