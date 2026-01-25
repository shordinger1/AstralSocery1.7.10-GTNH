/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client;
// TODO: Forge fluid system - manual review needed

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import hellfirepvp.astralsorcery.client.data.KnowledgeFragmentData;
import hellfirepvp.astralsorcery.client.data.PersistentDataManager;
import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.light.ClientLightbeamHandler;
import hellfirepvp.astralsorcery.client.effect.light.EffectLightning;
import hellfirepvp.astralsorcery.client.event.ClientConnectionEventHandler;
import hellfirepvp.astralsorcery.client.event.ClientGatewayHandler;
import hellfirepvp.astralsorcery.client.event.ClientRenderEventHandler;
import hellfirepvp.astralsorcery.client.gui.GuiJournalConstellationCluster;
import hellfirepvp.astralsorcery.client.gui.GuiJournalKnowledgeIndex;
import hellfirepvp.astralsorcery.client.gui.GuiJournalPerkTree;
import hellfirepvp.astralsorcery.client.gui.GuiJournalProgression;
import hellfirepvp.astralsorcery.client.gui.journal.GuiScreenJournal;
import hellfirepvp.astralsorcery.client.gui.journal.bookmark.BookmarkProvider;
import hellfirepvp.astralsorcery.client.models.obj.OBJModelLibrary;
import hellfirepvp.astralsorcery.client.render.entity.*;
import hellfirepvp.astralsorcery.client.render.tile.*;
import hellfirepvp.astralsorcery.client.util.JournalRecipeDisplayRecovery;
import hellfirepvp.astralsorcery.client.util.camera.ClientCameraManager;
import hellfirepvp.astralsorcery.client.util.item.ItemRendererFilteredTESR;
import hellfirepvp.astralsorcery.client.util.mappings.ClientJournalMapping;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.word.RandomWordGenerator;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.auxiliary.tick.TickManager;
import hellfirepvp.astralsorcery.common.base.Mods;
import hellfirepvp.astralsorcery.common.base.patreon.flare.PatreonFlareManagerClient;
import hellfirepvp.astralsorcery.common.block.BlockDynamicColor;
import hellfirepvp.astralsorcery.common.block.BlockDynamicStateMapper;
import hellfirepvp.astralsorcery.common.block.BlockMachine;
import hellfirepvp.astralsorcery.common.crafting.helper.CraftingAccessManager;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.entities.*;
import hellfirepvp.astralsorcery.common.integrations.ModIntegrationGeolosys;
import hellfirepvp.astralsorcery.common.item.base.IMetaItem;
import hellfirepvp.astralsorcery.common.item.base.IOBJItem;
import hellfirepvp.astralsorcery.common.item.base.render.ItemDynamicColor;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.migration.*;
import hellfirepvp.astralsorcery.common.registry.RegistryBlocks;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.tile.*;
import hellfirepvp.astralsorcery.common.tile.network.TileCollectorCrystal;
import hellfirepvp.astralsorcery.common.tile.network.TileCrystalLens;
import hellfirepvp.astralsorcery.common.tile.network.TileCrystalPrismLens;
import hellfirepvp.astralsorcery.common.util.FileStorageUtil;
import hellfirepvp.astralsorcery.common.util.Provider;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ClientProxy
 * Created by HellFirePvP
 * Date: 07.05.2016 / 00:23
 */
public class ClientProxy extends CommonProxy {

    // Marks if the client is connected and received all server data from AS' serverside
    public static boolean connected = false;
    private final ClientScheduler scheduler = new ClientScheduler();

    private static List<RenderInfoBlock> blockRegister = new ArrayList<>();
    private static List<RenderInfoItem> itemRegister = new ArrayList<>();

    @Override
    public void setupConfiguration() {
        super.setupConfiguration();

        Config.addDynamicEntry(new PersistentDataManager.ConfigPersistency());
    }

    @Override
    public void preInit() {
        MinecraftForge.EVENT_BUS.register(this);
        // 1.7.10: No IReloadableResourceManager.registerReloadListener
        // Initialize AssetLibrary directly
        AssetLibrary.resReloadInstance.onResourceManagerReload(null);

        // 1.7.10: No ModelLoaderRegistry, use DummyModelLoader directly if needed
        // OBJLoader not available in 1.7.10, using AdvancedModelLoader instead

        super.preInit();

        RandomWordGenerator.init();
        CraftingAccessManager.ignoreNEI = false;
        // Initialize NEI integration for 1.7.10
        hellfirepvp.astralsorcery.common.integrations.ModIntegrationNEI.NEICompat.init();
    }

    public void registerModels() {
        registerFluidRenderers();
        registerEntityRenderers();
        registerDisplayInformationInit();
        registerTileRenderers();
        registerItemRenderers();
    }

