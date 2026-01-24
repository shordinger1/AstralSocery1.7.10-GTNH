/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.key;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.KeyPerk;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyDigTypes
 * Created by HellFirePvP
 * Date: 20.07.2018 / 17:39
 */
public class KeyDigTypes extends KeyPerk {

    private static boolean checkingSpeed = false;

    public KeyDigTypes(String name, int x, int y) {
        super(name, x, y);
    }

    @SubscribeEvent
    public void checkHarvest(PlayerEvent.HarvestCheck event) {
        if (event.success) {
            return;
        }

        EntityPlayer player = event.entityPlayer;
        Side side = player.worldObj.isRemote ? Side.CLIENT : Side.SERVER;
        PlayerProgress prog = ResearchManager.getProgress(player, side);
        if (prog.hasPerkEffect(this)) {
            ItemStack heldMainHand = player.getCurrentEquippedItem();
            if (!(heldMainHand == null || heldMainHand.stackSize <= 0) && heldMainHand.getItem()
                .getToolClasses(heldMainHand)
                .contains("pickaxe")) {
                Block tryHarvest = event.block;
                String toolRequired = tryHarvest.getHarvestTool(0);
                if (toolRequired == null || toolRequired.equalsIgnoreCase("shovel")
                    || toolRequired.equalsIgnoreCase("axe")) {
                    event.success = true;
                }
            }
        }
    }

    @SubscribeEvent
    public void onHarvestSpeed(PlayerEvent.BreakSpeed event) {
        if (checkingSpeed) return;

        EntityPlayer player = event.entityPlayer;
        Side side = player.worldObj.isRemote ? Side.CLIENT : Side.SERVER;
        PlayerProgress prog = ResearchManager.getProgress(player, side);
        if (prog.hasPerkEffect(this)) {
            Block broken = event.block;
            ItemStack playerMainHand = player.getCurrentEquippedItem();
            if (!(playerMainHand == null || playerMainHand.stackSize <= 0)) {
                if (playerMainHand.getItem()
                    .getToolClasses(playerMainHand)
                    .contains("pickaxe")) {
                    if (!broken.isToolEffective("pickaxe", event.metadata)) {
                        if (broken.isToolEffective("shovel", event.metadata)
                            || broken.isToolEffective("axe", event.metadata)) {
                            checkingSpeed = true;
                            event.newSpeed = (float) Math
                                .max(event.newSpeed, playerMainHand.func_150997_a(Blocks.stone));
                            checkingSpeed = false;
                        }
                    }
                }
            }
        }
    }

}
