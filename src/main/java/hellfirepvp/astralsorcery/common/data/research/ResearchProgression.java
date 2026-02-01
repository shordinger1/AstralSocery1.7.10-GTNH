/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Research progression - Research progression steps
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.research;

import java.util.*;

/**
 * Research progression - Research progression steps (1.7.10)
 * <p>
 * <b>Progression Steps:</b>
 * <ul>
 * <li>DISCOVERY - Initial discovery</li>
 * <li>BASIC_CRAFT - Basic altar crafting</li>
 * <li>ATTUNEMENT - Constellation attunement</li>
 * <li>CONSTELLATION - Constellation crafting</li>
 * <li>RADIANCE - Trait crafting</li>
 * <li>BRILLIANCE - Brilliance tier</li>
 * </ul>
 */
public enum ResearchProgression {

    DISCOVERY(0, ProgressionTier.DISCOVERY),
    BASIC_CRAFT(1, ProgressionTier.DISCOVERY),
    ATTUNEMENT(2, ProgressionTier.ATTUNEMENT),
    CONSTELLATION(3, ProgressionTier.CONSTELLATION_CRAFT),
    RADIANCE(4, ProgressionTier.TRAIT_CRAFT),
    BRILLIANCE(5, ProgressionTier.BRILLIANCE);

    private final int progressId;
    private final ProgressionTier requiredProgress;

    private static final Map<Integer, ResearchProgression> ID_MAP = new HashMap<>();
    private static final Map<ProgressionTier, List<ResearchProgression>> TIER_MAP = new HashMap<>();

    static {
        for (ResearchProgression prog : values()) {
            ID_MAP.put(prog.progressId, prog);
            TIER_MAP.computeIfAbsent(prog.requiredProgress, k -> new ArrayList<>())
                .add(prog);
        }
    }

    ResearchProgression(int progressId, ProgressionTier requiredProgress) {
        this.progressId = progressId;
        this.requiredProgress = requiredProgress;
    }

    /**
     * Get progression ID
     */
    public int getProgressId() {
        return progressId;
    }

    /**
     * Get required progress tier
     */
    public ProgressionTier getRequiredProgress() {
        return requiredProgress;
    }

    /**
     * Get preconditions (research steps that must be completed first)
     */
    public Collection<ResearchProgression> getPreConditions() {
        List<ResearchProgression> pre = new ArrayList<>();
        for (ResearchProgression prog : values()) {
            if (prog.requiredProgress.ordinal() < this.requiredProgress.ordinal()) {
                pre.add(prog);
            }
        }
        return pre;
    }

    /**
     * Get progression by ID
     */
    public static ResearchProgression getById(int id) {
        return ID_MAP.get(id);
    }

    /**
     * Get all progressions for a tier
     */
    public static List<ResearchProgression> getForTier(ProgressionTier tier) {
        return TIER_MAP.getOrDefault(tier, new ArrayList<>());
    }

    /**
     * Get progression by enum name (for commands)
     *
     * @param name The enum name
     * @return The progression, or null if not found
     */
    public static ResearchProgression getByEnumName(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        try {
            return valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

}
