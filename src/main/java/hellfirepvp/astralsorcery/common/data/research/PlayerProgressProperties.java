/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * PlayerProgressProperties - Extended Entity Properties for PlayerProgress
 *
 * 1.7.10: Uses ExtendedEntityProperties system to persist player progress
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.research;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

/**
 * Extended Entity Properties for persisting player progress
 *
 * <p>
 * This class attaches to EntityPlayer and persists Astral Sorcery progress
 * across world saves using the 1.7.10 ExtendedEntityProperties system.
 * </p>
 *
 * <p>
 * <b>1.7.10 Implementation:</b>
 * </p>
 * Uses IExtendedEntityProperties interface instead of the Capability system
 * used in 1.12.2+. Extended properties are automatically saved/loaded by Minecraft
 * and persist across respawns and dimension changes.
 * </p>
 *
 * <p>
 * <b>Usage:</b>
 * </p>
 *
 * <pre>
 *
 * // Get player progress
 * PlayerProgress progress = PlayerProgressProperties.getProgress(player);
 * </pre>
 */
public class PlayerProgressProperties implements IExtendedEntityProperties {

    /**
     * The property key for registration
     */
    public static final String PROPERTY_KEY = "AstralSorceryPlayerProgress";

    /**
     * The actual player progress data
     */
    private final PlayerProgress progress;

    /**
     * Create a new PlayerProgressProperties
     */
    public PlayerProgressProperties() {
        this.progress = new PlayerProgress();
    }

    /**
     * Get the player progress
     *
     * @return The progress
     */
    public PlayerProgress getProgress() {
        return progress;
    }

    @Override
    public void saveNBTData(NBTTagCompound compound) {
        // Save progress to a nested tag
        NBTTagCompound progressTag = new NBTTagCompound();
        progress.store(progressTag);
        compound.setTag(PROPERTY_KEY, progressTag);
    }

    @Override
    public void loadNBTData(NBTTagCompound compound) {
        // Load progress from nested tag
        if (compound.hasKey(PROPERTY_KEY)) {
            NBTTagCompound progressTag = compound.getCompoundTag(PROPERTY_KEY);
            progress.load(progressTag);
        }
    }

    @Override
    public void init(Entity entity, World world) {
        // Initialization handled in constructor
    }

    /**
     * Register this property for a player
     *
     * @param player The player
     * @return The properties instance
     */
    public static PlayerProgressProperties register(EntityPlayer player) {
        PlayerProgressProperties props = (PlayerProgressProperties) player.getExtendedProperties(PROPERTY_KEY);

        if (props == null) {
            props = new PlayerProgressProperties();
            player.registerExtendedProperties(PROPERTY_KEY, props);
        }

        return props;
    }

    /**
     * Get player progress from player
     *
     * @param player The player
     * @return The player progress
     */
    public static PlayerProgress getProgress(EntityPlayer player) {
        PlayerProgressProperties props = (PlayerProgressProperties) player.getExtendedProperties(PROPERTY_KEY);

        if (props == null) {
            props = register(player);
        }

        return props.getProgress();
    }
}