    private void registerPendingIBlockColorBlocks() {
        // 1.7.10: Block colors are handled through IItemRenderer implementations
        // Migration BlockColors doesn't actually register anything in 1.7.10
        BlockColors colors = new BlockColors();
        for (BlockDynamicColor b : RegistryBlocks.pendingIBlockColorBlocks) {
            colors.registerBlockColorHandler(b, (Block) b);
        }
    }

    private void registerPendingIItemColorItems() {
        // 1.7.10: Item colors are handled through IItemRenderer implementations
        ItemColors colors = new ItemColors();
        for (ItemDynamicColor i : RegistryItems.pendingDynamicColorItems) {
            colors.registerItemColorHandler(new IItemColor() {

                @Override
                public int colorMultiplier(ItemStack stack, int tintIndex) {
                    return i.getColorForItemStack(stack, tintIndex);
                }
            }, (Item) i);
        }
    }

    private void registerFluidRenderers() {
        // 1.7.10: Fluid rendering uses a different system (ModelLoader doesn't exist)
        // TODO: Implement 1.7.10 compatible fluid rendering if needed
        // registerFluidRender(BlocksAS.fluidLiquidStarlight);
    }

    // 1.7.10: Commented out - uses 1.12+ ModelLoader API
    /*
     * private void registerFluidRender(Fluid f) {
     * RegistryBlocks.FluidCustomModelMapper mapper = new RegistryBlocks.FluidCustomModelMapper(f);
     * Block block = f.getBlock();
     * if (block != null) {
     * Item item = Item.getItemFromBlock(block);
     * if (item != null) {
     * ModelLoader.registerItemVariants(item);
     * ModelLoader.setCustomMeshDefinition(item, mapper);
     * } else {
     * ModelLoader.setCustomStateMapper(block, mapper);
     * }
     * }
     * }
     */

    @Override
    public void init() {
        super.init();

        registerModels();

        PersistentDataManager.INSTANCE.init(FileStorageUtil.getGeneralSubDirectory("astralsorcery_persistent"));

        GuiJournalPerkTree.initializeDrawBuffer();

        registerPendingIBlockColorBlocks();
        registerPendingIItemColorItems();

        MinecraftForge.EVENT_BUS.register(new ClientRenderEventHandler());
        MinecraftForge.EVENT_BUS.register(new ClientConnectionEventHandler());
        MinecraftForge.EVENT_BUS.register(EffectHandler.getInstance());
        MinecraftForge.EVENT_BUS.register(new ClientGatewayHandler());

        GuiScreenJournal
            .addBookmark(new BookmarkProvider("gui.journal.bm.research.name", 10, new Provider<GuiScreen>() {

                @Override
                public GuiScreen provide() {
                    return GuiJournalProgression.getJournalInstance();
                }
            }, new Provider<Boolean>() {

                @Override
                public Boolean provide() {
                    return true;
                }
            }));
        GuiScreenJournal
            .addBookmark(new BookmarkProvider("gui.journal.bm.constellations.name", 20, new Provider<GuiScreen>() {

                @Override
                public GuiScreen provide() {
                    return GuiJournalConstellationCluster.getConstellationScreen();
                }
            }, new Provider<Boolean>() {

                @Override
                public Boolean provide() {
                    return !ResearchManager.clientProgress.getSeenConstellations()
                        .isEmpty();
                }
            }));
        GuiScreenJournal.addBookmark(new BookmarkProvider("gui.journal.bm.perks.name", 30, new Provider<GuiScreen>() {

            @Override
            public GuiScreen provide() {
                return new GuiJournalPerkTree();
            }
        }, new Provider<Boolean>() {

            @Override
            public Boolean provide() {
                return ResearchManager.clientProgress.getAttunedConstellation() != null;
            }
        }));
        GuiScreenJournal
            .addBookmark(new BookmarkProvider("gui.journal.bm.knowledge.name", 40, new Provider<GuiScreen>() {

                @Override
                public GuiScreen provide() {
                    return new GuiJournalKnowledgeIndex();
                }
            }, new Provider<Boolean>() {

                @Override
                public Boolean provide() {
                    return !((KnowledgeFragmentData) PersistentDataManager.INSTANCE
                        .getData(PersistentDataManager.PersistentKey.KNOWLEDGE_FRAGMENTS)).getAllFragments()
                            .isEmpty();
                }
            }));
    }

