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
import net.minecraftforge.common.config.Configuration;

import cpw.mods.fml.relauncher.Side;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.KeyPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.types.IPlayerTickPerk;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.data.config.entry.ConfigEntry;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.tile.TileIlluminator;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.WrapMathHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeySpawnLights
 * Created by HellFirePvP
 * Date: 11.08.2018 / 21:27
 */
public class KeySpawnLights extends KeyPerk implements IPlayerTickPerk {

    private int lightSpawnRate = 15;
    private int radiusToSpawnLight = 5;

    public KeySpawnLights(String name, int x, int y) {
        super(name, x, y);
        Config.addDynamicEntry(new ConfigEntry(ConfigEntry.Section.PERKS, name) {

            @Override
            public void loadFromConfig(Configuration cfg) {
                lightSpawnRate = cfg.getInt(
                    "SpawnLightRate",
                    getConfigurationSection(),
                    lightSpawnRate,
                    5,
                    100_000,
                    "Defines the rate in ticks a position to spawn a light in is attempted to be found near the player.");
                radiusToSpawnLight = cfg.getInt(
                    "RadiusSpawnLight",
                    getConfigurationSection(),
                    radiusToSpawnLight,
                    2,
                    10,
                    "Defines the radius around the player the perk will search for a suitable position.");
            }
        });
    }

    @Override
    protected void applyEffectMultiplier(double multiplier) {
        super.applyEffectMultiplier(multiplier);

        this.lightSpawnRate = WrapMathHelper.ceil(this.lightSpawnRate * multiplier);
        this.radiusToSpawnLight = WrapMathHelper.ceil(this.radiusToSpawnLight * multiplier);
    }

    @Override
    public void onPlayerTick(EntityPlayer player, Side side) {
        if (side == Side.SERVER) {
            if (player.ticksExisted % lightSpawnRate == 0) {
                int attempts = 4;
                while (attempts > 0) {
                    BlockPos pos = new BlockPos(player).add(
                        rand.nextInt(radiusToSpawnLight) * (rand.nextBoolean() ? 1 : -1),
                        rand.nextInt(radiusToSpawnLight) * (rand.nextBoolean() ? 1 : -1),
                        rand.nextInt(radiusToSpawnLight) * (rand.nextBoolean() ? 1 : -1));
                    Block block = player.worldObj.getBlock(pos.getX(), pos.getY(), pos.getZ());
                    int meta = player.worldObj.getBlockMetadata(pos.getX(), pos.getY(), pos.getZ());
                    if (MiscUtils.isChunkLoaded(player.worldObj, pos) && TileIlluminator.illuminatorCheck.isStateValid(
                        player.worldObj,pos, block, meta)) {
                        if (player.worldObj
                            .setBlock(pos.getX(), pos.getY(), pos.getZ(), BlocksAS.blockVolatileLight, 0, 3)) {
                            return;
                        }
                    }
                    attempts--;
                }
            }
        }
    }
}
