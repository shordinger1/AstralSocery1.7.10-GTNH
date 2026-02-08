/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event;

import net.minecraft.world.World;

import cpw.mods.fml.common.eventhandler.Event;
import hellfirepvp.astralsorcery.common.util.math.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StarlightNetworkEvent
 * Created by HellFirePvP
 * Date: 16.12.2017 / 15:35
 */
public class StarlightNetworkEvent extends Event {

    private final World world;
    private final BlockPos pos;

    public StarlightNetworkEvent(World world, BlockPos pos) {
        this.world = world;
        this.pos = pos;
    }

    public World getWorld() {
        return world;
    }

    public BlockPos getPos() {
        return pos;
    }

    public static class NetworkConstruct extends StarlightNetworkEvent {

        public NetworkConstruct(World world, BlockPos pos) {
            super(world, pos);
        }

    }

    public static class NetworkRemoved extends StarlightNetworkEvent {

        public NetworkRemoved(World world, BlockPos pos) {
            super(world, pos);
        }

    }

    public static class NetworkActivate extends StarlightNetworkEvent {

        public NetworkActivate(World world, BlockPos pos) {
            super(world, pos);
        }

    }

}