    @Override
    public void postInit() {
        super.postInit();

        // 1.7.10: TileEntityItemStackRenderer doesn't have instance field
        // Using custom AstralTEISR for item rendering - initialized in ItemRenderRegistry
        // TileEntityItemStackRenderer dummyTEISR = new TileEntityItemStackRenderer();
        // new AstralTEISR(dummyTEISR);

        // TexturePreloader.doPreloadRoutine();

        ClientJournalMapping.init();
        OBJModelLibrary.init();

        // 1.7.10: No IReloadableResourceManager.registerReloadListener
        // Resource reloading handled differently in 1.7.10

        // Clears tooltip on langfile change or texture changes
        // 1.7.10: No resource reload listener registration

        JournalRecipeDisplayRecovery.attemptRecipeRecovery();
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id < 0 || id >= EnumGuiId.values().length) return null; // Out of range.
        EnumGuiId guiType = EnumGuiId.values()[id];
        return ClientGuiHandler.openGui(guiType, player, world, x, y, z);
    }

    public void registerItemRenderers() {
        // RenderTransformsHelper.init();

        // 1.7.10: ItemRendererFilteredTESR implements Forge's IItemRenderer
        // Use MinecraftForgeClient.registerItemRenderer for 1.7.10
        ItemRendererFilteredTESR blockMachineRender = new ItemRendererFilteredTESR();
        blockMachineRender
            .addRender(BlockMachine.MachineType.TELESCOPE.getMeta(), new TESRTelescope(), new TileTelescope());
        blockMachineRender
            .addRender(BlockMachine.MachineType.GRINDSTONE.getMeta(), new TESRGrindstone(), new TileGrindstone());
        // 1.7.10: Register through Forge's IItemRenderer system
        net.minecraftforge.client.MinecraftForgeClient
            .registerItemRenderer(Item.getItemFromBlock(BlocksAS.blockMachine), blockMachineRender);

        // ItemRenderRegistry.registerCameraTransforms(Item.getItemFromBlock(BlocksAS.blockMachine),
        // RenderTransformsHelper.BLOCK_TRANSFORMS);

        // 1.7.10: TESRCollectorCrystal needs to implement Forge's IItemRenderer for item rendering
        // For now, skip these registrations
        // ItemRenderRegistry.register(Item.getItemFromBlock(BlocksAS.collectorCrystal), new TESRCollectorCrystal());
        // ItemRenderRegistry
        // .register(Item.getItemFromBlock(BlocksAS.celestialCollectorCrystal), new TESRCollectorCrystal());

        if (Mods.GEOLOSYS.isPresent() && Mods.ORESTAGES.isPresent()) {
            ModIntegrationGeolosys.registerGeolosysSampleItemRenderer();
        }

        // ItemRenderRegistry.register(ItemsAS.something, new ? implements IItemRenderer());
    }

    @Override
    protected void registerTickHandlers(TickManager manager) {
        super.registerTickHandlers(manager);
        manager.register(new ClientLightbeamHandler());
        manager.register(scheduler);
        manager.register(ClientCameraManager.getInstance());
        manager.register(PatreonFlareManagerClient.INSTANCE);
    }

    @Override
    public void scheduleClientside(Runnable r, int tickDelay) {
        scheduler.addRunnable(r, tickDelay);
    }

    private void registerTileRenderers() {
        registerTESR(TileAltar.class, new TESRAltar());
        registerTESR(TileRitualPedestal.class, new TESRRitualPedestal());
        registerTESR(TileCollectorCrystal.class, new TESRCollectorCrystal());
        registerTESR(TileWell.class, new TESRWell());
        registerTESR(TileGrindstone.class, new TESRGrindstone());
        registerTESR(TileTelescope.class, new TESRTelescope());
        registerTESR(TileFakeTree.class, new TESRFakeTree());
        registerTESR(TileAttunementAltar.class, new TESRAttunementAltar());
        registerTESR(TileCrystalLens.class, new TESRLens());
        registerTESR(TileCrystalPrismLens.class, new TESRPrismLens());
        registerTESR(TileStarlightInfuser.class, new TESRStarlightInfuser());
        registerTESR(TileTranslucent.class, new TESRTranslucentBlock());
        registerTESR(TileAttunementRelay.class, new TESRAttunementRelay());
        registerTESR(TileMapDrawingTable.class, new TESRMapDrawingTable());
        registerTESR(TileChalice.class, new TESRChalice());
        registerTESR(TileObservatory.class, new TESRObservatory());
        if (Mods.GEOLOSYS.isPresent() && Mods.ORESTAGES.isPresent()) {
            ModIntegrationGeolosys.registerGeolosysSampleRender();
        }
    }

    private void registerTESR(Class tile, TileEntitySpecialRenderer renderer) {
        ClientRegistry.bindTileEntitySpecialRenderer(tile, renderer);
    }

    public void registerEntityRenderers() {
        // RenderingRegistry.registerEntityRenderingHandler(EntityTelescope.class, new RenderEntityTelescope.Factory());
        // RenderingRegistry.registerEntityRenderingHandler(EntityGrindstone.class, new
        // RenderEntityGrindstone.Factory());
        // 1.7.10: No Factory pattern, pass Render instances directly
        RenderingRegistry.registerEntityRenderingHandler(EntityItemHighlighted.class, new RenderEntityItemHighlight());
        RenderingRegistry.registerEntityRenderingHandler(EntityFlare.class, new RenderEntityFlare());
        RenderingRegistry.registerEntityRenderingHandler(EntityStarburst.class, new RenderEntityStarburst());
        RenderingRegistry.registerEntityRenderingHandler(EntityNocturnalSpark.class, new RenderEntityNoOp());
        RenderingRegistry.registerEntityRenderingHandler(EntityIlluminationSpark.class, new RenderEntityNoOp());
        RenderingRegistry.registerEntityRenderingHandler(EntityGrapplingHook.class, new RenderEntityHook());
        RenderingRegistry.registerEntityRenderingHandler(EntitySpectralTool.class, new RenderSpectralTool());
        RenderingRegistry.registerEntityRenderingHandler(EntityLiquidSpark.class, new RenderLiquidSpark());
        // RenderingRegistry.registerEntityRenderingHandler(SpellProjectile.class, new
        // RenderEntitySpellProjectile.Factory());
        RenderingRegistry.registerEntityRenderingHandler(EntityShootingStar.class, new RenderEntityShootingStar());
        RenderingRegistry
            .registerEntityRenderingHandler(EntityItemExplosionResistant.class, new RenderEntityItemHighlight());
    }

    public void registerDisplayInformationInit() {
        for (RenderInfoItem modelEntry : itemRegister) {
            if (modelEntry.variant) {
                registerVariantName(modelEntry.item, modelEntry.name);
            }
            // 1.7.10: Simplified model registration without ModelLoader
            if (modelEntry.item instanceof IOBJItem) {
                // OBJ items handled differently in 1.7.10 - using IItemRenderer
                // Handled by ItemRenderRegistry
            } else {
                // Standard item rendering in 1.7.10
            }
        }

        for (RenderInfoBlock modelEntry : blockRegister) {
            if (modelEntry.block instanceof BlockDynamicStateMapper) {
                if (((BlockDynamicStateMapper) modelEntry.block).handleRegisterStateMapper()) {
                    ((BlockDynamicStateMapper) modelEntry.block).registerStateMapper();
                }
            }
            // 1.7.10: Block item rendering handled differently
        }
    }

    @Override
    public void fireLightning(World world, Vector3 from, Vector3 to, Color overlay) {
        EffectLightning lightning = EffectHandler.getInstance()
            .lightning(from, to);
        if (overlay != null) {
            lightning.setOverlayColor(overlay);
        }
    }

    @Override
    public void registerFromSubItems(Item item, String name) {
        if (item instanceof IMetaItem) {
            int[] additionalMetas = ((IMetaItem) item).getSubItems();
            if (additionalMetas != null) {
                for (int meta : additionalMetas) {
                    registerItemRender(item, meta, name);
                }
            }
            return;
        }
        ArrayList<ItemStack> list = new ArrayList<>();
        // 1.7.10: getSubItems takes 3 parameters: Item, CreativeTabs, List
        item.getSubItems(item, item.getCreativeTab(), list);
        if (!list == null || list.stackSize <= 0) {
            for (ItemStack i : list) {
                registerItemRender(item, i.getItemDamage(), name);
            }
        } else {
            registerItemRender(item, 0, name);
        }
    }

    public void registerVariantName(Item item, String name) {
        // 1.7.10: No ModelBakery.registerItemVariants
        // Variants handled through item rendering system
    }

    public void registerBlockRender(Block block, int metadata, String name) {
        blockRegister.add(new RenderInfoBlock(block, metadata, name));
    }

    public void registerItemRender(Item item, int metadata, String name) {
        registerItemRender(item, metadata, name, false);
    }

    public void registerItemRender(Item item, int metadata, String name, boolean variant) {
        itemRegister.add(new RenderInfoItem(item, metadata, name, variant));
    }

    private static class RenderInfoBlock {

        public Block block;
        public int metadata;
        public String name;

        public RenderInfoBlock(Block block, int metadata, String name) {
            this.block = block;
            this.metadata = metadata;
            this.name = name;
        }
    }

    private static class RenderInfoItem {

        public Item item;
        public int metadata;
        public String name;
        public boolean variant;

        public RenderInfoItem(Item item, int metadata, String name, boolean variant) {
            this.item = item;
            this.metadata = metadata;
            this.name = name;
            this.variant = variant;
        }
    }

}
