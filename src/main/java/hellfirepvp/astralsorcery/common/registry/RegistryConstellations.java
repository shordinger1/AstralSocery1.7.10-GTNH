/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Constellation registry - simplified for 1.7.10
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import java.awt.Color;

import hellfirepvp.astralsorcery.common.constellation.ConstellationBase;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.constellation.MoonPhase;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * Registry for all Astral Sorcery constellations
 *
 * 1.7.10 Migration:
 * - Removed cape effects (will add later)
 * - Removed map effects (will add later)
 * - Removed spell effects
 * - Simplified constellation registration
 * - Removed OreDictAlias (use Items directly)
 * - Removed @Optional methods
 */
public class RegistryConstellations {

    // ========== Major Constellations ==========
    public static IMajorConstellation discidia;
    public static IMajorConstellation armara;
    public static IMajorConstellation vicio;
    public static IMajorConstellation aevitas;
    public static IMajorConstellation evorsio;
    public static IMajorConstellation vectras; // Phase 2.3: Teleportation constellation

    // ========== Weak Constellations ==========
    public static IWeakConstellation lucerna;
    public static IWeakConstellation mineralis;
    public static IWeakConstellation horologium;
    public static IWeakConstellation octans;
    public static IWeakConstellation bootes;
    public static IWeakConstellation fornax;
    public static IWeakConstellation pelotrio;

    // ========== Minor Constellations ==========
    public static IMinorConstellation gelu;
    public static IMinorConstellation ulteria;
    public static IMinorConstellation alcara;
    public static IMinorConstellation vorux;

    /**
     * Initialize all constellations
     */
    public static void init() {
        LogHelper.entry("RegistryConstellations.init");

        buildConstellations();
        registerConstellations();

        // TODO: Register signature items (requires OreDict)

        LogHelper.info("Registered " + ConstellationRegistry.getConstellationCount() + " constellations");
        LogHelper.exit("RegistryConstellations.init");
    }

