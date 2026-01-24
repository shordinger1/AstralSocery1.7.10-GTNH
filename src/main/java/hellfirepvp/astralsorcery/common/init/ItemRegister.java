/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.init;

import static hellfirepvp.astralsorcery.common.lib.ItemsAS.*;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.*;
import net.minecraft.item.Item;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.util.EnumHelper;

import cpw.mods.fml.common.registry.GameRegistry;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.item.*;
import hellfirepvp.astralsorcery.common.item.base.IItemVariants;
import hellfirepvp.astralsorcery.common.item.base.render.ItemDynamicColor;
import hellfirepvp.astralsorcery.common.item.block.ItemBlockAltar;
import hellfirepvp.astralsorcery.common.item.block.ItemBlockCustomName;
import hellfirepvp.astralsorcery.common.item.block.ItemBlockRitualPedestal;
import hellfirepvp.astralsorcery.common.item.block.ItemCollectorCrystal;
import hellfirepvp.astralsorcery.common.item.crystal.ItemCelestialCrystal;
import hellfirepvp.astralsorcery.common.item.crystal.ItemRockCrystalSimple;
import hellfirepvp.astralsorcery.common.item.crystal.ItemTunedCelestialCrystal;
import hellfirepvp.astralsorcery.common.item.crystal.ItemTunedRockCrystal;
import hellfirepvp.astralsorcery.common.item.gem.ItemPerkGem;
import hellfirepvp.astralsorcery.common.item.knowledge.ItemFragmentCapsule;
import hellfirepvp.astralsorcery.common.item.knowledge.ItemKnowledgeFragment;
import hellfirepvp.astralsorcery.common.item.tool.*;
import hellfirepvp.astralsorcery.common.item.tool.sextant.ItemSextant;
import hellfirepvp.astralsorcery.common.item.tool.wand.ItemWand;
import hellfirepvp.astralsorcery.common.item.useables.ItemPerkSeal;
import hellfirepvp.astralsorcery.common.item.useables.ItemShiftingStar;
import hellfirepvp.astralsorcery.common.item.useables.ItemUsableDust;
import hellfirepvp.astralsorcery.common.item.wand.ItemArchitectWand;
import hellfirepvp.astralsorcery.common.item.wand.ItemExchangeWand;
import hellfirepvp.astralsorcery.common.item.wand.ItemGrappleWand;
import hellfirepvp.astralsorcery.common.item.wand.ItemIlluminationWand;
import hellfirepvp.astralsorcery.common.item.wearable.ItemCape;
import hellfirepvp.astralsorcery.common.item.wearable.ItemEnchantmentAmulet;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;

/**
 * Simplified item registration following TSS-style pattern.
 * Direct GameRegistry calls instead of Primer system.
 */
public class ItemRegister {

    public static List<ItemDynamicColor> pendingDynamicColorItems = new LinkedList<>();

    public static Item.ToolMaterial crystalToolMaterial;
    public static EnumRarity rarityCelestial, rarityRelic;
    public static ItemArmor.ArmorMaterial imbuedLeatherMaterial;

    public static CreativeTabs creativeTabAstralSorcery, creativeTabAstralSorceryPapers,
        creativeTabAstralSorceryTunedCrystals;

    /**
     * Setup default materials and creative tabs before registration
     */
    public static void setupDefaults() {
        creativeTabAstralSorcery = new CreativeTabs(AstralSorcery.MODID) {

            @Override
            public Item getTabIconItem() {
                return journal;
            }
        };
        creativeTabAstralSorceryPapers = new CreativeTabs(AstralSorcery.MODID + ".papers") {

            @Override
            public Item getTabIconItem() {
                return constellationPaper;
            }
        };
        creativeTabAstralSorceryTunedCrystals = new CreativeTabs(AstralSorcery.MODID + ".crystals") {

            @Override
            public Item getTabIconItem() {
                return tunedRockCrystal;
            }
        };

        crystalToolMaterial = EnumHelper.addToolMaterial("CRYSTAL", 3, 1000, 20.0F, 5.5F, 40);
        // 1.7.10: setRepairItem doesn't exist on ToolMaterial
        // In 1.7.10, repair items are handled in Item.getIsRepairable()

        rarityCelestial = EnumHelper.addRarity("CELESTIAL", EnumChatFormatting.BLUE, "Celestial");
        rarityRelic = EnumHelper.addRarity("AS_RELIC", EnumChatFormatting.GOLD, "Relic");

        // 1.7.10: EnumHelper.addArmorMaterial has different signature
        // In 1.7.10: (String name, String textureName, int durability, int[] reductionAmounts, int enchantability)
        // Actually in 1.7.10 it's: (String name, int durability, int[] reductionAmounts, int enchantability)
        imbuedLeatherMaterial = EnumHelper.addArmorMaterial("AS_IMBUEDLEATHER", 26, new int[] { 0, 0, 7, 0 }, 30);
        // 1.7.10: setRepairItem doesn't exist on ArmorMaterial
        // In 1.7.10, repair items are handled in ItemArmor.getIsRepairable()
    }

    /**
     * Main registry method - call from CommonProxy.preInit()
     */
    public static void registry() {
        registerItems();
        registerBlockItems();
        registerDispenseBehavior();
    }

