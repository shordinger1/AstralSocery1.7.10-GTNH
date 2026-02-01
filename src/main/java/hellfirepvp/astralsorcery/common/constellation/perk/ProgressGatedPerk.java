/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Progress gated perk - Perk with progression requirements
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk;

import java.util.Collection;
import java.util.function.BiFunction;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;

/**
 * Progress gated perk - Perk with progression requirements (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Requires research progress to unlock</li>
 * <li>Requires constellation discovery</li>
 * <li>Requires specific progression tier</li>
 * <li>Hidden until requirements met</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>Uses BiFunction with PlayerProgress</li>
 * <li>Integrates with ResearchManager</li>
 * </ul>
 */
public class ProgressGatedPerk extends AbstractPerk {

    private BiFunction<EntityPlayer, PlayerProgress, Boolean> unlockFunction = (player, progress) -> true;

    public ProgressGatedPerk(String name, int x, int y) {
        super(name, x, y);
    }

    /**
     * Require constellation discovery
     */
    public void setRequireDiscoveredConstellation(IConstellation cst) {
        addResearchPreRequisite((player, progress) -> progress.hasConstellationDiscovered(cst));
    }

    /**
     * Require specific research progression
     */
    public void addRequireProgress(ResearchProgression progression) {
        addResearchPreRequisite(
            ((player, progress) -> progress.getResearchProgression()
                .contains(progression)));
    }

    /**
     * Require specific tier
     */
    public void addRequireTier(ProgressionTier tier) {
        addResearchPreRequisite(
            ((player, progress) -> progress.getTierReached()
                .isThisLaterOrEqual(tier)));
    }

    /**
     * Add a research prerequisite
     */
    public void addResearchPreRequisite(BiFunction<EntityPlayer, PlayerProgress, Boolean> unlockFunction) {
        BiFunction<EntityPlayer, PlayerProgress, Boolean> prev = this.unlockFunction;
        this.unlockFunction = (player, progress) -> prev.apply(player, progress)
            && unlockFunction.apply(player, progress);
        disableTooltipCaching(); // Cannot cache as it may change
    }

    @Override
    public boolean mayUnlockPerk(EntityPlayer player) {
        if (!canSee(player, cpw.mods.fml.relauncher.Side.SERVER)) {
            return false;
        }
        return super.mayUnlockPerk(player);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addLocalizedTooltip(Collection<String> tooltip) {
        if (!canSeeClient()) {
            tooltip.add(EnumChatFormatting.RED + StatCollector.translateToLocal("perk.info.missing_progress"));
            return false;
        }
        return super.addLocalizedTooltip(tooltip);
    }

    /**
     * Check if can see on client
     */
    @SideOnly(Side.CLIENT)
    public final boolean canSeeClient() {
        return canSee(net.minecraft.client.Minecraft.getMinecraft().thePlayer, Side.CLIENT);
    }

    /**
     * Check if can see
     */
    public final boolean canSee(EntityPlayer player, Side side) {
        PlayerProgress prog = ResearchManager.getProgress(player, side);
        if (prog.isValid()) {
            return canSee(player, prog);
        }
        return false;
    }

    /**
     * Check if can see with progress
     */
    public final boolean canSee(EntityPlayer player, PlayerProgress progress) {
        return unlockFunction.apply(player, progress);
    }

    @Override
    protected void applyPerkLogic(EntityPlayer player, Side side) {
        // Override in subclasses
    }

    @Override
    protected void removePerkLogic(EntityPlayer player, Side side) {
        // Override in subclasses
    }

}
