/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet.server;

import net.minecraft.client.Minecraft;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.block.BlockCustomOre;
import hellfirepvp.astralsorcery.common.constellation.cape.impl.CapeEffectEvorsio;
import hellfirepvp.astralsorcery.common.constellation.effect.aoe.*;
import hellfirepvp.astralsorcery.common.entities.EntityFlare;
import hellfirepvp.astralsorcery.common.entities.EntityItemStardust;
import hellfirepvp.astralsorcery.common.event.listener.EventHandlerEntity;
import hellfirepvp.astralsorcery.common.item.tool.wand.ItemWand;
import hellfirepvp.astralsorcery.common.item.wand.ItemArchitectWand;
import hellfirepvp.astralsorcery.common.potion.PotionCheatDeath;
import hellfirepvp.astralsorcery.common.starlight.network.handlers.BlockTransmutationHandler;
import hellfirepvp.astralsorcery.common.tile.*;
import hellfirepvp.astralsorcery.common.tile.network.TileCollectorCrystal;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.RaytraceAssist;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.effect.CelestialStrike;
import hellfirepvp.astralsorcery.common.util.effect.ShootingStarExplosion;
import hellfirepvp.astralsorcery.common.util.effect.time.TimeStopEffectHelper;
import io.netty.buffer.ByteBuf;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktParticleEvent
 * Created by HellFirePvP
 * Date: 02.08.2016 / 12:15
 */
public class PktParticleEvent implements IMessage, IMessageHandler<PktParticleEvent, IMessage> {

    private int typeOrdinal;
    private double xCoord, yCoord, zCoord;
    private double additionalDataDouble = 0.0D;
    private long additionalDataLong = 0L;

    public PktParticleEvent() {}

    public PktParticleEvent(ParticleEventType type, BlockPos vec) {
        this(type, vec.getX(), vec.getY(), vec.getZ());
    }

    public PktParticleEvent(ParticleEventType type, Vector3 vec) {
        this(type, vec.getX(), vec.getY(), vec.getZ());
    }

    public PktParticleEvent(ParticleEventType type, double x, double y, double z) {
        this.typeOrdinal = type.ordinal();
        this.xCoord = x;
        this.yCoord = y;
        this.zCoord = z;
    }

    public void setAdditionalData(double additionalData) {
        this.additionalDataDouble = additionalData;
    }

    public double getAdditionalData() {
        return additionalDataDouble;
    }

    public void setAdditionalDataLong(long additionalDataLong) {
        this.additionalDataLong = additionalDataLong;
    }

