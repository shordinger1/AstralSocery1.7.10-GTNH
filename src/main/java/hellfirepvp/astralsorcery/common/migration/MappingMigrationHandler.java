/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

import java.util.LinkedList;

import net.minecraft.util.ResourceLocation;

import hellfirepvp.astralsorcery.AstralSorcery;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MappingMigrationHandler
 * Created by HellFirePvP
 * Date: 03.07.2017 / 12:50
 */
public class MappingMigrationHandler {

    // this is not generified/abstracted yet due to lazyness and it's just 1 thing to migrate so........

    private static final int DATA_FIXER_VERSION = 1;
    private static final ResourceLocation ILLUMINATION_POWDER_KEY = new ResourceLocation(
        AstralSorcery.MODID,
        "itemilluminationpowder");

    private static final LinkedList<String> migrationTileNames = new LinkedList<>();

    public static void init() {
        // 1.7.10: DataFixer system doesn't exist in this version
        // Skipping data migration initialization for 1.7.10
        // MappingMigrationHandler instance = new MappingMigrationHandler();
        // MinecraftForge.EVENT_BUS.register(instance);
    }

    // 1.7.10: RegistryEvent doesn't exist, event system is different
    // Removed @SubscribeEvent
    // public void onMissingMapping(RegistryEvent.MissingMappings<Item> event) {
    // for (RegistryEvent.MissingMappings.Mapping<Item> mapping : event.getMappings()) {
    // if (mapping.key.equals(ILLUMINATION_POWDER_KEY)) {
    // mapping.remap(ItemsAS.useableDust);
    // }
    // }
    // }

    public static void listenTileMigration(String name) {
        migrationTileNames.add(name);
    }
}
