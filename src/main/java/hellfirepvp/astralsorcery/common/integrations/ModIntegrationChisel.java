/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import cpw.mods.fml.common.event.FMLInterModComms;
import hellfirepvp.astralsorcery.common.base.Mods;
import hellfirepvp.astralsorcery.common.block.BlockBlackMarble;
import hellfirepvp.astralsorcery.common.block.BlockMarble;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ModIntegrationChisel
 * Created by HellFirePvP
 * Date: 17.07.2017 / 18:02
 */
public class ModIntegrationChisel {

    public static void sendVariantIMC() {
        for (BlockMarble.MarbleBlockType type : BlockMarble.MarbleBlockType.values()) {
            if (type.obtainableInCreative()) {
                sendVariantMapping(type.asBlock(), type.asStack(), ChiselGroup.MARBLE);
            }
        }
        for (BlockBlackMarble.BlackMarbleBlockType type : BlockBlackMarble.BlackMarbleBlockType.values()) {
            if (type.obtainableInCreative()) {
                sendVariantMapping(type.asBlock(), type.asStack(), ChiselGroup.SOOTY_MARBLE);
            }
        }
    }

    private static void sendVariantMapping(Block state, ItemStack stack, ChiselGroup group) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("group", group.group);
        tag.setTag("stack", stack.writeToNBT(new NBTTagCompound()));
        tag.setString(
            "block",
            state.getRegistryName()
                .toString());
        tag.setInteger("meta", state.getMetaFromState(state));
        FMLInterModComms.addChatMessage(Mods.CHISEL.modid, "add_variation", tag);
    }

    public static enum ChiselGroup {

        MARBLE("marble"),
        SOOTY_MARBLE("sooty_marble");

        private final String group;

        ChiselGroup(String group) {
            this.group = group;
        }
    }

}
