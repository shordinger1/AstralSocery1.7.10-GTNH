/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Grapple Wand - Launches a grappling hook
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.wand;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import hellfirepvp.astralsorcery.common.base.AstralBaseItem;
import hellfirepvp.astralsorcery.common.entity.EntityGrapplingHook;
import hellfirepvp.astralsorcery.common.lib.CreativeTabsAS;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * Grapple Wand
 * <p>
 * A wand that launches a grappling hook to pull the player.
 * <p>
 * Features:
 * - Right-click to launch grappling hook
 * - Pulls player toward target
 * - Free to use (no charge system yet)
 * <p>
 * TODO (Future):
 * - Implement alignment charge system
 * - Add cooldown
 * - Add custom sound effects
 * - Add custom particle effects
 */
public class ItemGrappleWand extends AstralBaseItem {

    public ItemGrappleWand() {
        super();
        setMaxStackSize(1); // Only one wand per stack
        setMaxDamage(0); // No durability - infinite use
        setCreativeTab(CreativeTabsAS.ASTRAL_SORCERY_TAB);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        // Client-side: just return
        if (world.isRemote) {
            return stack;
        }

        // Server-side: launch grappling hook
        try {
            // Spawn the grappling hook entity
            EntityGrapplingHook hook = new EntityGrapplingHook(world, player);
            world.spawnEntityInWorld(hook);

            // Play bow sound as placeholder
            world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F);

            LogHelper.debug("Player " + player.getCommandSenderName() + " fired grappling hook");
        } catch (Exception e) {
            LogHelper.error("Failed to spawn grappling hook: " + e.getMessage());
            e.printStackTrace();
        }

        return stack;
    }
}
