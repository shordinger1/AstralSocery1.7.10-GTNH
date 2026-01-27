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
        // 1.7.10: EntityJoinWorldEvent has 'entity' field, not getEntity() method
        if (event.world instanceof WorldServer && event.entity instanceof EntityItem) {
            ItemStack itemStack = ((EntityItem) event.entity).getEntityItem();
            if (stack > -1) {
                event.setCanceled(true);
                // 1.7.10: Fix null check logic - should be itemStack != null, not !itemStack == null
                if (itemStack != null && itemStack.stackSize > 0) {
                    // 1.7.10: ItemBlock uses field_150939_a for block reference
                    if (itemStack.getItem() instanceof ItemBlock
                        && ((ItemBlock) itemStack.getItem()).field_150939_a.equals(Blocks.stone)) {
                        event.entity.setDead();
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
                event.entity.setDead();
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
