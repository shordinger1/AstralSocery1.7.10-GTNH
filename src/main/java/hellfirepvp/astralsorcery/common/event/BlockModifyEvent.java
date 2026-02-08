/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import cpw.mods.fml.common.eventhandler.Event;
import hellfirepvp.astralsorcery.common.util.math.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockModifyEvent
 * Created by HellFirePvP
 * Date: 04.08.2016 / 10:49
 */
public class BlockModifyEvent extends Event {

    private final Chunk chunk;
    private final World world;
    private final BlockPos at;
    private final Block oldBlock, newBlock;
    private final int oldMeta, newMeta;

    public BlockModifyEvent(World world, Chunk chunk, BlockPos at, Block oldBlock, int oldMeta, Block newBlock,
        int newMeta) {
        this.at = at;
        this.chunk = chunk;
        this.world = world;
        this.oldBlock = oldBlock;
        this.oldMeta = oldMeta;
        this.newBlock = newBlock;
        this.newMeta = newMeta;
    }

    public BlockPos getPos() {
        return at;
    }

    public World getWorld() {
        return world;
    }

    public Chunk getChunk() {
        return chunk;
    }

    @Nullable
    public net.minecraft.tileentity.TileEntity getTileEntity() {
        return world.getTileEntity(at.getX(), at.getY(), at.getZ());
    }

    public Block getOldBlock() {
        return oldBlock;
    }

    public Block getNewBlock() {
        return newBlock;
    }

    public int getOldMeta() {
        return oldMeta;
    }

    public int getNewMeta() {
        return newMeta;
    }

}
