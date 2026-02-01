/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Player progress - Player research and perk progression data
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.research;

import java.util.*;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.constellation.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.PerkLevelManager;

/**
 * Player progress - Player research and perk progression data (1.7.10)
 * <p>
 * <b>Features:</b>
 * <ul>
 * <li>Constellation discovery tracking</li>
 * <li>Perk application and sealing</li>
 * <li>Progression tier management</li>
 * <li>Perk XP and level tracking</li>
 * </ul>
 * <p>
 * <b>1.7.10 API Notes:</b>
 * <ul>
 * <li>Simplified without network sync (for now)</li>
 * <li>No sextant target system</li>
 * <li>No research IO thread (direct save/load)</li>
 * </ul>
 */
public class PlayerProgress {

    private List<String> knownConstellations = new ArrayList<>();
    private List<String> seenConstellations = new ArrayList<>();
    private IMajorConstellation attunedConstellation = null;
    private boolean wasOnceAttuned = false;
    private List<ResearchProgression> researchProgression = new LinkedList<>();
    private ProgressionTier tierReached = ProgressionTier.DISCOVERY;
    private List<String> freePointTokens = new ArrayList<>();
    private Set<AbstractPerk> appliedPerks = new HashSet<>();
    private Map<AbstractPerk, NBTTagCompound> appliedPerkData = new HashMap<>();
    private List<AbstractPerk> sealedPerks = new ArrayList<>();
    private double perkExp = 0;
    private boolean tomeReceived = false;

    /**
     * Load progress from NBT
     */
    public void load(NBTTagCompound compound) {
        knownConstellations.clear();
        seenConstellations.clear();
        researchProgression.clear();
        attunedConstellation = null;
        tierReached = ProgressionTier.DISCOVERY;
        wasOnceAttuned = false;
        appliedPerks.clear();
        appliedPerkData.clear();
        sealedPerks.clear();
        freePointTokens.clear();
        perkExp = 0;
        tomeReceived = false;

        if (compound.hasKey("seenConstellations")) {
            NBTTagList list = compound.getTagList("seenConstellations", 8);
            for (int i = 0; i < list.tagCount(); i++) {
                seenConstellations.add(list.getStringTagAt(i));
            }
        }
        if (compound.hasKey("constellations")) {
            NBTTagList list = compound.getTagList("constellations", 8);
            for (int i = 0; i < list.tagCount(); i++) {
                String s = list.getStringTagAt(i);
                knownConstellations.add(s);
                if (!seenConstellations.contains(s)) {
                    seenConstellations.add(s);
                }
            }
        }

        // Load attuned constellation
        if (compound.hasKey("attuned")) {
            String cst = compound.getString("attuned");
            IConstellation c = ConstellationRegistry.getConstellationByName(cst);
            if (c != null && c instanceof IMajorConstellation) {
                attunedConstellation = (IMajorConstellation) c;
            }
        }

        // Load perks
        int perkTreeLevel = compound.getInteger("perkTreeVersion");
        if (perkTreeLevel < 1) { // Outdated perk tree version
            if (attunedConstellation != null) {
                // Outdated version - reset perks
                appliedPerks.clear();
                appliedPerkData.clear();
                sealedPerks.clear();
            }
        } else {
            if (compound.hasKey("perks")) {
                NBTTagList list = compound.getTagList("perks", 10);
                for (int i = 0; i < list.tagCount(); i++) {
                    NBTTagCompound tag = list.getCompoundTagAt(i);
                    String perkRegName = tag.getString("perkName");
                    NBTTagCompound data = (NBTTagCompound) tag.getTag("perkData");
                    AbstractPerk perk = hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTree.PERK_TREE
                        .getPerk(perkRegName);
                    if (perk != null) {
                        appliedPerks.add(perk);
                        appliedPerkData.put(perk, data);
                    }
                }
            }
            if (compound.hasKey("sealedPerks")) {
                NBTTagList list = compound.getTagList("sealedPerks", 10);
                for (int i = 0; i < list.tagCount(); i++) {
                    NBTTagCompound tag = list.getCompoundTagAt(i);
                    String perkRegName = tag.getString("perkName");
                    AbstractPerk perk = hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTree.PERK_TREE
                        .getPerk(perkRegName);
                    if (perk != null) {
                        sealedPerks.add(perk);
                    }
                }
            }

            if (compound.hasKey("pointTokens")) {
                NBTTagList list = compound.getTagList("pointTokens", 8);
                for (int i = 0; i < list.tagCount(); i++) {
                    this.freePointTokens.add(list.getStringTagAt(i));
                }
            }
        }

        if (compound.hasKey("tierReached")) {
            int tierOrdinal = compound.getInteger("tierReached");
            ProgressionTier[] tiers = ProgressionTier.values();
            tierReached = tiers[Math.min(tierOrdinal, tiers.length - 1)];
        }

        if (compound.hasKey("research")) {
            int[] research = compound.getIntArray("research");
            for (int resOrdinal : research) {
                ResearchProgression prog = ResearchProgression.getById(resOrdinal);
                if (prog != null) {
                    researchProgression.add(prog);
                }
            }
        }

        this.wasOnceAttuned = compound.getBoolean("wasAttuned");

        if (compound.hasKey("perkExp")) {
            this.perkExp = compound.getDouble("perkExp");
        }

        if (!compound.hasKey("bookReceived")) {
            this.tomeReceived = true; // Legacy support
        } else {
            this.tomeReceived = compound.getBoolean("bookReceived");
        }
    }

