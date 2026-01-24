/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.patreon.base;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.base.patreon.PatreonEffectHelper;
import hellfirepvp.astralsorcery.common.util.ItemUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PtEffectHelmetRender
 * Created by HellFirePvP
 * Date: 25.02.2019 / 20:24
 */
public class PtEffectHelmetRender extends PatreonEffectHelper.PatreonEffect {

    private final UUID playerUUID;
    private final ItemStack dummyHeadItem;
    private boolean addedHelmet = false;

    public PtEffectHelmetRender(UUID uniqueId, PatreonEffectHelper.FlareColor chosenColor, UUID plUUID,
        ItemStack headStack) {
        super(uniqueId, chosenColor);
        this.playerUUID = plUUID;
        this.dummyHeadItem = headStack;
    }

    @Override
    public void initialize() {
        super.initialize();

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onRenderPre(RenderPlayerEvent.Pre ev) {
        EntityPlayer player = ev.entityPlayer; // 1.7.10: Use entityPlayer instead of getEntityPlayer()
        if (player.getUniqueID()
            .equals(playerUUID) && player.inventory.armorInventory[3] == null) { // 1.7.10: Armor slot 3 = HEAD, check
                                                                                 // for null
            player.inventory.armorInventory[3] = ItemUtils.copyStackWithSize(dummyHeadItem, 1); // 1.7.10: Direct array
                                                                                                // access
            addedHelmet = true;
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onRenderPost(RenderPlayerEvent.Post ev) {
        EntityPlayer player = ev.entityPlayer; // 1.7.10: Use entityPlayer instead of getEntityPlayer()
        if (player.getUniqueID()
            .equals(playerUUID) && addedHelmet) {
            player.inventory.armorInventory[3] = null; // 1.7.10: Direct array access
            addedHelmet = false;
        }
    }

}
