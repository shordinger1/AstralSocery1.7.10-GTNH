/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations;

import hellfirepvp.astralsorcery.common.util.data.Tuple;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.migration.net.darkhax.gamestages.GameStageHelper;
import hellfirepvp.astralsorcery.common.migration.net.darkhax.orestages.api.OreTiersAPI;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ModIntegrationOreStages
 * Created by HellFirePvP
 * Date: 19.05.2018 / 18:56
 */
public class ModIntegrationOreStages {

    @SideOnly(Side.CLIENT)
    @Optional.Method(modid = "orestages")
    public static boolean canSeeOreClient(Block test) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (player == null) return false;
        Tuple<String, OreTiersAPI.BlockMeta> replacement;
        if ((replacement = OreTiersAPI.getStageInfo(test)) != null) {
            return GameStageHelper.clientHasStage(player, replacement.getKey());
        }
        return true;
    }

}
