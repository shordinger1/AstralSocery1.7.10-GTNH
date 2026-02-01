/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect;

import java.util.*;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EffectHandler
 * Created by HellFirePvP
 * Date: 12.05.2016 / 17:44
 *
 * 1.7.10 Port:
 * - Manages all particle effects
 * - Handles effect lifecycle, rendering, and ticking
 * - Integrated with Forge event system
 */
public final class EffectHandler {

    public static final Random STATIC_EFFECT_RAND = new Random();
    public static final EffectHandler instance = new EffectHandler();

    private static boolean acceptsNewParticles = true, cleanRequested = false;
    private static List<IComplexEffect> toAddBuffer = new LinkedList<>();

    public static final Map<IComplexEffect.RenderTarget, Map<Integer, List<IComplexEffect>>> complexEffects = new HashMap<>();
    public static final List<EntityFXFacingParticle> fastRenderParticles = new LinkedList<>();
    public static final List<EntityFXFacingParticle> fastRenderGatewayParticles = new LinkedList<>();

    private EffectHandler() {}

    public static EffectHandler getInstance() {
        return instance;
    }

    @SubscribeEvent
    public void onOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type == RenderGameOverlayEvent.ElementType.ALL) {
            acceptsNewParticles = false;
            Map<Integer, List<IComplexEffect>> layeredEffects = complexEffects
                .get(IComplexEffect.RenderTarget.OVERLAY_TEXT);
            for (int i = 0; i <= 2; i++) {
                for (IComplexEffect effect : layeredEffects.get(i)) {
                    GL11.glPushMatrix();
                    GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                    effect.render(event.partialTicks);
                    GL11.glPopAttrib();
                    GL11.glPopMatrix();
                }
            }
            acceptsNewParticles = true;
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onRender(RenderWorldLastEvent event) {
        float pTicks = event.partialTicks;
        acceptsNewParticles = false;

        // Render fast particles (billboard particles)
        EntityFXFacingParticle.renderFast(pTicks, fastRenderParticles);

        // Render complex effects
        Map<Integer, List<IComplexEffect>> layeredEffects = complexEffects.get(IComplexEffect.RenderTarget.RENDERLOOP);
        for (int i = 0; i <= 2; i++) {
            for (IComplexEffect effect : layeredEffects.get(i)) {
                effect.render(pTicks);
            }
        }

        // Render gateway particles
        EntityFXFacingParticle.renderFast(pTicks, fastRenderGatewayParticles);

        acceptsNewParticles = true;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tick();
    }

    public EntityComplexFX registerFX(EntityComplexFX entityComplexFX) {
        register(entityComplexFX);
        return entityComplexFX;
    }

    private void register(final IComplexEffect effect) {
        if (effect == null || Minecraft.getMinecraft()
            .isGamePaused()) return;

        if (acceptsNewParticles) {
            registerUnsafe(effect);
        } else {
            toAddBuffer.add(effect);
        }
    }

    private void registerUnsafe(IComplexEffect effect) {
        if (!mayAcceptParticle(effect)) return;

        if (effect instanceof EntityFXFacingParticle.Gateway) {
            fastRenderGatewayParticles.add((EntityFXFacingParticle) effect);
        } else if (effect instanceof EntityFXFacingParticle) {
            fastRenderParticles.add((EntityFXFacingParticle) effect);
        } else {
            complexEffects.get(effect.getRenderTarget())
                .get(effect.getLayer())
                .add(effect);
        }
        effect.clearRemoveFlag();
    }

    public void tick() {
        if (cleanRequested) {
            for (IComplexEffect.RenderTarget t : IComplexEffect.RenderTarget.values()) {
                for (int i = 0; i <= 2; i++) {
                    List<IComplexEffect> effects = complexEffects.get(t)
                        .get(i);
                    effects.forEach(IComplexEffect::flagAsRemoved);
                    effects.clear();
                }
            }
            fastRenderParticles.clear();
            fastRenderGatewayParticles.clear();
            toAddBuffer.clear();
            cleanRequested = false;
        }

        if (Minecraft.getMinecraft().thePlayer == null) {
            return;
        }

        acceptsNewParticles = false;

        // Tick complex effects
        for (IComplexEffect.RenderTarget target : complexEffects.keySet()) {
            Map<Integer, List<IComplexEffect>> layeredEffects = complexEffects.get(target);
            for (int i = 0; i <= 2; i++) {
                Iterator<IComplexEffect> iterator = layeredEffects.get(i)
                    .iterator();
                while (iterator.hasNext()) {
                    IComplexEffect effect = iterator.next();
                    effect.tick();
                    if (effect.canRemove()) {
                        effect.flagAsRemoved();
                        iterator.remove();
                    }
                }
            }
        }

        // Tick fast particles
        Vector3 playerPos = Vector3.atEntityCorner(Minecraft.getMinecraft().thePlayer);
        for (EntityFXFacingParticle effect : new ArrayList<>(fastRenderParticles)) {
            if (effect == null) {
                fastRenderParticles.remove(null);
                continue;
            }
            effect.tick();
            if (effect.canRemove() || (effect.isDistanceRemovable() && effect.getPosition()
                .distanceSquared(playerPos) >= 256 * 256)) {
                effect.flagAsRemoved();
                fastRenderParticles.remove(effect);
            }
        }

        // Tick gateway particles
        for (EntityFXFacingParticle effect : new ArrayList<>(fastRenderGatewayParticles)) {
            if (effect == null) {
                fastRenderGatewayParticles.remove(null);
                continue;
            }
            effect.tick();
            if (effect.canRemove() || (effect.isDistanceRemovable() && effect.getPosition()
                .distanceSquared(playerPos) >= 256 * 256)) {
                effect.flagAsRemoved();
                fastRenderGatewayParticles.remove(effect);
            }
        }

        acceptsNewParticles = true;

        // Add buffered effects
        List<IComplexEffect> effects = new LinkedList<>(toAddBuffer);
        toAddBuffer.clear();
        for (IComplexEffect eff : effects) {
            registerUnsafe(eff);
        }
    }

    /**
     * Check if particle should be accepted based on settings
     */
    public static boolean mayAcceptParticle(IComplexEffect effect) {
        // TODO: Implement particle amount config setting
        int cfg = 2; // Default to all particles
        if (effect instanceof IComplexEffect.PreventRemoval || cfg == 2) return true;
        return cfg == 1 && STATIC_EFFECT_RAND.nextInt(3) == 0;
    }

    /**
     * Get the current effect count for debugging
     */
    public static int getDebugEffectCount() {
        int count = 0;
        for (Map<Integer, List<IComplexEffect>> effects : complexEffects.values()) {
            for (List<IComplexEffect> eff : effects.values()) {
                count += eff.size();
            }
        }
        count += fastRenderParticles.size();
        count += fastRenderGatewayParticles.size();
        return count;
    }

    /**
     * Initialize the effect handler
     */
    static {
        for (IComplexEffect.RenderTarget target : IComplexEffect.RenderTarget.values()) {
            Map<Integer, List<IComplexEffect>> layeredEffects = new HashMap<>();
            for (int i = 0; i <= 2; i++) {
                layeredEffects.put(i, new LinkedList<>());
            }
            complexEffects.put(target, layeredEffects);
        }
    }

    /**
     * Clean up all effects
     */
    public static void cleanUp() {
        cleanRequested = true;
    }

}
