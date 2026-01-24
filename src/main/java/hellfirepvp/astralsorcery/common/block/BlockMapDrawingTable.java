/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import java.awt.*;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBook;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.item.ItemCraftingComponent;
import hellfirepvp.astralsorcery.common.item.ItemInfusedGlass;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.tile.TileMapDrawingTable;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockMapDrawingTable
 * Created by HellFirePvP
 * Date: 18.03.2017 / 17:32
 */
public class BlockMapDrawingTable extends BlockContainer {

    private static final AxisAlignedBB drawingTableBox = AxisAlignedBB
        .getBoundingBox(-6.0 / 16.0, 0, -4.0 / 16.0, 22.0 / 16.0, 24.0 / 16.0, 20.0 / 16.0);

    public BlockMapDrawingTable() {
        super(Material.rock);
        setHardness(2F);
        setStepSound(Block.soundTypeWood);
        setResistance(15F);
        setHarvestLevel("axe", 1);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        for (int i = 0; i < rand.nextInt(2) + 2; i++) {
            Vector3 offset = new Vector3(-5.0 / 16.0, 1.505, -3.0 / 16.0);
            int random = rand.nextInt(12);
            if (random > 5) {
                offset.addX(24.0 / 16.0);
            }
            offset.addZ((random % 6) * (4.0 / 16.0));
            offset.add(rand.nextFloat() * 0.1, 0, rand.nextFloat() * 0.1)
                .add(x, y, z);
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(offset.getX(), offset.getY(), offset.getZ());
            p.scale(rand.nextFloat() * 0.1F + 0.15F)
                .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
            p.gravity(0.004F)
                .setMaxAge(rand.nextInt(30) + 35);
            switch (random) {
                case 0:
                    p.setColor(new Color(0xFF0800));
                    break;
                case 1:
                    p.setColor(new Color(0xFFCC00));
                    break;
                case 2:
                    p.setColor(new Color(0x6FFF00));
                    break;
                case 3:
                    p.setColor(new Color(0x00FCFF));
                    break;
                case 4:
                    p.setColor(new Color(0x0028FF));
                    break;
                case 5:
                    p.setColor(new Color(0xFF00FE));
                    break;
                case 6:
                    p.setColor(new Color(0xF07800));
                    break;
                case 7:
                    p.setColor(new Color(0xB4F000));
                    break;
                case 8:
                    p.setColor(new Color(0x01F000));
                    break;
                case 9:
                    p.setColor(new Color(0x007AF0));
                    break;
                case 10:
                    p.setColor(new Color(0x3900F0));
                    break;
                case 11:
                    p.setColor(new Color(0xf0007B));
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer playerIn, int side, float hitX,
        float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            ItemStack held = playerIn.getCurrentEquippedItem();
            TileMapDrawingTable tm = MiscUtils.getTileAt(worldIn, x, y, z, TileMapDrawingTable.class);
            if (tm != null) {
                if (playerIn.isSneaking()) {
                    ItemStack slotIn = tm.getSlotIn();
                    if (slotIn != null && slotIn.stackSize > 0) {
                        playerIn.inventory.addItemStackToInventory(slotIn);
                        tm.putSlotIn(null);
                        return true;
                    }
                    ItemStack slotGlass = tm.getSlotGlassLens();
                    if (slotGlass != null && slotGlass.stackSize > 0) {
                        playerIn.inventory.addItemStackToInventory(slotGlass);
                        tm.putGlassLens(null);
                        return true;
                    }
                } else {
                    if (!(held == null || held.stackSize <= 0)) {
                        if (held.getItem() instanceof ItemCraftingComponent) {
                            if (held.getItemDamage() == ItemCraftingComponent.MetaType.PARCHMENT.getMeta()) {
                                int remaining = tm.addParchment(held.stackSize);
                                if (remaining < held.stackSize) {
                                    worldIn.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "random.flip", 1F, 1F);
                                    if (!playerIn.capabilities.isCreativeMode) {
                                        held.stackSize = remaining;
                                        if (held.stackSize <= 0) {
                                            playerIn.inventory.mainInventory[playerIn.inventory.currentItem] = null;
                                        } else {
                                            playerIn.inventory.mainInventory[playerIn.inventory.currentItem] = held;
                                        }
                                    }
                                }
                            }
                        } else if (held.getItem() instanceof ItemInfusedGlass) {
                            ItemStack slotGlass = tm.getSlotGlassLens();
                            if (slotGlass == null || slotGlass.stackSize <= 0) {
                                tm.putGlassLens(held);
                                if (!playerIn.capabilities.isCreativeMode) {
                                    held.stackSize -= 1;
                                    if (held.stackSize <= 0) {
                                        playerIn.inventory.mainInventory[playerIn.inventory.currentItem] = null;
                                    } else {
                                        playerIn.inventory.mainInventory[playerIn.inventory.currentItem] = held;
                                    }
                                }
                            }
                        } else if ((held.getItem() instanceof ItemBook || held.isItemEnchantable())) {
                            ItemStack slotIn = tm.getSlotIn();
                            if (slotIn == null || slotIn.stackSize <= 0) {
                                tm.putSlotIn(ItemUtils.copyStackWithSize(held, 1));
                                if (!playerIn.capabilities.isCreativeMode) {
                                    held.stackSize -= 1;
                                    if (held.stackSize <= 0) {
                                        playerIn.inventory.mainInventory[playerIn.inventory.currentItem] = null;
                                    } else {
                                        playerIn.inventory.mainInventory[playerIn.inventory.currentItem] = held;
                                    }
                                }
                            }
                        } else if (held.getItem() instanceof ItemPotion && held.getItemDamage() == 0) {
                            ItemStack slotIn = tm.getSlotIn();
                            if (slotIn == null || slotIn.stackSize <= 0) {
                                tm.putSlotIn(held);
                                if (!playerIn.capabilities.isCreativeMode) {
                                    held.stackSize -= 1;
                                    if (held.stackSize <= 0) {
                                        playerIn.inventory.mainInventory[playerIn.inventory.currentItem] = null;
                                    } else {
                                        playerIn.inventory.mainInventory[playerIn.inventory.currentItem] = held;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            if (!playerIn.isSneaking()) {
                ItemStack held = playerIn.getCurrentEquippedItem();
                if (!(held == null || held.stackSize <= 0)) {
                    if ((held.getItem() instanceof ItemCraftingComponent
                        && (held.getItemDamage() == ItemCraftingComponent.MetaType.PARCHMENT.getMeta()))
                        || held.getItem() instanceof ItemInfusedGlass
                        || held.isItemEnchantable()
                        || held.getItem() instanceof ItemBook
                        || (held.getItem() instanceof ItemPotion && held.getItemDamage() == 0)) {
                        return true;
                    }
                }
                AstralSorcery.proxy.openGui(CommonProxy.EnumGuiId.MAP_DRAWING, playerIn, worldIn, x, y, z);
            }
        }
        return true;
    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block block, int meta) {
        TileMapDrawingTable tm = MiscUtils.getTileAt(worldIn, x, y, z, TileMapDrawingTable.class);
        if (tm != null) {
            tm.dropContents();
        }

        super.breakBlock(worldIn, x, y, z, block, meta);
    }

    public AxisAlignedBB getBoundingBox(int x, int y, int z) {
        return drawingTableBox;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileMapDrawingTable();
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileMapDrawingTable();
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

}
