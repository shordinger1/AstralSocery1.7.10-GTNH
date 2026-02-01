/*******************************************************************************
 * Astral Sorcery - Particle Effect Test
 *
 * Test class to demonstrate the particle effect system
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * Test class for particle effects
 * This demonstrates how to use the particle effect system
 */
public class ParticleEffectTest {

    private int tickCounter = 0;

    @SubscribeEvent
    public void onRenderTick(RenderWorldLastEvent event) {
        tickCounter++;

        if (tickCounter % 5 == 0) {
            EntityPlayer player = net.minecraft.client.Minecraft.getMinecraft().thePlayer;
            if (player != null) {
                // Spawn test particles around the player
                Vector3 pos = Vector3.atEntityCorner(player);
                double offsetX = (Math.random() - 0.5) * 2;
                double offsetY = (Math.random() - 0.5) * 2 + 1;
                double offsetZ = (Math.random() - 0.5) * 2;

                EffectHelper.genericFlareParticle(pos.getX() + offsetX, pos.getY() + offsetY, pos.getZ() + offsetZ)
                    .motion((Math.random() - 0.5) * 0.02, (Math.random() - 0.5) * 0.02, (Math.random() - 0.5) * 0.02)
                    .scale(0.5F + (float) Math.random() * 0.5F);
            }
        }
    }
}
