/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Base packet class for network communication
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet;

import java.io.IOException;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import hellfirepvp.astralsorcery.common.lib.Constants;
import hellfirepvp.astralsorcery.common.util.LogHelper;
import hellfirepvp.astralsorcery.common.util.math.BlockPos;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

/**
 * Base packet class for all network packets
 */
public abstract class AbstractPacket implements IMessage {

    public abstract void fromBytes(ByteBuf buf);

    public abstract void toBytes(ByteBuf buf);

    protected ByteBufInputStream readStream(ByteBuf buf) throws IOException {
        return new ByteBufInputStream(buf);
    }

    protected ByteBufOutputStream writeStream(ByteBuf buf) throws IOException {
        return new ByteBufOutputStream(buf);
    }

    public boolean isValid() {
        return true;
    }

    public String getPacketType() {
        return this.getClass()
            .getSimpleName();
    }

    protected void logPacket() {
        if (Constants.IS_DEBUG) {
            LogHelper.debug("Processing packet: " + getPacketType());
        }
    }

    protected void validateSize(ByteBuf buf, int requiredSize) throws IOException {
        if (buf.readableBytes() < requiredSize) {
            throw new IOException(
                "Buffer too small: need " + requiredSize + " bytes, have " + buf.readableBytes() + " bytes");
        }
    }

    protected String readString(ByteBuf buf, int maxLength) {
        try {
            int length = buf.readShort();
            if (length < 0 || length > maxLength) {
                LogHelper.error("Invalid string length: " + length);
                return null;
            }
            validateSize(buf, length);
            byte[] bytes = new byte[length];
            buf.readBytes(bytes);
            return new String(bytes, "UTF-8");
        } catch (Exception e) {
            LogHelper.error("Failed to read string", e);
            return null;
        }
    }

    protected boolean writeString(ByteBuf buf, String str) {
        if (str == null) {
            buf.writeShort(-1);
            return true;
        }

        try {
            byte[] bytes = str.getBytes("UTF-8");
            if (bytes.length > Short.MAX_VALUE) {
                LogHelper.error("String too long: " + bytes.length + " bytes");
                return false;
            }
            buf.writeShort(bytes.length);
            buf.writeBytes(bytes);
            return true;
        } catch (Exception e) {
            LogHelper.error("Failed to write string", e);
            return false;
        }
    }

    protected BlockPos readBlockPos(ByteBuf buf) {
        try {
            int x = buf.readInt();
            int y = buf.readInt();
            int z = buf.readInt();
            return new BlockPos(x, y, z);
        } catch (Exception e) {
            LogHelper.error("Failed to read BlockPos", e);
            return null;
        }
    }

    protected boolean writeBlockPos(ByteBuf buf, BlockPos pos) {
        if (pos == null) {
            LogHelper.error("Cannot write null BlockPos");
            return false;
        }

        try {
            buf.writeInt(pos.getX());
            buf.writeInt(pos.getY());
            buf.writeInt(pos.getZ());
            return true;
        } catch (Exception e) {
            LogHelper.error("Failed to write BlockPos", e);
            return false;
        }
    }
}
