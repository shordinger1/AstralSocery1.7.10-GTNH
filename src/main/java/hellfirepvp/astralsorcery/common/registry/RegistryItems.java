/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import static hellfirepvp.astralsorcery.common.lib.ItemsAS.*;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryItems
 * Created by HellFirePvP
 * Date: 07.05.2016 / 15:03
 */
public class RegistryItems {

    public static List<ItemDynamicColor> pendingDynamicColorItems = new LinkedList<>();

    public static Item.ToolMaterial crystalToolMaterial;
    public static EnumRarity rarityCelestial, rarityRelic;
    public static ItemArmor.ArmorMaterial imbuedLeatherMaterial;

    public static CreativeTabs creativeTabAstralSorcery, creativeTabAstralSorceryPapers,
        creativeTabAstralSorceryTunedCrystals;

    public static void setupDefaults() {
        creativeTabAstralSorcery = new CreativeTabs(AstralSorcery.MODID) {

            @Override
            public Item getTabIconItem() {
                return new ItemStack(journal).getItem();
            }
        };
        creativeTabAstralSorceryPapers = new CreativeTabs(AstralSorcery.MODID + ".papers") {

            @Override
            public Item getTabIconItem() {
                return new ItemStack(constellationPaper).getItem();
            }
        };
        creativeTabAstralSorceryTunedCrystals = new CreativeTabs(AstralSorcery.MODID + ".crystals") {

            @Override
            public Item getTabIconItem() {
                return new ItemStack(tunedRockCrystal).getItem();
            }
        };

        crystalToolMaterial = EnumHelper.addToolMaterial("CRYSTAL", 3, 1000, 20.0F, 5.5F, 40);
        crystalToolMaterial.setRepairItem(null);

        rarityCelestial = EnumHelper.addRarity("CELESTIAL", EnumChatFormatting.BLUE, "Celestial");
        rarityRelic = EnumHelper.addRarity("AS_RELIC", EnumChatFormatting.GOLD, "Relic");

        // 1.7.10: addArmorMaterial only takes 4 parameters (name, durability, reductionAmounts, enchantability)
        // Texture name and sound are not parameters in 1.7.10
        imbuedLeatherMaterial = EnumHelper.addArmorMaterial("AS_IMBUEDLEATHER", 26, new int[] { 0, 0, 7, 0 }, 30);
        // 1.7.10: ArmorMaterial doesn't have setRepairItem - repair is handled through recipes
    }

    public static void init() {
        registerItems();

        registerBlockItems();

        registerDispenseBehavior();
    }

    // "Normal" items
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
        // roseBranchBow = registerItem(new ItemRoseBranchBow());
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

    // Items associated to blocks/itemblocks
    private static void registerBlockItems() {
        for (Block block : RegistryBlocks.defaultItemBlocksToRegister) {
            RegistryItems.registerDefaultItemBlock(block);
        }
        // 1.7.10: ItemSlab requires 4 parameters (Block, BlockSlab, BlockSlab, boolean)
        registerItem(
            new ItemSlab(BlocksAS.blockMarbleSlab, BlocksAS.blockMarbleSlab, BlocksAS.blockMarbleDoubleSlab, false));
        for (Block block : RegistryBlocks.customNameItemBlocksToRegister) {
            RegistryItems.registerCustomNameItemBlock(block);
        }

        registerItem(new ItemBlockRitualPedestal());
        registerItem(new ItemBlockAltar());

        registerItem(new ItemCollectorCrystal(BlocksAS.collectorCrystal));
        registerItem(new ItemCollectorCrystal(BlocksAS.celestialCollectorCrystal));
    }

    private static void registerDispenseBehavior() {
        // 1.7.10: dispenseBehaviorRegistry is lowercase and uses putObject()
        BlockDispenser.dispenseBehaviorRegistry.putObject(useableDust, useableDust);
    }

    private static <T extends Block> void registerCustomNameItemBlock(T block) {
        registerItem(
            new ItemBlockCustomName(block),
            block.getClass()
                .getSimpleName()
                .toLowerCase());
    }

    private static <T extends Block> void registerDefaultItemBlock(T block) {
        registerDefaultItemBlock(
            block,
            block.getClass()
                .getSimpleName()
                .toLowerCase());
    }

    private static <T extends Block> void registerDefaultItemBlock(T block, String name) {
        registerItem(new ItemBlock(block), name);
    }

    private static <T extends Item> T registerItem(T item, String name) {
        item.setUnlocalizedName(name);
        // 1.7.10: setRegistryName() doesn't exist - GameRegistry.registerItem() handles the mapping
        register(item, name);
        return item;
    }

    private static <T extends Item> T registerItem(T item) {
        String simpleName = item.getClass()
            .getSimpleName()
            .toLowerCase();
        if (item instanceof ItemBlock) {
            // 1.7.10: ItemBlock uses field_150939_a instead of getBlock() method
            simpleName = ((ItemBlock) item).field_150939_a.getClass()
                .getSimpleName()
                .toLowerCase();
        }
        return registerItem(item, simpleName);
    }

    /*
     * private static <T extends IForgeRegistryEntry> T registerItem(String modId, T item) {
     * return registerItem(modId, item, item.getClass().getSimpleName());
     * }
     * private static <T extends IForgeRegistryEntry> T registerItem(String modId, T item, String name) {
     * try {
     * LoadController modController = (LoadController) Loader.class.getField("modController").get(Loader.PERK_TREE());
     * Object oldMod = modController.getClass().getField("activeContainer").get(modController);
     * modController.getClass().getField("activeContainer").set(modController,
     * Loader.PERK_TREE().getIndexedModList().get(modId));
     * register(item, name);
     * modController.getClass().getField("activeContainer").set(modController, oldMod);
     * return item;
     * } catch (Exception exc) {
     * AstralSorcery.log.error("Could not register item with name " + name);
     * return null;
     * }
     * }
     */

    // 1.7.10: IForgeRegistryEntry doesn't exist - use direct registration methods
    private static void register(Item item, String name) {
        item.setUnlocalizedName(name);
        GameRegistry.registerItem(item, name);
        registerItemInformations(item, name);
        if (item instanceof ItemDynamicColor) {
            pendingDynamicColorItems.add((ItemDynamicColor) item);
        }
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
