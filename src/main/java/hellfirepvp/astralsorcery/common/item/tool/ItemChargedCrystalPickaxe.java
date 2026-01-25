/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.block.EffectTranslucentFallingBlock;
import hellfirepvp.astralsorcery.common.base.Mods;
import hellfirepvp.astralsorcery.common.integrations.ModIntegrationOreStages;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktOreScan;
import hellfirepvp.astralsorcery.common.structure.array.BlockArray;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.struct.OreDiscoverer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemChargedCrystalPickaxe
 * Created by HellFirePvP
 * Date: 12.03.2017 / 23:25
 */
public class ItemChargedCrystalPickaxe extends ItemCrystalPickaxe implements ChargedCrystalToolBase {

    private static int idx = 0;

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World worldIn, EntityPlayer playerIn) {
        if (!(stack == null || stack.stackSize <= 0) && scanForOres(worldIn, playerIn)) {
            return stack;
        }
        return stack;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        if (scanForOres(worldIn, playerIn)) {
            return true;
        }
        return false;
    }

    private boolean scanForOres(World world, EntityPlayer player) {
        // 1.7.10: No CooldownTracker, removed cooldown check; use world parameter
        if (!world.isRemote && player instanceof EntityPlayerMP && !MiscUtils.isPlayerFakeMP((EntityPlayerMP) player)) {
            final World scanWorld = world;
            final EntityPlayer scanPlayer = player;
            Thread tr = new Thread(new Runnable() {
                @Override
                public void run() {
                    BlockArray foundOres = OreDiscoverer.startSearch(scanWorld, Vector3.atEntityCorner(scanPlayer), 14);
                    if (!foundOres == null || foundOres.stackSize <= 0) {
                        List<BlockPos> positions = new LinkedList<>();
                        BlockPos plPos = new BlockPos(scanPlayer);
                        for (BlockPos pos : foundOres.getPattern()
                            .keySet()) {
                            // 1.7.10: BlockPos.distanceSq() doesn't exist, calculate manually
                            double dx = pos.getX() - plPos.getX();
                            double dy = pos.getY() - plPos.getY();
                            double dz = pos.getZ() - plPos.getZ();
                            double distSq = dx * dx + dy * dy + dz * dz;
                            if (distSq < 350) {
                                positions.add(pos);
                            }
                        }
                        PktOreScan scan = new PktOreScan(positions, true);
                        PacketChannel.CHANNEL.sendTo(scan, (EntityPlayerMP) scanPlayer);
                    }
                }
            });
            tr.setName("Ore Scan " + idx);
            idx++;
            tr.start();
            // 1.7.10: No cooldown to set
            return true;
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    public static void playClientEffects(Collection<BlockPos> positions, boolean tumble) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (player == null) return;
        List<Block> changed = new LinkedList<>();

        for (BlockPos at : positions) {
            Vector3 atPos = new Vector3(at).add(0.5, 0.5, 0.5);
            atPos.add(
                itemRand.nextFloat() - itemRand.nextFloat(),
                itemRand.nextFloat() - itemRand.nextFloat(),
                itemRand.nextFloat() - itemRand.nextFloat());
            // 1.7.10: getBlock() takes x, y, z coordinates, not BlockPos
            Block state = Minecraft.getMinecraft().theWorld.getBlock(at.getX(), at.getY(), at.getZ());
            if (Mods.ORESTAGES.isPresent()) {
                if (changed.contains(state) || !ModIntegrationOreStages.canSeeOreClient(state)) {
                    changed.add(state);
                    continue;
                }
            }

            EffectTranslucentFallingBlock bl = EffectHandler.getInstance()
                .translucentFallingBlock(atPos, state);
            bl.setDisableDepth(true)
                .setScaleFunction(new EntityComplexFX.ScaleFunction.Shrink<>());
            bl.setMotion(0, 0.03, 0)
                .setAlphaFunction(EntityComplexFX.AlphaFunction.PYRAMID);
            if (tumble) {
                bl.tumble();
            }
            bl.setMaxAge(35);
        }
    }

    @Nonnull
    @Override
    public Item getInertVariant() {
        return ItemsAS.crystalPickaxe;
    }

}
