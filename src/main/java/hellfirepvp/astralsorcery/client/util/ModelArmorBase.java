/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ModelArmorBase
 * Created by HellFirePvP
 * Date: 18.10.2017 / 20:27
 */
// Ripoff of net.minecraft.client.model.ModelArmorStandArmor cause armor stands are retarded
public class ModelArmorBase extends ModelBiped {

    // 1.7.10: Use int instead of EntityEquipmentSlot (0=helmet, 1=chestplate, 2=leggings, 3=boots)
    protected final int slot;

    public ModelArmorBase(int targetSlot) {
        this.slot = targetSlot;
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
        float headPitch, float scaleFactor, Entity entityIn) {
        // 1.7.10: EntityArmorStand doesn't exist (added in 1.8), so just use the default behavior
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
    }

}