    /**
     * Store progress to NBT
     */
    public void store(NBTTagCompound cmp) {
        NBTTagList list = new NBTTagList();
        for (String s : knownConstellations) {
            list.appendTag(new NBTTagString(s));
        }
        NBTTagList l = new NBTTagList();
        for (String s : seenConstellations) {
            l.appendTag(new NBTTagString(s));
        }
        cmp.setTag("constellations", list);
        cmp.setTag("seenConstellations", l);
        cmp.setInteger("tierReached", tierReached.ordinal());
        cmp.setBoolean("wasAttuned", wasOnceAttuned);

        NBTTagList listTokens = new NBTTagList();
        for (String s : freePointTokens) {
            listTokens.appendTag(new NBTTagString(s));
        }
        cmp.setTag("pointTokens", listTokens);

        int[] researchArray = new int[researchProgression.size()];
        for (int i = 0; i < researchProgression.size(); i++) {
            ResearchProgression progression = researchProgression.get(i);
            researchArray[i] = progression.getProgressId();
        }
        cmp.setIntArray("research", researchArray);

        if (attunedConstellation != null) {
            cmp.setString("attuned", attunedConstellation.getUnlocalizedName());
        }

        list = new NBTTagList();
        for (Map.Entry<AbstractPerk, NBTTagCompound> entry : appliedPerkData.entrySet()) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString(
                "perkName",
                entry.getKey()
                    .getRegistryName());
            tag.setTag("perkData", entry.getValue());
            list.appendTag(tag);
        }
        cmp.setTag("perks", list);

