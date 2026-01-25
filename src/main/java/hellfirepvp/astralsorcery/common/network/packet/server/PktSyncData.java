/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet.server;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.data.AbstractData;
import hellfirepvp.astralsorcery.common.data.SyncDataHolder;
import hellfirepvp.astralsorcery.common.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktSyncData
 * Created by HellFirePvP
 * Date: 07.05.2016 / 01:12
 */
public class PktSyncData implements IMessage, IMessageHandler<PktSyncData, IMessage> {

    private Map<String, AbstractData> data = new HashMap<>();
    private boolean shouldSyncAll = false;

    public PktSyncData() {}

    public PktSyncData(Map<String, AbstractData> dataToSend, boolean shouldSyncAll) {
        this.data = dataToSend;
        this.shouldSyncAll = shouldSyncAll;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        // 1.7.10: Use ByteBufUtils instead of PacketBuffer methods
        int size = buf.readInt();

        for (int i = 0; i < size; i++) {
            String key = ByteBufUtils.readString(buf);

            byte providerId = buf.readByte();
            AbstractData.AbstractDataProvider<? extends AbstractData> provider = AbstractData.Registry
                .getProvider(providerId);
            if (provider == null) {
                AstralSorcery.log.warn("Provider for ID " + providerId + " doesn't exist! Skipping...");
                continue;
            }

            // 1.7.10: ByteBufUtils.readNBTTag doesn't throw IOException, it catches exceptions internally
            NBTTagCompound cmp = ByteBufUtils.readNBTTag(buf);

            AbstractData dat = provider.provideNewInstance(Side.CLIENT);
            dat.readRawFromPacket(cmp);

            data.put(key, dat);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        // 1.7.10: Use ByteBufUtils instead of PacketBuffer methods
        buf.writeInt(data.size());

        for (String key : data.keySet()) {
            AbstractData dat = data.get(key);
            NBTTagCompound cmp = new NBTTagCompound();
            if (shouldSyncAll) {
                dat.writeAllDataToPacket(cmp);
            } else {
                dat.writeToPacket(cmp);
            }

            ByteBufUtils.writeString(buf, key);

            byte providerId = dat.getProviderID();
            buf.writeByte(providerId);
            ByteBufUtils.writeNBTTag(buf, cmp);
        }
    }

    @Override
    public IMessage onMessage(PktSyncData message, MessageContext ctx) {
        AstralSorcery.proxy.scheduleClientside(() -> SyncDataHolder.receiveServerPacket(message.data));
        return null;
    }

}
