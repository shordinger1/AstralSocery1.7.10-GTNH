/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations;

import hellfirepvp.astralsorcery.common.base.Mods;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.registry.GameData;
//import hellfirepvp.astralsorcery.common.base.Mods;
import hellfirepvp.astralsorcery.common.block.BlockBlackMarble;
import hellfirepvp.astralsorcery.common.block.BlockMarble;
//import hellfirepvp.astralsorcery.common.migration.IBlockState;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ModIntegrationChisel
 * Created by HellFirePvP
 * Date: 17.07.2017 / 18:02
 */
public class ModIntegrationChisel {

    public static void sendVariantIMC() {
        // DISABLED: IBlockState not available in 1.7.10
        // This functionality is not compatible with 1.7.10 API
        /*
        for (BlockMarble.MarbleBlockType type : BlockMarble.MarbleBlockType.values()) {
            if (type.obtainableInCreative()) {
                IBlockState state = type.asBlock();
                Block block = state.getBlock();
                int meta = state.getMetadata();
                sendVariantMapping(block, meta, type.asStack(), ChiselGroup.MARBLE);
            }
        }
        for (BlockBlackMarble.BlackMarbleBlockType type : BlockBlackMarble.BlackMarbleBlockType.values()) {
            if (type.obtainableInCreative()) {
                IBlockState state = type.asBlock();
                Block block = state.getBlock();
                int meta = state.getMetadata();
                sendVariantMapping(block, meta, type.asStack(), ChiselGroup.SOOTY_MARBLE);
            }
        }
        */
    }

    private static void sendVariantMapping(Block block, int meta, ItemStack stack, ChiselGroup group) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("group", group.group);
        tag.setTag("stack", stack.writeToNBT(new NBTTagCompound()));
        // In 1.7.10, use GameData to get the registry name
        String registryName = GameData.getBlockRegistry()
            .getNameForObject(block);
        tag.setString("block", registryName);
        tag.setInteger("meta", meta);
        // In 1.7.10, use sendMessage instead of addChatMessage
        FMLInterModComms.sendMessage(Mods.CHISEL.modid, "add_variation", tag);
    }

    public enum ChiselGroup {

        MARBLE("marble"),
        SOOTY_MARBLE("sooty_marble");

        private final String group;

        ChiselGroup(String group) {
            this.group = group;
        }
    }

}