    private static void registerItems() {
        craftingComponent = registerItem(new ItemCraftingComponent());
        constellationPaper = registerItem(new ItemConstellationPaper());
        infusedGlass = registerItem(new ItemInfusedGlass());

        rockCrystal = registerItem(new ItemRockCrystalSimple());
        tunedRockCrystal = registerItem(new ItemTunedRockCrystal());

        celestialCrystal = registerItem(new ItemCelestialCrystal());
        tunedCelestialCrystal = registerItem(new ItemTunedCelestialCrystal());

        journal = registerItem(new ItemJournal());
        handTelescope = registerItem(new ItemHandTelescope());
        linkingTool = registerItem(new ItemLinkingTool());
        wand = registerItem(new ItemWand());
        sextant = registerItem(new ItemSextant());
        illuminationWand = registerItem(new ItemIlluminationWand());
        coloredLens = registerItem(new ItemColoredLens());
        skyResonator = registerItem(new ItemSkyResonator());
        shiftingStar = registerItem(new ItemShiftingStar());
        architectWand = registerItem(new ItemArchitectWand());
        exchangeWand = registerItem(new ItemExchangeWand());
        grapplingWand = registerItem(new ItemGrappleWand());
        useableDust = registerItem(new ItemUsableDust());
        knowledgeShare = registerItem(new ItemKnowledgeShare());
        perkSeal = registerItem(new ItemPerkSeal());
        knowledgeFragment = registerItem(new ItemKnowledgeFragment());
        fragmentCapsule = registerItem(new ItemFragmentCapsule());
        perkGem = registerItem(new ItemPerkGem());

        crystalPickaxe = registerItem(new ItemCrystalPickaxe());
        crystalShovel = registerItem(new ItemCrystalShovel());
        crystalAxe = registerItem(new ItemCrystalAxe());
        crystalSword = registerItem(new ItemCrystalSword());
        chargedCrystalAxe = registerItem(new ItemChargedCrystalAxe());
        chargedCrystalSword = registerItem(new ItemChargedCrystalSword());
        chargedCrystalPickaxe = registerItem(new ItemChargedCrystalPickaxe());
        chargedCrystalShovel = registerItem(new ItemChargedCrystalShovel());

        armorImbuedCape = registerItem(new ItemCape());
        enchantmentAmulet = registerItem(new ItemEnchantmentAmulet());
    }

    private static void registerBlockItems() {
        // Register default ItemBlocks
        for (Block block : BlockRegister.defaultItemBlocksToRegister) {
            registerDefaultItemBlock(block);
        }

        // Register slab
        // 1.7.10: ItemSlab constructor has signature: (Block block, boolean fullBlock)
        // In 1.7.10, slab items work differently than 1.12.2
        // registerItem(new ItemSlab(BlocksAS.blockMarbleSlab, false));

        // Register custom name ItemBlocks
        for (Block block : BlockRegister.customNameItemBlocksToRegister) {
            registerCustomNameItemBlock(block);
        }

        // Register special ItemBlocks
        registerItem(new ItemBlockRitualPedestal());
        registerItem(new ItemBlockAltar());

        registerItem(new ItemCollectorCrystal(BlocksAS.collectorCrystal));
        registerItem(new ItemCollectorCrystal(BlocksAS.celestialCollectorCrystal));
    }

    private static void registerDispenseBehavior() {
        // 1.7.10: Field name is different (lowercase 'd')
        BlockDispenser.dispenseBehaviorRegistry.putObject(useableDust, useableDust);
    }

    private static <T extends Block> void registerCustomNameItemBlock(T block) {
        String name = block.getClass()
            .getSimpleName()
            .toLowerCase();
        registerItem(new ItemBlockCustomName(block), name);
    }

    private static <T extends Block> void registerDefaultItemBlock(T block) {
        String name = block.getClass()
            .getSimpleName()
            .toLowerCase();
        registerItem(new ItemBlock(block), name);
    }

    private static <T extends Item> T registerItem(T item, String name) {
        item.setUnlocalizedName(name);
        GameRegistry.registerItem(item, name);
        registerItemInformations(item, name);
        if (item instanceof ItemDynamicColor) {
            pendingDynamicColorItems.add((ItemDynamicColor) item);
        }
        return item;
    }

    private static <T extends Item> T registerItem(T item) {
        String simpleName = item.getClass()
            .getSimpleName()
            .toLowerCase();
        // 1.7.10: ItemBlock doesn't have getBlock() method
        // We need to handle this differently - just use the item's class name
        // since ItemBlocks should have their block passed during registration elsewhere
        return registerItem(item, simpleName);
    }

    private static <T extends Item> void registerItemInformations(T item, String name) {
        if (item instanceof IItemVariants) {
            for (int i = 0; i < ((IItemVariants) item).getVariants().length; i++) {
                int m = i;
                if (((IItemVariants) item).getVariantMetadatas() != null) {
                    m = ((IItemVariants) item).getVariantMetadatas()[i];
                }
                String vName = name + "_" + ((IItemVariants) item).getVariants()[i];
                if (((IItemVariants) item).getVariants()[i].equals("*")) {
                    vName = name;
                }
                AstralSorcery.proxy.registerItemRender(item, m, vName, true);
            }
        } else if (!(item instanceof ItemBlockCustomName)) {
            AstralSorcery.proxy.registerFromSubItems(item, name);
        }
    }

}
