package hellfirepvp.astralsorcery.common.migration;

import net.minecraft.util.ResourceLocation;

/**
 * Compatibility class for SoundEvent (introduced in Minecraft 1.9+).
 * In 1.7.10, sounds are handled differently, using String resource names directly.
 */
public class SoundEvent {

    private final ResourceLocation soundName;

    public SoundEvent(ResourceLocation soundName) {
        this.soundName = soundName;
    }

    public SoundEvent(String soundName) {
        this(new ResourceLocation(soundName));
    }

    public ResourceLocation getSoundName() {
        return this.soundName;
    }

    public String getSoundNameString() {
        return this.soundName.toString();
    }
}
