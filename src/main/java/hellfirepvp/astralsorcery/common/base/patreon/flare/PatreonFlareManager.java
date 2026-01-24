/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.patreon.flare;

import java.util.*;
import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import hellfirepvp.astralsorcery.common.auxiliary.tick.ITickHandler;
import hellfirepvp.astralsorcery.common.base.patreon.PatreonEffectHelper;
import hellfirepvp.astralsorcery.common.data.DataPatreonFlares;
import hellfirepvp.astralsorcery.common.data.SyncDataHolder;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatreonFlareManager
 * Created by HellFirePvP
 * Date: 23.06.2018 / 16:04
 */
public class PatreonFlareManager implements ITickHandler {

    public static PatreonFlareManager INSTANCE = new PatreonFlareManager();

    private PatreonFlareManager() {}

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        MinecraftServer server = FMLCommonHandler.instance()
            .getMinecraftServerInstance();
        if (server == null) return;

        DataPatreonFlares dataFlares = SyncDataHolder.getDataServer(SyncDataHolder.DATA_PATREON_FLARES);
        List<UUID> ownerDiff = new ArrayList<>(dataFlares.getOwners(Side.SERVER));

        // 1.7.10: Use server.getConfigurationManager() instead of getPlayerList()
        for (Map.Entry<UUID, List<PatreonEffectHelper.PatreonEffect>> effect : PatreonEffectHelper
            .getPatreonEffects(server.getConfigurationManager().playerEntityList)
            .entrySet()) {
            // 1.7.10: Use func_152612_a() to get player by username, or iterate through player list
            EntityPlayerMP owner = null;
            for (Object playerObj : server.getConfigurationManager().playerEntityList) {
                if (playerObj instanceof EntityPlayerMP) {
                    EntityPlayerMP player = (EntityPlayerMP) playerObj;
                    if (player.getUniqueID()
                        .equals(effect.getKey())) {
                        owner = player;
                        break;
                    }
                }
            }
            if (owner == null) continue;
            Map<PatreonEffectHelper.PatreonEffect, PatreonPartialEntity> knownEntities = dataFlares
                .getEntities(Side.SERVER, owner.getUniqueID());
            ownerDiff.remove(owner.getUniqueID());

            for (PatreonEffectHelper.PatreonEffect pe : effect.getValue()) {
                if (!pe.hasPartialEntity()) continue;

                PatreonPartialEntity entity = knownEntities.get(pe);
                if (entity == null) {
                    entity = dataFlares.createEntity(owner, pe);
                }

                World plWorld = owner.worldObj;
                if (entity.getLastTickedDim() != null && plWorld.provider.dimensionId != entity.getLastTickedDim()) {
                    entity.setPositionNear(owner);
                }

                if (entity.update(plWorld)) {
                    dataFlares.updateEntity(entity);
                }
            }
        }

        for (UUID removedOwner : ownerDiff) {
            Map<PatreonEffectHelper.PatreonEffect, PatreonPartialEntity> knownEntities = dataFlares
                .getEntities(Side.SERVER, removedOwner);

            for (PatreonPartialEntity entity : knownEntities.values()) {
                dataFlares.destroyEntity(entity);
            }
        }
    }

    @Override
    public EnumSet<TickEvent.Type> getHandledTypes() {
        return EnumSet.of(TickEvent.Type.SERVER);
    }

    @Override
    public boolean canFire(TickEvent.Phase phase) {
        return phase == TickEvent.Phase.END;
    }

    @Override
    public String getName() {
        return "Patreon Flare Manager (Server)";
    }
}