        list = new NBTTagList();
        for (AbstractPerk perk : sealedPerks) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("perkName", perk.getRegistryName());
            list.appendTag(tag);
        }
        cmp.setTag("sealedPerks", list);
        cmp.setInteger("perkTreeVersion", 1);
        cmp.setDouble("perkExp", perkExp);
        cmp.setBoolean("bookReceived", tomeReceived);
    }

    public boolean isValid() {
        return true;
    }

    protected boolean forceGainResearch(ResearchProgression progression) {
        if (!researchProgression.contains(progression)) {
            researchProgression.add(progression);
            return true;
        }
        return false;
    }

    protected void setAttunedConstellation(IMajorConstellation constellation) {
        this.attunedConstellation = constellation;
        this.wasOnceAttuned = true;
    }

    public Collection<AbstractPerk> getAppliedPerks() {
        return appliedPerks == null ? new ArrayList<>() : Collections.unmodifiableCollection(appliedPerks);
    }

    public List<AbstractPerk> getSealedPerks() {
        return sealedPerks == null ? new ArrayList<>() : Collections.unmodifiableList(sealedPerks);
    }

    public Map<AbstractPerk, NBTTagCompound> getUnlockedPerkData() {
        return appliedPerkData == null ? new HashMap<>() : Collections.unmodifiableMap(appliedPerkData);
    }

    public NBTTagCompound getPerkData(AbstractPerk perk) {
        NBTTagCompound tag = appliedPerkData.get(perk);
        return tag == null ? null : (NBTTagCompound) tag.copy();
    }

    public boolean hasPerkUnlocked(AbstractPerk perk) {
        return appliedPerks.contains(perk);
    }

    public boolean isPerkSealed(AbstractPerk perk) {
        return sealedPerks.contains(perk);
    }

    public void applyPerk(AbstractPerk perk, NBTTagCompound data) {
        this.appliedPerks.add(perk);
        this.appliedPerkData.put(perk, data);
    }

    boolean removePerk(AbstractPerk perk) {
        return appliedPerks.remove(perk) && (!sealedPerks.contains(perk) || sealedPerks.remove(perk));
    }

    boolean removePerkData(AbstractPerk perk) {
        return appliedPerkData.remove(perk) != null;
    }

    protected boolean sealPerk(AbstractPerk perk) {
        if (sealedPerks.contains(perk) || !hasPerkUnlocked(perk)) {
            return false;
        }
        return sealedPerks.add(perk);
    }

    protected boolean breakSeal(AbstractPerk perk) {
        if (!sealedPerks.contains(perk) || !hasPerkUnlocked(perk)) {
            return false;
        }
        return sealedPerks.remove(perk);
    }

    public List<ResearchProgression> getResearchProgression() {
        researchProgression.removeIf(Objects::isNull);
        return new LinkedList<>(researchProgression);
    }

    public ProgressionTier getTierReached() {
        return tierReached;
    }

    public IMajorConstellation getAttunedConstellation() {
        return attunedConstellation;
    }

    public boolean wasOnceAttuned() {
        return wasOnceAttuned;
    }

    public boolean didReceiveTome() {
        return tomeReceived;
    }

    protected void setTomeReceived() {
        this.tomeReceived = true;
    }

    protected boolean grantFreeAllocationPoint(String freePointToken) {
        if (this.freePointTokens.contains(freePointToken)) {
            return false;
        }
        this.freePointTokens.add(freePointToken);
        return true;
    }

    protected boolean tryRevokeAllocationPoint(String token) {
        return this.freePointTokens.remove(token);
    }

    public List<String> getFreePointTokens() {
        return Collections.unmodifiableList(freePointTokens);
    }

    public int getAvailablePerkPoints(EntityPlayer player) {
        int allocatedPerks = this.appliedPerks.size() - 1; // Root perk doesn't count
        int allocationLevels = PerkLevelManager.INSTANCE.getLevel(getPerkExp(), player);
        return (allocationLevels + this.freePointTokens.size()) - allocatedPerks;
    }

    public boolean hasFreeAllocationPoint(EntityPlayer player) {
        return getAvailablePerkPoints(player) > 0;
    }

    public double getPerkExp() {
        return perkExp;
    }

    public int getPerkLevel(EntityPlayer player) {
        return PerkLevelManager.INSTANCE.getLevel(getPerkExp(), player);
    }

    public float getPercentToNextLevel(EntityPlayer player) {
        return PerkLevelManager.INSTANCE.getNextLevelPercent(getPerkExp(), player);
    }

    protected void modifyExp(double exp, EntityPlayer player) {
        int currLevel = PerkLevelManager.INSTANCE.getLevel(getPerkExp(), player);
        if (exp >= 0 && currLevel >= PerkLevelManager.getLevelCap()) {
            return;
        }

        this.perkExp = Math.max(this.perkExp + exp, 0);
    }

    protected void setExp(double exp) {
        this.perkExp = Math.max(exp, 0);
    }

    protected boolean stepTier() {
        if (getTierReached().hasNextTier()) {
            setTierReached(ProgressionTier.values()[getTierReached().ordinal() + 1]);
            return true;
        }
        return false;
    }

    protected void setTierReached(ProgressionTier tier) {
        this.tierReached = tier;
    }

    public List<String> getKnownConstellations() {
        return knownConstellations;
    }

    public List<String> getSeenConstellations() {
        return seenConstellations;
    }

    public boolean hasConstellationDiscovered(IConstellation constellation) {
        return hasConstellationDiscovered(constellation.getUnlocalizedName());
    }

    public boolean hasConstellationDiscovered(String constellation) {
        return knownConstellations.contains(constellation);
    }

    protected void discoverConstellation(String name) {
        memorizeConstellation(name);
        if (!knownConstellations.contains(name)) {
            knownConstellations.add(name);
        }
    }

    protected void memorizeConstellation(String name) {
        if (!seenConstellations.contains(name)) {
            seenConstellations.add(name);
        }
    }

}