    public long getAdditionalDataLong() {
        return additionalDataLong;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.typeOrdinal = buf.readInt();
        this.xCoord = buf.readDouble();
        this.yCoord = buf.readDouble();
        this.zCoord = buf.readDouble();
        this.additionalDataDouble = buf.readDouble();
        this.additionalDataLong = buf.readLong();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.typeOrdinal);
        buf.writeDouble(this.xCoord);
        buf.writeDouble(this.yCoord);
        buf.writeDouble(this.zCoord);
        buf.writeDouble(this.additionalDataDouble);
        buf.writeLong(this.additionalDataLong);
    }

    @Override
    public IMessage onMessage(PktParticleEvent message, MessageContext ctx) {
        try {
            ParticleEventType type = ParticleEventType.values()[message.typeOrdinal];
            EventAction trigger = type.getTrigger(ctx.side);
            if (trigger != null) {
                AstralSorcery.proxy.scheduleClientside(() -> triggerClientside(trigger, message));
            }
        } catch (Exception exc) {
            AstralSorcery.log.warn(
                "Error executing ParticleEventType " + message.typeOrdinal
                    + " at "
                    + xCoord
                    + ", "
                    + yCoord
                    + ", "
                    + zCoord);
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    private void triggerClientside(EventAction trigger, PktParticleEvent message) {
        if (Minecraft.getMinecraft().theWorld == null) return;
        AstralSorcery.proxy.scheduleClientside(() -> trigger.trigger(message));
    }

    public Vector3 getVec() {
        return new Vector3(xCoord, yCoord, zCoord);
    }

    public static enum ParticleEventType {

        // DEFINE EVENT TRIGGER IN THE FCKING HUGE SWITCH STATEMENT DOWN TEHRE.
        COLLECTOR_BURST,
        GEM_CRYSTAL_BURST,
        CELESTIAL_CRYSTAL_BURST,
        CELESTIAL_CRYSTAL_FORM,
        CRAFT_FINISH_BURST,
        STARMETAL_ORE_CHARGE,
        TRANSMUTATION_CHARGE,
        WELL_CATALYST_BREAK,
        WAND_CRYSTAL_HIGHLIGHT,
        PHOENIX_PROC,
        TREE_VORTEX,
        ARCHITECT_PLACE,
        CEL_STRIKE,
        SH_STAR,
        SH_STAR_EX,
        BURN_PARCHMENT,
        ENGRAVE_LENS,
        GEN_STRUCTURE,
        DISCIDIA_ATTACK_STACK,
        TIME_FREEZE_EFFECT,

        CE_CROP_INTERACT,
        CE_MELT_BLOCK,
        CE_ACCEL_TILE,
        CE_DMG_ENTITY,
        CE_WATER_FISH,
        CE_BREAK_BLOCK,
        CE_SPAWN_PREPARE_EFFECTS,

        CAPE_EVORSIO_BREAK,
        CAPE_EVORSIO_AOE,

        FLARE_PROC,
        RT_DEBUG;

        // GOD I HATE THIS PART
        // But i can't do this in the ctor because server-client stuffs.
        @SideOnly(Side.CLIENT)
        private static EventAction getClientTrigger(ParticleEventType type) {
            switch (type) {
                case COLLECTOR_BURST:
                    return new EventAction() {

                        @Override
                        public void trigger(PktParticleEvent event) {
                            TileCollectorCrystal.breakParticles(event);
                        }
                    };
                case CELESTIAL_CRYSTAL_BURST:
                    return new EventAction() {

                        @Override
                        public void trigger(PktParticleEvent event) {
                            TileCelestialCrystals.breakParticles(event);
                        }
                    };
                case CELESTIAL_CRYSTAL_FORM:
                    return new EventAction() {

                        @Override
                        public void trigger(PktParticleEvent event) {
                            EntityItemStardust.spawnFormationParticles(event);
                        }
                    };
                case CRAFT_FINISH_BURST:
                    return new EventAction() {

                        @Override
                        public void trigger(PktParticleEvent event) {
                            TileAltar.finishBurst(event);
                        }
                    };
                case STARMETAL_ORE_CHARGE:
                    return new EventAction() {

                        @Override
                        public void trigger(PktParticleEvent event) {
                            BlockCustomOre.playStarmetalOreEffects(event);
                        }
                    };
                case TRANSMUTATION_CHARGE:
                    return new EventAction() {

                        @Override
                        public void trigger(PktParticleEvent event) {
                            BlockTransmutationHandler.playTransmutationEffects(event);
                        }
                    };
                case WELL_CATALYST_BREAK:
                    return new EventAction() {

                        @Override
                        public void trigger(PktParticleEvent event) {
                            TileWell.catalystBurst(event);
                        }
                    };
                case WAND_CRYSTAL_HIGHLIGHT:
                    return new EventAction() {

                        @Override
                        public void trigger(PktParticleEvent event) {
                            ItemWand.highlightEffects(event);
                        }
                    };
                case PHOENIX_PROC:
                    return new EventAction() {

                        @Override
                        public void trigger(PktParticleEvent event) {
                            PotionCheatDeath.playEntityDeathEffect(event);
                        }
                    };
                case CE_CROP_INTERACT:
                    return new EventAction() {

                        @Override
                        public void trigger(PktParticleEvent event) {
                            CEffectAevitas.playParticles(event);
                        }
                    };
                case ARCHITECT_PLACE:
                    return new EventAction() {

                        @Override
                        public void trigger(PktParticleEvent event) {
                            ItemArchitectWand.playArchitectPlaceEvent(event);
                        }
                    };
                case CE_MELT_BLOCK:
                    return new EventAction() {

                        @Override
                        public void trigger(PktParticleEvent event) {
                            CEffectFornax.playParticles(event);
                        }
                    };
                case FLARE_PROC:
                    return new EventAction() {

                        @Override
                        public void trigger(PktParticleEvent event) {
                            EntityFlare.playParticles(event);
                        }
                    };
                case CE_ACCEL_TILE:
                    return new EventAction() {

                        @Override
                        public void trigger(PktParticleEvent event) {
                            CEffectHorologium.playParticles(event);
                        }
                    };
                case CE_DMG_ENTITY:
                    return new EventAction() {

                        @Override
                        public void trigger(PktParticleEvent event) {
                            CEffectDiscidia.playParticles(event);
                        }
                    };
                case CE_WATER_FISH:
                    return new EventAction() {

                        @Override
                        public void trigger(PktParticleEvent event) {
                            CEffectOctans.playParticles(event);
                        }
                    };
                case TREE_VORTEX:
                    return new EventAction() {

                        @Override
                        public void trigger(PktParticleEvent event) {
                            TileTreeBeacon.playParticles(event);
                        }
                    };
                case RT_DEBUG:
                    return new EventAction() {

                        @Override
                        public void trigger(PktParticleEvent event) {
                            RaytraceAssist.playDebug(event);
                        }
                    };
                case CEL_STRIKE:
                    return new EventAction() {

                        @Override
                        public void trigger(PktParticleEvent event) {
                            CelestialStrike.playEffects(event);
                        }
                    };
                case SH_STAR:
                    return new EventAction() {

                        @Override
                        public void trigger(PktParticleEvent event) {
                            ShootingStarExplosion.playEffects(event);
                        }
                    };
                case SH_STAR_EX:
                    return new EventAction() {

                        @Override
                        public void trigger(PktParticleEvent event) {
                            ShootingStarExplosion.playExEffects(event);
                        }
                    };
                case BURN_PARCHMENT:
                    return new EventAction() {

                        @Override
                        public void trigger(PktParticleEvent event) {
                            TileMapDrawingTable.burnParchmentEffects(event);
                        }
                    };
                case ENGRAVE_LENS:
                    return new EventAction() {

                        @Override
                        public void trigger(PktParticleEvent event) {
                            TileMapDrawingTable.engraveLensEffects(event);
                        }
                    };
                case GEN_STRUCTURE:
                    return new EventAction() {

                        @Override
                        public void trigger(PktParticleEvent event) {
                            TileOreGenerator.playGenerateStructureEffect(event);
                        }
                    };
                case CE_BREAK_BLOCK:
                    return new EventAction() {

                        @Override
                        public void trigger(PktParticleEvent event) {
                            CEffectEvorsio.playBreakEffects(event);
                        }
                    };
                case CE_SPAWN_PREPARE_EFFECTS:
                    return new EventAction() {

                        @Override
                        public void trigger(PktParticleEvent event) {
                            CEffectPelotrio.playSpawnPrepareEffects(event);
                        }
                    };
                case DISCIDIA_ATTACK_STACK:
                    return new EventAction() {

                        @Override
                        public void trigger(PktParticleEvent event) {
                            EventHandlerEntity.playDiscidiaStackAttackEffects(event);
                        }
                    };
                case CAPE_EVORSIO_BREAK:
                    return new EventAction() {

                        @Override
                        public void trigger(PktParticleEvent event) {
                            CapeEffectEvorsio.playBlockBreakParticles(event);
                        }
                    };
                case CAPE_EVORSIO_AOE:
                    return new EventAction() {

                        @Override
                        public void trigger(PktParticleEvent event) {
                            CapeEffectEvorsio.playAreaDamageParticles(event);
                        }
                    };
                case TIME_FREEZE_EFFECT:
                    return new EventAction() {

                        @Override
                        public void trigger(PktParticleEvent event) {
                            TimeStopEffectHelper.playEntityParticles(event);
                        }
                    };
                case GEM_CRYSTAL_BURST:
                    return new EventAction() {

                        @Override
                        public void trigger(PktParticleEvent event) {
                            TileGemCrystals.playBreakParticles(event);
                        }
                    };
                default:
                    break;
            }
            return null;
        }

        public EventAction getTrigger(Side side) {
            if (!side.isClient()) return null;
            return getClientTrigger(this);
        }

    }

    private static interface EventAction {

        public void trigger(PktParticleEvent event);

    }

}