    /**
     * Build all constellations with their star patterns
     */
    private static void buildConstellations() {
        StarLocation sl1, sl2, sl3, sl4, sl5, sl6, sl7, sl8, sl9;

        // ========== Major Constellations ==========

        discidia = new ConstellationBase.Major("discidia", new Color(0xE01903));
        sl1 = discidia.addStar(7, 2);
        sl2 = discidia.addStar(3, 6);
        sl3 = discidia.addStar(5, 12);
        sl4 = discidia.addStar(20, 11);
        sl5 = discidia.addStar(15, 17);
        sl6 = discidia.addStar(26, 21);
        sl7 = discidia.addStar(23, 27);
        sl8 = discidia.addStar(15, 25);
        discidia.addConnection(sl1, sl2);
        discidia.addConnection(sl2, sl3);
        discidia.addConnection(sl2, sl4);
        discidia.addConnection(sl4, sl5);
        discidia.addConnection(sl5, sl7);
        discidia.addConnection(sl6, sl7);
        discidia.addConnection(sl7, sl8);

        armara = new ConstellationBase.Major("armara", new Color(0xB7BBB8));
        sl1 = armara.addStar(8, 4);
        sl2 = armara.addStar(9, 15);
        sl3 = armara.addStar(11, 26);
        sl4 = armara.addStar(19, 25);
        sl5 = armara.addStar(23, 14);
        sl6 = armara.addStar(23, 4);
        sl7 = armara.addStar(15, 7);
        armara.addConnection(sl1, sl2);
        armara.addConnection(sl2, sl3);
        armara.addConnection(sl3, sl4);
        armara.addConnection(sl4, sl5);
        armara.addConnection(sl5, sl6);
        armara.addConnection(sl6, sl7);
        armara.addConnection(sl7, sl1);
        armara.addConnection(sl2, sl5);
        armara.addConnection(sl2, sl7);
        armara.addConnection(sl5, sl7);

        vicio = new ConstellationBase.Major("vicio", new Color(0x00BDAD));
        sl1 = vicio.addStar(3, 8);
        sl2 = vicio.addStar(13, 9);
        sl3 = vicio.addStar(6, 23);
        sl4 = vicio.addStar(14, 16);
        sl5 = vicio.addStar(23, 24);
        sl6 = vicio.addStar(22, 16);
        sl7 = vicio.addStar(24, 4);
        vicio.addConnection(sl1, sl2);
        vicio.addConnection(sl2, sl7);
        vicio.addConnection(sl3, sl4);
        vicio.addConnection(sl4, sl7);
        vicio.addConnection(sl5, sl6);
        vicio.addConnection(sl6, sl7);

        aevitas = new ConstellationBase.Major("aevitas", new Color(0x2EE400));
        sl1 = aevitas.addStar(15, 14);
        sl2 = aevitas.addStar(7, 12);
        sl3 = aevitas.addStar(3, 6);
        sl4 = aevitas.addStar(21, 8);
        sl5 = aevitas.addStar(25, 2);
        sl6 = aevitas.addStar(13, 21);
        sl7 = aevitas.addStar(9, 26);
        sl8 = aevitas.addStar(17, 28);
        sl9 = aevitas.addStar(27, 17);
        aevitas.addConnection(sl1, sl2);
        aevitas.addConnection(sl2, sl3);
        aevitas.addConnection(sl1, sl4);
        aevitas.addConnection(sl4, sl5);
        aevitas.addConnection(sl1, sl6);
        aevitas.addConnection(sl6, sl7);
        aevitas.addConnection(sl6, sl8);
        aevitas.addConnection(sl4, sl9);

        evorsio = new ConstellationBase.Major("evorsio", new Color(0xA00100));
        sl1 = evorsio.addStar(13, 16);
        sl2 = evorsio.addStar(18, 6);
        sl3 = evorsio.addStar(26, 4);
        sl4 = evorsio.addStar(24, 13);
        sl5 = evorsio.addStar(2, 18);
        sl6 = evorsio.addStar(4, 27);
        sl7 = evorsio.addStar(11, 24);
        evorsio.addConnection(sl1, sl2);
        evorsio.addConnection(sl1, sl3);
        evorsio.addConnection(sl1, sl4);
        evorsio.addConnection(sl1, sl5);
        evorsio.addConnection(sl1, sl6);
        evorsio.addConnection(sl1, sl7);

        // Phase 2.3: Vectras - Teleportation constellation
        vectras = new ConstellationBase.Major("vectras", new Color(0x00FF66));
        sl1 = vectras.addStar(13, 5);
        sl2 = vectras.addStar(17, 6);
        sl3 = vectras.addStar(21, 9);
        sl4 = vectras.addStar(9, 12);
        sl5 = vectras.addStar(5, 15);
        sl6 = vectras.addStar(10, 18);
        sl7 = vectras.addStar(18, 22);
        vectras.addConnection(sl1, sl2);
        vectras.addConnection(sl2, sl3);
        vectras.addConnection(sl3, sl4);
        vectras.addConnection(sl4, sl5);
        vectras.addConnection(sl5, sl6);
        vectras.addConnection(sl6, sl7);
        vectras.addConnection(sl7, sl1);

        // ========== Weak Constellations ==========

        lucerna = new ConstellationBase.Weak("lucerna", new Color(0xFFE709));
        sl1 = lucerna.addStar(15, 13);
        sl2 = lucerna.addStar(3, 5);
        sl3 = lucerna.addStar(25, 3);
        sl4 = lucerna.addStar(28, 16);
        sl5 = lucerna.addStar(22, 27);
        sl6 = lucerna.addStar(6, 26);
        lucerna.addConnection(sl1, sl2);
        lucerna.addConnection(sl1, sl3);
        lucerna.addConnection(sl1, sl4);
        lucerna.addConnection(sl1, sl5);
        lucerna.addConnection(sl1, sl6);

        mineralis = new ConstellationBase.Weak("mineralis", new Color(0xCB7D0A));
        sl1 = mineralis.addStar(16, 2);
        sl2 = mineralis.addStar(8, 8);
        sl3 = mineralis.addStar(9, 22);
        sl4 = mineralis.addStar(15, 29);
        sl5 = mineralis.addStar(23, 21);
        sl6 = mineralis.addStar(24, 9);
        mineralis.addConnection(sl1, sl2);
        mineralis.addConnection(sl2, sl3);
        mineralis.addConnection(sl3, sl4);
        mineralis.addConnection(sl4, sl5);
        mineralis.addConnection(sl5, sl6);
        mineralis.addConnection(sl6, sl1);
        mineralis.addConnection(sl1, sl4);

        horologium = new ConstellationBase.Weak("horologium", new Color(0x7D16B4));
        sl1 = horologium.addStar(7, 6);
        sl2 = horologium.addStar(22, 5);
        sl3 = horologium.addStar(5, 27);
        sl4 = horologium.addStar(23, 25);
        horologium.addConnection(sl1, sl2);
        horologium.addConnection(sl2, sl3);
        horologium.addConnection(sl3, sl4);
        horologium.addConnection(sl4, sl1);

        octans = new ConstellationBase.Weak("octans", new Color(0x706EFF));
        sl1 = octans.addStar(3, 6);
        sl2 = octans.addStar(11, 11);
        sl3 = octans.addStar(18, 4);
        sl4 = octans.addStar(18, 29);
        octans.addConnection(sl1, sl2);
        octans.addConnection(sl2, sl3);
        octans.addConnection(sl3, sl4);
        octans.addConnection(sl2, sl4);

        bootes = new ConstellationBase.Weak("bootes", new Color(0xD41CD6));
        sl1 = bootes.addStar(9, 22);
        sl2 = bootes.addStar(3, 14);
        sl3 = bootes.addStar(22, 27);
        sl4 = bootes.addStar(16, 5);
        sl5 = bootes.addStar(26, 3);
        sl6 = bootes.addStar(24, 11);
        bootes.addConnection(sl1, sl2);
        bootes.addConnection(sl1, sl3);
        bootes.addConnection(sl1, sl4);
        bootes.addConnection(sl1, sl6);
        bootes.addConnection(sl4, sl5);
        bootes.addConnection(sl5, sl6);

        fornax = new ConstellationBase.Weak("fornax", new Color(0xFF4E1B));
        sl1 = fornax.addStar(4, 25);
        sl2 = fornax.addStar(14, 28);
        sl3 = fornax.addStar(28, 21);
        sl4 = fornax.addStar(12, 18);
        sl5 = fornax.addStar(16, 16);
        fornax.addConnection(sl1, sl2);
        fornax.addConnection(sl2, sl3);
        fornax.addConnection(sl2, sl4);
        fornax.addConnection(sl2, sl5);

        pelotrio = new ConstellationBase.Weak("pelotrio", new Color(0xEC006B));
        sl1 = pelotrio.addStar(4, 7);
        sl2 = pelotrio.addStar(12, 2);
        sl3 = pelotrio.addStar(20, 3);
        sl4 = pelotrio.addStar(15, 13);
        sl5 = pelotrio.addStar(10, 23);
        sl6 = pelotrio.addStar(26, 11);
        pelotrio.addConnection(sl1, sl2);
        pelotrio.addConnection(sl2, sl4);
        pelotrio.addConnection(sl3, sl4);
        pelotrio.addConnection(sl4, sl5);
        pelotrio.addConnection(sl4, sl6);
        pelotrio.addConnection(sl6, sl3);

        // ========== Minor Constellations ==========

        gelu = new ConstellationBase.Minor(
            "gelu",
            new Color(0x758BA8),
            MoonPhase.NEW,
            MoonPhase.WAXING1_4,
            MoonPhase.WAXING1_2);
        sl1 = gelu.addStar(8, 7);
        sl2 = gelu.addStar(28, 8);
        sl3 = gelu.addStar(23, 21);
        sl4 = gelu.addStar(3, 22);
        sl5 = gelu.addStar(17, 17);
        sl6 = gelu.addStar(16, 13);
        gelu.addConnection(sl1, sl2);
        gelu.addConnection(sl3, sl4);
        gelu.addConnection(sl2, sl5);
        gelu.addConnection(sl4, sl6);

        ulteria = new ConstellationBase.Minor(
            "ulteria",
            new Color(0x347463),
            MoonPhase.WANING1_2,
            MoonPhase.WANING3_4,
            MoonPhase.NEW);
        sl1 = ulteria.addStar(14, 9);
        sl2 = ulteria.addStar(17, 16);
        sl3 = ulteria.addStar(25, 19);
        sl4 = ulteria.addStar(7, 21);
        sl5 = ulteria.addStar(22, 25);
        ulteria.addConnection(sl1, sl2);
        ulteria.addConnection(sl2, sl3);
        ulteria.addConnection(sl4, sl5);

        alcara = new ConstellationBase.Minor("alcara", new Color(0x802952), MoonPhase.WANING1_2, MoonPhase.WAXING1_2);
        sl1 = alcara.addStar(6, 27);
        sl2 = alcara.addStar(14, 20);
        sl3 = alcara.addStar(17, 24);
        sl4 = alcara.addStar(10, 18);
        sl5 = alcara.addStar(7, 5);
        sl6 = alcara.addStar(17, 9);
        alcara.addConnection(sl1, sl2);
        alcara.addConnection(sl2, sl3);
        alcara.addConnection(sl1, sl4);
        alcara.addConnection(sl4, sl5);
        alcara.addConnection(sl4, sl6);

        vorux = new ConstellationBase.Minor(
            "vorux",
            new Color(0xA8881E),
            MoonPhase.FULL,
            MoonPhase.WAXING3_4,
            MoonPhase.WANING3_4);
        sl1 = vorux.addStar(3, 21);
        sl2 = vorux.addStar(7, 7);
        sl3 = vorux.addStar(14, 15);
        sl4 = vorux.addStar(18, 5);
        sl5 = vorux.addStar(25, 16);
        sl6 = vorux.addStar(16, 26);
        sl7 = vorux.addStar(27, 2);
        vorux.addConnection(sl1, sl2);
        vorux.addConnection(sl2, sl3);
        vorux.addConnection(sl3, sl4);
        vorux.addConnection(sl4, sl5);
        vorux.addConnection(sl5, sl6);
        vorux.addConnection(sl4, sl7);
    }

