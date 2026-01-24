/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.key;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import cpw.mods.fml.relauncher.Side;
import hellfirepvp.astralsorcery.common.base.OreTypes;
import hellfirepvp.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.KeyPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.types.IPlayerTickPerk;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.data.config.entry.ConfigEntry;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.BlockStateCheck;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.WrapMathHelper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyStoneEnrichment
 * Created by HellFirePvP
 * Date: 17.07.2018 / 20:10
 */
public class KeyStoneEnrichment extends KeyPerk implements IPlayerTickPerk {

    private static final BlockStateCheck stoneCheck = new CleanStoneCheck();

    private int enrichmentRadius = 3;
    private int chanceToEnrich = 70;

    public KeyStoneEnrichment(String name, int x, int y) {
        super(name, x, y);
        Config.addDynamicEntry(new ConfigEntry(ConfigEntry.Section.PERKS, name) {

            @Override
            public void loadFromConfig(Configuration cfg) {
                enrichmentRadius = cfg.getInt(
                    "Effect_Radius",
                    getConfigurationSection(),
                    enrichmentRadius,
                    1,
                    35,
                    "Defines the radius where a random position to generate a ore at is searched");
                chanceToEnrich = cfg.getInt(
                    "Chance_To_CreateOre",
                    getConfigurationSection(),
                    chanceToEnrich,
                    2,
                    4_000_000,
                    "Sets the chance (Random.nextInt(chance) == 0) to try to see if a random stone next to the player should get turned into an ore; the lower the more likely");
            }
        });
    }

    @Override
    protected void applyEffectMultiplier(double multiplier) {
        super.applyEffectMultiplier(multiplier);

        this.enrichmentRadius = WrapMathHelper.ceil(this.enrichmentRadius * multiplier);
        this.chanceToEnrich = WrapMathHelper.ceil(this.chanceToEnrich * multiplier);
    }

    @Override
    public void onPlayerTick(EntityPlayer player, Side side) {
        if (side == Side.SERVER) {
            PlayerProgress prog = ResearchManager.getProgress(player, side);
            float modChance = PerkAttributeHelper.getOrCreateMap(player, side)
                .modifyValue(player, prog, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT, chanceToEnrich);
            if (rand.nextInt(Math.round(Math.max(modChance, 1))) == 0) {
                float enrRad = PerkAttributeHelper.getOrCreateMap(player, side)
                    .modifyValue(player, prog, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT, enrichmentRadius);
                Vector3 vec = Vector3.atEntityCenter(player)
                    .add(
                        (rand.nextFloat() * enrRad * 2) - enrRad,
                        (rand.nextFloat() * enrRad * 2) - enrRad,
                        (rand.nextFloat() * enrRad * 2) - enrRad);
                BlockPos pos = vec.toBlockPos();
                if (stoneCheck.isStateValid(player.worldObj.getBlock(pos.getX(), pos.getY(), pos.getZ()))) {
                    ItemStack blockStack = OreTypes.AEVITAS_ORE_PERK.getRandomOre(rand);
                    if (!(blockStack == null || blockStack.stackSize <= 0)) {
                        Block state = ItemUtils.createBlockState(blockStack);
                        if (state != null) {
                            player.worldObj.setBlock(pos.getX(), pos.getY(), pos.getZ(), state, 0, 3);
                        }
                    }
                }
            }
        }
    }

    private static class CleanStoneCheck implements BlockStateCheck {

        @Override
        public boolean isStateValid(World world, BlockPos pos, net.minecraft.block.Block block, int metadata) {
            // 1.7.10: BlockStone doesn't have VARIANT property. Just check if it's stone with metadata 0
            return block == Blocks.stone && metadata == 0;
        }

        @Override
        public boolean isStateValid(net.minecraft.block.Block state) {
            // 1.7.10: Simple check for regular stone
            return state == Blocks.stone;
        }

    }

}
