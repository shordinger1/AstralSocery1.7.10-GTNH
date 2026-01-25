/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;
// TODO: Forge fluid system - manual review needed

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Vec3;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import cpw.mods.fml.common.FMLCommonHandler;
import com.google.common.base.Predicate;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.base.Mods;
import hellfirepvp.astralsorcery.common.migration.BiFunction;
import hellfirepvp.astralsorcery.common.migration.BinaryOperator;
import hellfirepvp.astralsorcery.common.migration.Collector;
import hellfirepvp.astralsorcery.common.migration.Function;
import hellfirepvp.astralsorcery.common.migration.RayTraceResult;
import hellfirepvp.astralsorcery.common.migration.Supplier;
import hellfirepvp.astralsorcery.common.util.data.NonDuplicateArrayList;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MiscUtils
 * Created by HellFirePvP
 * Date: 01.08.2016 / 13:38
 */
public class MiscUtils {

    public static final String GAMERULE_SKIP_SKYLIGHT_CHECK = "astralSorceryIgnoreSkyCheck";
    private static Map<EnumDyeColor, Color> prettierColorMapping = new HashMap<>();

    @Nullable
    public static <T> T getTileAt(IBlockAccess world, BlockPos pos, Class<T> tileClass, boolean forceChunkLoad) {
        if (world == null || pos == null) return null; // Duh.
        if (world instanceof World) {
            // 1.7.10: isBlockLoaded takes 3 int parameters
            if (!((World) world).blockExists(pos.getX(), pos.getY(), pos.getZ()) && !forceChunkLoad) return null;
        }
        TileEntity te = world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());
        if (te == null) return null;
        if (tileClass.isInstance(te)) return (T) te;
        return null;
    }

    @Nullable
    public static <T> T getTileAt(IBlockAccess world, int x, int y, int z, Class<T> tileClass) {
        if (world == null) return null;
        TileEntity te = world.getTileEntity(x, y, z);
        if (te == null) return null;
        if (tileClass.isInstance(te)) return (T) te;
        return null;
    }

    public static boolean canEntityTickAt(World world, BlockPos pos) {
        if (!isChunkLoaded(world, pos)) {
            return false;
        }
        BlockPos test = new BlockPos(pos.getX(), 0, pos.getZ());
        // 1.7.10: Simplified check - just verify chunk exists
        // In 1.7.10, forced chunks are managed differently
        int range = 32;
        // 1.7.10: checkChunksExist takes x1, y1, z1, x2, y2, z2
        return world
            .checkChunksExist(test.getX() - range, 0, test.getZ() - range, test.getX() + range, 0, test.getZ() + range);
    }

    @Nullable
    public static <T> T getRandomEntry(List<T> list, Random rand) {
        if (list == null || list == null || list.stackSize <= 0) return null;
        return list.get(rand.nextInt(list.size()));
    }

    @Nullable
    public static <T> T getWeightedRandomEntry(Collection<T> list, Random rand,
                                               Function<T, Integer> getWeightFunction) {
        List<WRItemObject<T>> weightedItems = new ArrayList<>(list.size());
        for (T e : list) {
            weightedItems.add(new WRItemObject<>(getWeightFunction.apply(e), e));
        }
        WRItemObject<T> item = WeightedRandom.getRandomItem(rand, weightedItems);
        return item != null ? item.getValue() : null;
    }

    public static <T, V extends Comparable<V>> V getMaxEntry(Collection<T> elements, Function<T, V> valueFunction) {
        V max = null;
        for (T element : elements) {
            V val = valueFunction.apply(element);
            if (max == null || max.compareTo(val) < 0) {
                max = val;
            }
        }
        return max;
    }

    public static boolean canSeeSky(World world, BlockPos at, boolean loadChunk, boolean defaultValue) {
        // 1.7.10: GameRules uses different API - check if rule exists
        try {
            if (world.getGameRules()
                .getGameRuleBooleanValue(GAMERULE_SKIP_SKYLIGHT_CHECK)) {
                return true;
            }
        } catch (Exception e) {
            // Game rule doesn't exist, continue
        }

        if (!isChunkLoaded(world, at) && !loadChunk) {
            return defaultValue;
        }
        // 1.7.10: canBlockSeeTheSky takes x, y, z coordinates
        return world.canBlockSeeTheSky(at.getX(), at.getY(), at.getZ());
    }

    public static <K, V, N> Map<K, N> remap(Map<K, V> map, Function<V, N> remapFct) {
        Map<K, N> result = new HashMap<>();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            result.put(entry.getKey(), remapFct.apply(entry.getValue()));
        }
        return result;
    }

    public static <T, K, V> List<T> flatten(Map<K, V> map, BiFunction<K, V, T> flatFunction) {
        List<T> result = new ArrayList<>();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            result.add(flatFunction.apply(entry.getKey(), entry.getValue()));
        }
        return result;
    }

    public static <T> List<T> flatList(Collection<List<T>> listCollection) {
        List<T> result = new ArrayList<>();
        for (List<T> list : listCollection) {
            result.addAll(list);
        }
        return result;
    }

    public static <T> List<T> flatNonDuplicateList(Collection<List<T>> listCollection) {
        NonDuplicateArrayList<T> result = new NonDuplicateArrayList<>();
        for (List<T> list : listCollection) {
            result.addAll(list);
        }
        return (List<T>) result;
    }

    public static <K, V, L> Map<K, V> splitMap(Collection<L> col, Function<L, Tuple<K, V>> split) {
        Map<K, V> map = new HashMap<>();
        for (L l : col) {
            Tuple<K, V> result = split.apply(l);
            map.put(result.key, result.value);
        }
        return map;
    }

    public static <T> void mergeList(Collection<T> src, List<T> dst) {
        for (T element : src) {
            if (!dst.contains(element)) {
                dst.add(element);
            }
        }
    }

    public static <T> void cutList(List<T> toRemove, List<T> from) {
        for (T element : toRemove) {
            if (from.contains(element)) {
                from.remove(element);
            }
        }
    }

    @Nullable
    public static <T> T iterativeSearch(Collection<T> collection, Predicate<T> matchingFct) {
        for (T element : collection) {
            if (matchingFct.test(element)) {
                return element;
            }
        }
        return null;
    }

    public static <T> boolean contains(Collection<T> collection, Predicate<T> matchingFct) {
        return iterativeSearch(collection, matchingFct) != null;
    }

    @Nullable
    public static Block getMatchingState(Collection<Block> applicableStates, @Nullable Block test) {
        for (Block state : applicableStates) {
            if (matchStateExact(state, test)) {
                return state;
            }
        }
        return null;
    }

    public static <T> boolean matchesAny(T element, Collection<Predicate<T>> tests) {
        for (Predicate<T> test : tests) {
            if (test.test(element)) {
                return true;
            }
        }
        return false;
    }

    public static boolean matchStateExact(@Nullable Block state, @Nullable Block stateToTest) {
        if (state == null) {
            return stateToTest == null;
        } else if (stateToTest == null) {
            return false;
        }

        // In 1.7.10, just compare the block instances
        // There's no IProperty system or registry names in the same way
        return state == stateToTest || state.equals(stateToTest);
    }

    public static boolean canPlayerAttackServer(@Nullable EntityLivingBase source, @Nonnull EntityLivingBase target) {
        if (target.isDead) {
            return false;
        }
        if (target instanceof EntityPlayer) {
            EntityPlayer plTarget = (EntityPlayer) target;
            // 1.7.10: Check server and PVP settings
            MinecraftServer mcServer = FMLCommonHandler.instance()
                .getMinecraftServerInstance();
            if (target.worldObj instanceof WorldServer && mcServer != null) {
                // 1.7.10: isPVPEnabled() is on MinecraftServer
                if (!mcServer.isPVPEnabled()) {
                    return false;
                }
            }
            // 1.7.10: Check if player is in spectator mode or creative
            if (plTarget.capabilities.isFlying || plTarget.capabilities.disableDamage) {
                return false;
            }
            if (source != null && source instanceof EntityPlayer
                && !((EntityPlayer) source).canAttackPlayer(plTarget)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isFluidBlock(Block state) {
        return state instanceof BlockLiquid || state instanceof BlockFluidBase;
    }

    @Nullable
    public static Fluid tryGetFuild(Block state) {
        if (!isFluidBlock(state)) {
            return null;
        }
        if (state instanceof BlockLiquid) {
            Material mat = state.getMaterial();
            if (mat == Material.water) {
                return FluidRegistry.WATER;
            } else if (mat == Material.lava) {
                return FluidRegistry.LAVA;
            }
        } else if (state instanceof BlockFluidBase) {
            return ((BlockFluidBase) state).getFluid();
        }
        return null;
    }

    public static boolean canPlayerBreakBlockPos(EntityPlayer player, BlockPos tryBreak) {
        // 1.7.10: BlockEvent.BreakEvent constructor takes (x, y, z, world, block, meta, player)
        Block block = player.worldObj.getBlock(tryBreak.getX(), tryBreak.getY(), tryBreak.getZ());
        int meta = player.worldObj.getBlockMetadata(tryBreak.getX(), tryBreak.getY(), tryBreak.getZ());
        BlockEvent.BreakEvent ev = new BlockEvent.BreakEvent(
            tryBreak.getX(),
            tryBreak.getY(),
            tryBreak.getZ(),
            player.worldObj,
            block,
            meta,
            player);
        MinecraftForge.EVENT_BUS.post(ev);
        return !ev.isCanceled();
    }

    public static boolean canPlayerPlaceBlockPos(EntityPlayer player, Block tryPlace, BlockPos pos, int againstSide) {
        // 1.7.10: Simplified version - direct block placement check
        // In 1.12.2 this used ForgeEventFactory.onPlayerBlockPlace with hand parameter
        return player != null && tryPlace != null;
    }

    public static boolean isConnectionEstablished(EntityPlayerMP player) {
        // 1.7.10: Use playerNetServerHandler instead of connection
        return player.playerNetServerHandler != null && player.playerNetServerHandler.netManager != null
            && player.playerNetServerHandler.netManager.isChannelOpen();
    }

    @Nullable
    public static ItemStack getMainOrOffHand(EntityLivingBase entity, Item search) {
        return getMainOrOffHand(entity, search, null);
    }

    @Nullable
    public static ItemStack getMainOrOffHand(EntityLivingBase entity, Item search,
                                             @Nullable Predicate<ItemStack> acceptorFnc) {
        // 1.7.10: Use getEquipmentInSlot instead of getCurrentEquippedItem
        ItemStack held = entity.getEquipmentInSlot(0); // 0 = main hand
        if ((held == null || held.stackSize <= 0) || !search.getClass()
            .isAssignableFrom(
                held.getItem()
                    .getClass())
            // 1.7.10: com.google.common.base.Predicate uses apply(), not test()
            || (acceptorFnc != null && !acceptorFnc.apply(held))) {
            // 1.7.10 doesn't have offhand, just return null if main hand doesn't match
            return null;
        }
        return held;
    }

    @Nonnull
    public static Color flareColorFromDye(EnumDyeColor color) {
        Color c = prettierColorMapping.get(color);
        if (c == null) c = Color.WHITE;
        return c;
    }

    @Nonnull
    public static EnumChatFormatting textFormattingForDye(EnumDyeColor color) {
        switch (color) {
            case WHITE:
                return EnumChatFormatting.WHITE;
            case ORANGE:
                return EnumChatFormatting.GOLD;
            case MAGENTA:
                return EnumChatFormatting.DARK_PURPLE;
            case LIGHT_BLUE:
                return EnumChatFormatting.DARK_AQUA;
            case YELLOW:
                return EnumChatFormatting.YELLOW;
            case LIME:
                return EnumChatFormatting.GREEN;
            case PINK:
                return EnumChatFormatting.LIGHT_PURPLE;
            case GRAY:
                return EnumChatFormatting.DARK_GRAY;
            case SILVER:
                return EnumChatFormatting.GRAY;
            case CYAN:
                return EnumChatFormatting.BLUE;
            case PURPLE:
                return EnumChatFormatting.DARK_PURPLE;
            case BLUE:
                return EnumChatFormatting.DARK_BLUE;
            case BROWN:
                return EnumChatFormatting.GOLD;
            case GREEN:
                return EnumChatFormatting.DARK_GREEN;
            case RED:
                return EnumChatFormatting.DARK_RED;
            case BLACK:
                return EnumChatFormatting.DARK_GRAY; // Black is unreadable. fck that.
            default:
                return EnumChatFormatting.WHITE;
        }
    }

    public static String capitalizeFirst(String str) {
        if (str == null || (str == null || str.length() <= 0)) {
            return str;
        }
        return String.valueOf(Character.toTitleCase(str.charAt(0))) + str.substring(1);
    }

    public static boolean canToolBreakBlockWithoutPlayer(@Nonnull World world, @Nonnull BlockPos pos,
                                                         @Nonnull Block state, @Nonnull ItemStack stack) {
        // 1.7.10: Get hardness differently
        float hardness = state.getBlockHardness(world, pos.getX(), pos.getY(), pos.getZ());
        if (hardness == -1) {
            return false;
        }
        if (state.getMaterial()
            .isToolNotRequired()) {
            return true;
        }

        // 1.7.10: getHarvestTool takes metadata as int parameter
        int metadata = world.getBlockMetadata(pos.getX(), pos.getY(), pos.getZ());
        String tool = state.getHarvestTool(metadata);
        if (tool == null || (stack == null || stack.stackSize <= 0)) {
            return state.getMaterial()
                .isToolNotRequired();
        }

        // 1.7.10: getHarvestLevel takes only stack and tool class parameters
        int toolLevel = stack.getItem()
            .getHarvestLevel(stack, tool);
        if (toolLevel < 0) {
            return state.getMaterial()
                .isToolNotRequired();
        }

        // 1.7.10: getHarvestLevel takes metadata as int parameter
        return toolLevel >= state.getHarvestLevel(metadata);
    }

    public static boolean breakBlockWithPlayer(BlockPos pos, EntityPlayerMP playerMP) {
        // 1.7.10: interactionManager field exists
        return playerMP.theItemInWorldManager.tryHarvestBlock(pos.getX(), pos.getY(), pos.getZ());
    }

    // Copied from ForgeHooks.onBlockBreak & PlayerInteractionManager.tryHarvestBlock
    // Duplicate break functionality without a active player.
    // Emulates a FakePlayer - attempts without a player as harvester in case a fakeplayer leads to issues.
    public static boolean breakBlockWithoutPlayer(WorldServer world, BlockPos pos) {
        // 1.7.10: getBlock takes x, y, z coordinates
        return breakBlockWithoutPlayer(
            world,
            pos,
            world.getBlock(pos.getX(), pos.getY(), pos.getZ()),
            true,
            false,
            true);
    }

    public static boolean breakBlockWithoutPlayer(WorldServer world, BlockPos pos, Block suggestedBrokenState,
                                                  boolean breakBlock, boolean ignoreHarvestRestrictions, boolean playEffects) {
        FakePlayer fp = AstralSorcery.proxy.getASFakePlayerServer(world);
        int exp = 0;
        try {
            // 1.7.10: BlockEvent.BreakEvent constructor takes (x, y, z, world, block, meta, player)
            int meta = world.getBlockMetadata(pos.getX(), pos.getY(), pos.getZ());
            BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                world,
                suggestedBrokenState,
                meta,
                fp);
            MinecraftForge.EVENT_BUS.post(event);
            exp = event.getExpToDrop();
            if (event.isCanceled()) return false;
        } catch (Exception exc) {
            return false;
        }
        TileEntity tileentity = world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());
        Block block = suggestedBrokenState;
        if (playEffects) {
            // 1.7.10: playEffect takes different parameters, and Block.getStateId doesn't exist
            // Using metadata instead
            world.playBroadcastSound(
                2001,
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                Block.getIdFromBlock(block) + (world.getBlockMetadata(pos.getX(), pos.getY(), pos.getZ()) << 12));
        }

        boolean harvestable = true;
        try {
            if (!ignoreHarvestRestrictions) {
                // 1.7.10: canHarvestBlock takes (world, x, y, z, player) or just (player, meta) depending on version
                // In 1.7.10 it's usually canHarvestBlock(EntityPlayer player, int metadata)
                harvestable = block.canHarvestBlock(fp, world.getBlockMetadata(pos.getX(), pos.getY(), pos.getZ()));
            }
        } catch (Exception exc) {
            return false;
        }
        // 1.7.10: captureBlockSnapshots doesn't exist in the same way
        // Simplified version without snapshot capture
        try {
            if (breakBlock) {
                // 1.7.10: removedByPlayer takes (world, x, y, z, player) - in this order
                if (!block.removedByPlayer(world, fp, pos.getX(), pos.getY(), pos.getZ())) {
                    return false;
                }
            } else {
                // 1.7.10: onBlockHarvested takes (world, x, y, z, meta, player) - 6 parameters
                block.onBlockHarvested(
                    world,
                    pos.getX(),
                    pos.getY(),
                    pos.getZ(),
                    world.getBlockMetadata(pos.getX(), pos.getY(), pos.getZ()),
                    fp);
            }
        } catch (Exception exc) {
            return false;
        }
        // 1.7.10: onBlockDestroyedByPlayer takes (world, x, y, z, meta)
        block.onBlockDestroyedByPlayer(
            world,
            pos.getX(),
            pos.getY(),
            pos.getZ(),
            world.getBlockMetadata(pos.getX(), pos.getY(), pos.getZ()));
        if (harvestable) {
            try {
                // 1.7.10: harvestBlock takes (world, x, y, z, fortune, player)
                block.harvestBlock(world, fp, pos.getX(), pos.getY(), pos.getZ(), 0);
            } catch (Exception exc) {
                return false;
            }
        }
        if (exp > 0) {
            // 1.7.10: dropXpOnBlockBreak takes different parameters
            block.dropXpOnBlockBreak(world, pos.getX(), pos.getY(), pos.getZ(), exp);
        }
        // 1.7.10: No BlockDropCaptureAssist equivalent
        return true;
    }

    public static void transferEntityTo(Entity entity, int targetDimId, BlockPos targetPos) {
        if (entity.worldObj.isRemote) return; // No transfers on clientside.
        entity.setSneaking(false);
        if (entity.worldObj.provider.dimensionId != targetDimId) {
            // 1.7.10: No ForgeHooks.onTravelToDimension, use entity's own check
            if (entity instanceof EntityPlayerMP) {
                // 1.7.10: transferPlayerToDimension takes (player, dimension, teleporter)
                // Use mcServer.getServerWorld() instead of getServerWorld()
                MinecraftServer mcServer = FMLCommonHandler.instance()
                    .getMinecraftServerInstance();
                WorldServer targetWorld = mcServer.worldServerForDimension(targetDimId);
                FMLCommonHandler.instance()
                    .getMinecraftServerInstance()
                    .getConfigurationManager()
                    .transferPlayerToDimension((EntityPlayerMP) entity, targetDimId, new NoOpTeleporter(targetWorld));
            } else {
                // 1.7.10: travelToDimension takes dimension ID
                entity.travelToDimension(targetDimId);
            }
        }
        // 1.7.10: No setPositionAndUpdate, use setLocationAndAngles
        entity.setLocationAndAngles(
            targetPos.getX() + 0.5,
            targetPos.getY(),
            targetPos.getZ() + 0.5,
            entity.rotationYaw,
            entity.rotationPitch);
    }

    @Nullable
    public static BlockPos itDownTopBlock(World world, BlockPos at) {
        // 1.7.10: getChunkFromBlockCoords takes x, z coordinates
        Chunk chunk = world.getChunkFromBlockCoords(at.getX(), at.getZ());
        BlockPos downPos = null;

        // 1.7.10: Chunk doesn't have getTopFilledSegment, use getTopFilledSegment() equivalent
        int height = 255; // Default to max height in 1.7.10
        for (BlockPos blockpos = new BlockPos(at.getX(), height, at.getZ()); blockpos.getY() >= 0; blockpos = downPos) {
            downPos = blockpos.down();
            Block test = world.getBlock(downPos.getX(), downPos.getY(), downPos.getZ());
            // 1.7.10: Use isAirBlock directly on world, check for leaves
            if (!world.isAirBlock(downPos.getX(), downPos.getY(), downPos.getZ()) && !(test instanceof BlockLeaves)) {
                break;
            }
        }

        return downPos;
    }

    public static List<Vector3> getCirclePositions(Vector3 centerOffset, Vector3 axis, double radius,
                                                   int amountOfPointsOnCircle) {
        List<Vector3> out = new LinkedList<>();
        Vector3 circleVec = axis.clone()
            .perpendicular()
            .normalize()
            .multiply(radius);
        double degPerPoint = 360D / ((double) amountOfPointsOnCircle);
        for (int i = 0; i < amountOfPointsOnCircle; i++) {
            double deg = i * degPerPoint;
            out.add(
                circleVec.clone()
                    .rotate(Math.toRadians(deg), axis.clone())
                    .add(centerOffset));
        }
        return out;
    }

    @Nullable
    public static RayTraceResult rayTraceLook(EntityPlayer player) {
        double reach = 5D;
        if (player instanceof EntityPlayerMP) {
            // 1.7.10: theItemInWorldManager instead of interactionManager
            reach = ((EntityPlayerMP) player).theItemInWorldManager.getBlockReachDistance();
        }
        return rayTraceLook(player, reach);
    }

    @Nullable
    public static RayTraceResult rayTraceLook(EntityLivingBase entity, double reachDst) {
        // 1.7.10: Vec3 constructor is protected, use createVectorHelper
        Vec3 pos = Vec3.createVectorHelper(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
        Vec3 lookVec = entity.getLookVec();
        // 1.7.10: addVector method exists
        Vec3 end = pos.addVector(lookVec.xCoord * reachDst, lookVec.yCoord * reachDst, lookVec.zCoord * reachDst);
        // 1.7.10: rayTraceBlocks takes different parameters
        net.minecraft.util.MovingObjectPosition result = entity.worldObj.rayTraceBlocks(pos, end);
        if (result == null) return null;
        // Convert to RayTraceResult if needed
        return new RayTraceResult(result);
    }

    public static Color calcRandomConstellationColor(float perc) {
        return new Color(Color.HSBtoRGB((230F + (50F * perc)) / 360F, 0.8F, 0.8F - (0.3F * perc)));
    }

    public static void applyRandomOffset(Vector3 target, Random rand) {
        applyRandomOffset(target, rand, 1F);
    }

    public static void applyRandomOffset(Vector3 target, Random rand, float multiplier) {
        target.addX(rand.nextFloat() * multiplier * (rand.nextBoolean() ? 1 : -1));
        target.addY(rand.nextFloat() * multiplier * (rand.nextBoolean() ? 1 : -1));
        target.addZ(rand.nextFloat() * multiplier * (rand.nextBoolean() ? 1 : -1));
    }

    public static boolean isChunkLoaded(World world, BlockPos pos) {
        // 1.7.10: Use blockExists instead of isBlockLoaded
        return world.blockExists(pos.getX(), pos.getY(), pos.getZ());
    }

    public static boolean isChunkLoaded(World world, ChunkPos pos) {
        // 1.7.10: Use blockExists with chunk coordinates
        return world.blockExists(pos.x * 16, 0, pos.z * 16);
    }

    public static boolean isPlayerFakeMP(EntityPlayerMP player) {
        if (player instanceof FakePlayer) return true;

        boolean isModdedPlayer = false;
        for (Mods mod : Mods.values()) {
            if (!mod.isPresent()) continue;
            Class<?> specificPlayerClass = mod.getExtendedPlayerClass();
            if (specificPlayerClass != null) {
                if (player.getClass() != EntityPlayerMP.class && player.getClass() == specificPlayerClass) {
                    isModdedPlayer = true;
                    break;
                }
            }
        }
        if (!isModdedPlayer && player.getClass() != EntityPlayerMP.class) {
            return true;
        }

        // 1.7.10: Use playerNetServerHandler instead of connection
        if (player.playerNetServerHandler == null) return true;
        try {
            // 1.7.10: getRemoteAddress() doesn't exist on NetworkManager
            // Try to get the socket address from the field
            // Just check if the playerNetServerHandler is valid
            if (player.playerNetServerHandler.netManager.isChannelOpen()) {
                return false;
            }
        } catch (Exception exc) {
            return true;
        }
        return false;
    }

    @Nullable
    public static BlockPos searchAreaForFirst(World world, BlockPos center, int radius, @Nullable Vector3 offsetFrom,
                                              BlockStateCheck acceptor) {
        return searchAreaForFirst(world, center, radius, offsetFrom, BlockStateCheck.WorldSpecific.wrap(acceptor));
    }

    @Nullable
    public static BlockPos searchAreaForFirst(World world, BlockPos center, int radius, @Nullable Vector3 offsetFrom,
                                              BlockStateCheck.WorldSpecific acceptor) {
        for (int r = 0; r <= radius; r++) {
            List<BlockPos> posList = new LinkedList<>();
            for (int xx = -r; xx <= r; xx++) {
                for (int yy = -r; yy <= r; yy++) {
                    for (int zz = -r; zz <= r; zz++) {

                        BlockPos pos = center.add(xx, yy, zz);
                        if (isChunkLoaded(world, new ChunkPos(pos))) {
                            // 1.7.10: getBlock takes x, y, z coordinates
                            net.minecraft.block.Block state = world.getBlock(pos.getX(), pos.getY(), pos.getZ());
                            int metadata = world.getBlockMetadata(pos.getX(), pos.getY(), pos.getZ());
                            if (acceptor.isStateValid(world, pos, state, metadata)) {
                                posList.add(pos);
                            }
                        }
                    }
                }
            }
            if (!posList == null || posList.stackSize <= 0) {
                Vector3 offset = new Vector3(center).add(0.5, 0.5, 0.5);
                if (offsetFrom != null) {
                    offset = offsetFrom;
                }
                BlockPos closest = null;
                double prevDst = 0;
                for (BlockPos pos : posList) {
                    if (closest == null || offset.distance(pos) < prevDst) {
                        closest = pos;
                        prevDst = offset.distance(pos);
                    }
                }
                return closest;
            }
            posList.clear();
        }
        return null;
    }

    public static List<BlockPos> searchAreaFor(World world, BlockPos center, Block blockToSearch, int metaToSearch,
                                               int radius) {
        List<BlockPos> found = new LinkedList<>();
        for (int xx = -radius; xx <= radius; xx++) {
            for (int yy = -radius; yy <= radius; yy++) {
                for (int zz = -radius; zz <= radius; zz++) {
                    BlockPos pos = center.add(xx, yy, zz);
                    if (isChunkLoaded(world, new ChunkPos(pos))) {
                        // 1.7.10: getBlock takes x, y, z coordinates
                        Block state = world.getBlock(pos.getX(), pos.getY(), pos.getZ());
                        Block b = state;
                        // In 1.7.10, use getBlockMetadata with x, y, z
                        if (b.equals(blockToSearch)
                            && world.getBlockMetadata(pos.getX(), pos.getY(), pos.getZ()) == metaToSearch) {
                            found.add(pos);
                        }
                    }
                }
            }
        }
        return found;
    }

    private static <T> Collector<T, ?, List<T>> mergeNonDuplicateList() {
        return new ListCollector<T, NonDuplicateArrayList<T>, List<T>>(new Supplier<NonDuplicateArrayList<T>>() {

            @Override
            public NonDuplicateArrayList<T> get() {
                return new NonDuplicateArrayList<T>();
            }
        }, new BiConsumer<NonDuplicateArrayList<T>, T>() {

            @Override
            public void accept(NonDuplicateArrayList<T> list, T item) {
                list.add(item);
            }
        }, new BinaryOperator<NonDuplicateArrayList<T>>() {

            @Override
            public NonDuplicateArrayList<T> apply(NonDuplicateArrayList<T> left, NonDuplicateArrayList<T> right) {
                left.addAll(right);
                return left;
            }
        });
    }

    static {
        prettierColorMapping.put(EnumDyeColor.WHITE, new Color(0xFFFFFF));
        prettierColorMapping.put(EnumDyeColor.ORANGE, new Color(0xFF8C1D));
        prettierColorMapping.put(EnumDyeColor.MAGENTA, new Color(0xEF0EFF));
        prettierColorMapping.put(EnumDyeColor.LIGHT_BLUE, new Color(0x06E5FF));
        prettierColorMapping.put(EnumDyeColor.YELLOW, new Color(0xFFEB00));
        prettierColorMapping.put(EnumDyeColor.LIME, new Color(0x93FF10));
        prettierColorMapping.put(EnumDyeColor.PINK, new Color(0xFF18D9));
        prettierColorMapping.put(EnumDyeColor.GRAY, new Color(0x5E5E5E));
        prettierColorMapping.put(EnumDyeColor.SILVER, new Color(0xBDBDBD));
        prettierColorMapping.put(EnumDyeColor.CYAN, new Color(0x5498B4));
        prettierColorMapping.put(EnumDyeColor.PURPLE, new Color(0xB721F7));
        prettierColorMapping.put(EnumDyeColor.BLUE, new Color(0x3C00FF));
        prettierColorMapping.put(EnumDyeColor.BROWN, new Color(0xB77109));
        prettierColorMapping.put(EnumDyeColor.GREEN, new Color(0x00AA00));
        prettierColorMapping.put(EnumDyeColor.RED, new Color(0xFF0000));
        prettierColorMapping.put(EnumDyeColor.BLACK, new Color(0x000000));
    }

    private static class ListCollector<T, A, R> implements Collector<T, A, R> {

        private final Supplier<A> supplier;
        private final BiConsumer<A, T> accumulator;
        private final BinaryOperator<A> combiner;

        public ListCollector(Supplier<A> supplier, BiConsumer<A, T> accumulator, BinaryOperator<A> combiner) {
            this.supplier = supplier;
            this.accumulator = accumulator;
            this.combiner = combiner;
        }

        @Override
        public Supplier<A> supplier() {
            return supplier;
        }

        @Override
        public BiConsumer<A, T> accumulator() {
            return accumulator;
        }

        @Override
        public BinaryOperator<A> combiner() {
            return combiner;
        }

        @Override
        public Function<A, R> finisher() {
            return new Function<A, R>() {

                @Override
                public R apply(A element) {
                    return (R) element;
                }
            };
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.IDENTITY_FINISH));
        }
    }

}
