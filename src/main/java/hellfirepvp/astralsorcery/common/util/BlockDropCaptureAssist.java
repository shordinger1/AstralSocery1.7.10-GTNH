/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockDropCaptureAssist
 * Created by HellFirePvP
 * Date: 11.03.2017 / 22:07
 */
public class BlockDropCaptureAssist {

    public static BlockDropCaptureAssist instance = new BlockDropCaptureAssist();

    private static Map<Integer, ArrayList<ItemStack>> capturedStacks = new HashMap<>();
    private static int stack = -1;

    private BlockDropCaptureAssist() {}

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onDrop(EntityJoinWorldEvent event) {
        if (event.world instanceof WorldServer && event.getEntity() instanceof EntityItem) {
            ItemStack itemStack = ((EntityItem) event.getEntity()).getEntityItem();
            if (stack > -1) {
                event.setCanceled(true);
                if (!itemStack == null || itemStack.stackSize <= 0) {
                    if (itemStack.getItem() instanceof ItemBlock && ((ItemBlock) itemStack.getItem()).getBlock()
                        .equals(Blocks.STONE)) {
                        event.getEntity()
                            .setDead();
                        return;
                    }
                    // Apparently concurrency sometimes gets us here...
                    if (stack > -1) {
                        if (!capturedStacks.containsKey(stack)) {
                            capturedStacks.put(stack, new ArrayList<>());
                        }
                        capturedStacks.get(stack)
                            .add(itemStack);
                    }
                }
                event.getEntity()
                    .setDead();
            }
        }
    }

    public static void startCapturing() {
        stack++;
        capturedStacks.put(stack, new ArrayList<>());
    }

    public static ArrayList<ItemStack> getCapturedStacksAndStop() {
        ArrayList<ItemStack> pop = capturedStacks.get(stack);
        capturedStacks.remove(stack);
        stack = Math.max(-1, stack - 1);
        return pop == null ? new ArrayList<>() : pop;
    }

}
