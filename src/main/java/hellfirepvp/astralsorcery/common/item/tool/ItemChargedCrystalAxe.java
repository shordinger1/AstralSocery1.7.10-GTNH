/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool;

import java.awt.*;
import java.util.Map;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.network.packet.server.PktDualParticleEvent;
import hellfirepvp.astralsorcery.common.structure.array.BlockArray;
import hellfirepvp.astralsorcery.common.tile.TileFakeTree;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.WrapMathHelper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.struct.TreeDiscoverer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemChargedCrystalAxe
 * Created by HellFirePvP
 * Date: 11.03.2017 / 22:04
 */
public class ItemChargedCrystalAxe extends ItemCrystalAxe implements ChargedCrystalToolBase {

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, int x, int y, int z, EntityPlayer player) {
        World world = player.worldObj;
        // 1.7.10: No CooldownTracker, removed cooldown check; use world variable
        if (!world.isRemote && !player.isSneaking()) {
            BlockPos pos = new BlockPos(x, y, z);
            BlockArray tree = TreeDiscoverer.tryCaptureTreeAt(world, pos, 9, true);
            if (tree != null) {
                Map<BlockPos, BlockArray.BlockInformation> pattern = tree.getPattern();
                for (Map.Entry<BlockPos, BlockArray.BlockInformation> blocks : pattern.entrySet()) {
                    if (world.setBlock(
                        blocks.getKey()
                            .getX(),
                        blocks.getKey()
                            .getY(),
                        blocks.getKey()
                            .getZ(),
                        BlocksAS.blockFakeTree,
                        0,
                        3)) {
                        TileFakeTree tt = MiscUtils.getTileAt(world, blocks.getKey(), TileFakeTree.class, true);
                        if (tt != null) {
                            tt.setupTile(player, itemstack, blocks.getValue().state);
                            itemstack.damageItem(1, player);
                        } else {
                            world.setBlock(
                                blocks.getKey()
                                    .getX(),
                                blocks.getKey()
                                    .getY(),
                                blocks.getKey()
                                    .getZ(),
                                blocks.getValue().state,
                                0,
                                3);
                        }
                    }
                }
                // 1.7.10: No cooldown to set
                return true;
            }
        }
        return super.onBlockStartBreak(itemstack, x, y, z, player);
    }

    @SideOnly(Side.CLIENT)
    public static void playDrainParticles(PktDualParticleEvent pktDualParticleEvent) {
        Vector3 to = pktDualParticleEvent.getTargetVec();
        int colorHex = WrapMathHelper.floor(pktDualParticleEvent.getAdditionalData());
        Color c = new Color(colorHex);
        for (int i = 0; i < 10; i++) {
            Vector3 from = pktDualParticleEvent.getOriginVec()
                .add(itemRand.nextFloat(), itemRand.nextFloat(), itemRand.nextFloat());
            Vector3 mov = to.clone()
                .subtract(from)
                .normalize()
                .multiply(0.1 + 0.1 * itemRand.nextFloat());
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(from.getX(), from.getY(), from.getZ());
            p.motion(mov.getX(), mov.getY(), mov.getZ())
                .setMaxAge(30 + itemRand.nextInt(25));
            p.gravity(0.004)
                .scale(0.25F)
                .setColor(c);
        }
    }

    @Nonnull
    @Override
    public Item getInertVariant() {
        return ItemsAS.crystalAxe;
    }

}