    /**
     * Register all constellations with the registry
     */
    private static void registerConstellations() {
        // Major constellations
        ConstellationRegistry.registerConstellation(discidia);
        ConstellationRegistry.registerConstellation(armara);
        ConstellationRegistry.registerConstellation(vicio);
        ConstellationRegistry.registerConstellation(aevitas);
        ConstellationRegistry.registerConstellation(evorsio);
        ConstellationRegistry.registerConstellation(vectras); // Phase 2.3

        // Weak constellations
        ConstellationRegistry.registerConstellation(lucerna);
        ConstellationRegistry.registerConstellation(mineralis);
        ConstellationRegistry.registerConstellation(horologium);
        ConstellationRegistry.registerConstellation(octans);
        ConstellationRegistry.registerConstellation(bootes);
        ConstellationRegistry.registerConstellation(fornax);
        ConstellationRegistry.registerConstellation(pelotrio);

        // Minor constellations
        ConstellationRegistry.registerConstellation(gelu);
        ConstellationRegistry.registerConstellation(ulteria);
        ConstellationRegistry.registerConstellation(alcara);
        ConstellationRegistry.registerConstellation(vorux);

        // Phase 2.3: Additional minor constellations from our implementation
        // These match the standalone classes we created
        // octris - Already registered as octans
        // attertea - Not yet in original 1.12.2, skipping for now
    }

    /**
     * Initialize constellation signature items
     * TODO: Implement when OreDict system is ready
     */
    public static void initConstellationSignatures() {
        // TODO: Add signature items for each constellation
        // Requires OreDictAlias to be implemented
        LogHelper.info("Constellation signature items not yet implemented for 1.7.10");
    }

}
