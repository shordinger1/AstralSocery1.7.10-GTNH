/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.key;

import java.util.Iterator;
import java.util.Random;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.world.BlockEvent;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.KeyPerk;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyMagnetDrops
 * Created by HellFirePvP
 * Date: 23.11.2018 / 16:52
 */
public class KeyMagnetDrops extends KeyPerk {

    public KeyMagnetDrops(String name, int x, int y) {
        super(name, x, y);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onDropLoot(LivingDropsEvent event) {
        DamageSource source = event.source;
        if (source.getEntity() != null && source.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) source.getEntity();
            Side side = player.worldObj.isRemote ? Side.CLIENT : Side.SERVER;
            PlayerProgress prog = ResearchManager.getProgress(player, side);
            if (prog.hasPerkEffect(this)) {
                Iterator<EntityItem> it = event.drops.iterator();
                while (it.hasNext()) {
                    EntityItem item = it.next();
                    if (this.attemptPickup(player, item)) {
                        it.remove();
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onDropHarvest(BlockEvent.HarvestDropsEvent event) {
        World world = event.world;
        if (world.isRemote) {
            return;
        }

        EntityPlayer player = event.harvester;
        if (player == null) {
            return;
        }
        PlayerProgress prog = ResearchManager.getProgress(player, Side.SERVER);
        if (!prog.hasPerkEffect(this)) {
            return;
        }

        // Simulate normal drop-logic to see what/which drops to try add before
        // setting chances to 1 with remaining not-capturable drops
        Random r = world.rand;
        Iterator<ItemStack> iterator = event.drops.iterator();
        while (iterator.hasNext()) {
            ItemStack drop = iterator.next();
            if (r.nextFloat() <= event.dropChance) {
                EntityItem i = new EntityItem(world, player.posX, player.posY, player.posZ, drop);
                if (this.attemptPickup(player, i)) {
                    iterator.remove();
                }
            } else {
                iterator.remove();
            }
        }
    }

    private boolean attemptPickup(EntityPlayer player, EntityItem item) {
        ItemStack stack = item.getEntityItem();
        if (stack == null || stack.stackSize <= 0) {
            return false;
        }
        item.delayBeforeCanPickup = 0; // 1.7.10: Direct field access for no pickup delay
        try {
            item.onCollideWithPlayer(player);
        } catch (Exception ignored) {
            // Guess some mod could run into an issue here...
        }
        if (!item.isDead) {
            item.delayBeforeCanPickup = 10; // 1.7.10: Default pickup delay
            return false;
        } else {
            return true;
        }
    }

}
