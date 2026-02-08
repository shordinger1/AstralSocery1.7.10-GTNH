/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Constellation Paper Item - Displays constellation information
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.base.AstralBaseItem;
import hellfirepvp.astralsorcery.common.util.LocalizationHelper;

/**
 * Constellation Paper Item
 * <p>
 * Displays constellation information.
 * Can show discovered or unknown constellations.
 * <p>
 * Features:
 * - NBT-based constellation storage
 * - Dynamic color based on discovery status
 * - Random constellation generation on craft
 * - Entity highlighting
 * <p>
 * TODO:
 * - Implement constellation NBT reading/writing
 * - Implement random constellation generation
 * - Link with constellation system
 * - Implement ItemHighlighted interface
 * - Add dynamic color system
 */
public class ItemConstellationPaper extends AstralBaseItem {

    private static final String TAG_CONSTELLATION = "constellation";

    public ItemConstellationPaper() {
        super(64); // Max stack size 64
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            // Generate random constellation on creation
            java.util.List<hellfirepvp.astralsorcery.common.constellation.IConstellation> constellations = hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry
                .getAllConstellations();

            if (!constellations.isEmpty()) {
                java.util.Random rand = new java.util.Random();
                hellfirepvp.astralsorcery.common.constellation.IConstellation constellation = constellations
                    .get(rand.nextInt(constellations.size()));
                setConstellation(stack, constellation.getUnlocalizedName());
            }
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (world.isRemote) {
            return stack;
        }

        // Server-side: use paper to research constellation
        String constellationName = getConstellation(stack);
        if (constellationName == null) {
            if (player != null) {
                player.addChatMessage(new net.minecraft.util.ChatComponentText("§cThis paper has no constellation!"));
            }
            return stack;
        }

        hellfirepvp.astralsorcery.common.constellation.IConstellation constellation = hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry
            .getConstellationByName(constellationName);

        if (constellation == null) {
            if (player != null) {
                player.addChatMessage(
                    new net.minecraft.util.ChatComponentText("§cInvalid constellation: " + constellationName));
            }
            return stack;
        }

        // Check if player has discovered this constellation
        hellfirepvp.astralsorcery.common.data.research.PlayerProgress progress = hellfirepvp.astralsorcery.common.data.research.PlayerProgressProperties
            .getProgress(player);

        if (!progress.hasConstellationDiscovered(constellation)) {
            player.addChatMessage(
                new net.minecraft.util.ChatComponentText("§cYou haven't discovered this constellation yet!"));
            player.addChatMessage(
                new net.minecraft.util.ChatComponentText("§eUse a §6Telescope §eat night to discover constellations."));
            return stack;
        }

        // Check if player can attune (needs attuned constellation)
        if (progress.getAttunedConstellation() == null) {
            player.addChatMessage(
                new net.minecraft.util.ChatComponentText("§cYou need to attune to a constellation first!"));
            player.addChatMessage(new net.minecraft.util.ChatComponentText("§eUse an §6Attunement Altar §eto attune."));
            return stack;
        }

        // Try to use paper for research
        // For now, just consume the paper and give a small hint
        player.addChatMessage(new net.minecraft.util.ChatComponentText("§eYou study the constellation paper..."));
        player.addChatMessage(new net.minecraft.util.ChatComponentText("§7Constellation: §6" + constellationName));

        // Consume the paper
        stack.stackSize--;
        if (stack.stackSize <= 0 && player != null) {
            player.destroyCurrentEquippedItem();
        }

        return stack;
    }

    /**
     * Get constellation from NBT
     */
    public String getConstellation(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null && nbt.hasKey(TAG_CONSTELLATION)) {
            return nbt.getString(TAG_CONSTELLATION);
        }
        return null;
    }

    /**
     * Set constellation in NBT
     */
    public void setConstellation(ItemStack stack, String constellation) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }
        nbt.setString(TAG_CONSTELLATION, constellation);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        // TODO: Return different rarity based on constellation
        return EnumRarity.uncommon;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        String constellation = getConstellation(stack);
        if (constellation != null) {
            // Try to get localized constellation name
            String constelKey = "astralsorcery.constellation." + constellation;
            String constelName = LocalizationHelper.tr(constelKey);
            // If translation exists (not equal to key), show it
            if (!constelName.equals(constelKey)) {
                tooltip.add("§b" + constelName);
            } else {
                tooltip.add("§e" + constellation);
            }
            // Show discovered/unknown status when constellation system is implemented
            // boolean discovered = isDiscovered(player, constellation);
            // String status = discovered ? "§a" + tr("item.itemconstellationpaper.discovered") : "§c" +
            // tr("item.itemconstellationpaper.unknown");
            // tooltip.add("§7" + tr("item.itemconstellationpaper.status") + ": " + status);
        } else {
            tooltip.add("§c" + LocalizationHelper.tr("item.itemconstellationpaper.noConstellation"));
            tooltip.add("§8" + LocalizationHelper.tr("item.itemconstellationpaper.usage"));
        }

        // Add general tooltip
        LocalizationHelper.addItemTooltip(stack, tooltip, 2, false);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect(ItemStack stack) {
        // TODO: Return true if constellation is discovered
        return false;
    }

    // TODO: Implement ItemHighlighted for entity glow effect
    // TODO: Implement dynamic color based on discovery
}
