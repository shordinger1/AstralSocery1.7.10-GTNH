/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.item;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import hellfirepvp.astralsorcery.common.migration.ItemCameraTransforms;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemRenderRegistry
 * Created by HellFirePvP
 * Date: 23.07.2016 / 16:18
 */
public class ItemRenderRegistry {

    private static Map<ResourceLocation, IItemRenderer> registeredItems = new HashMap<>();
    private static Map<ResourceLocation, ItemCameraTransforms> registeredCameraTransforms = new HashMap<>();

    public static boolean isRegistered(ResourceLocation location) {
        return location != null && registeredItems.containsKey(location);
    }

    public static boolean shouldHandleItemRendering(ItemStack stack) {
        if (stack == null || stack.getItem() == null) return false;
        // 1.7.10: Use Item.itemRegistry.getNameForObject() instead of getRegistryName()
        String regName = Item.itemRegistry.getNameForObject(stack.getItem());
        if (regName == null) return false;
        ResourceLocation entry = getWrappedLocation(new ResourceLocation(regName));
        return isRegistered(entry);
    }

    public static void renderItemStack(ItemStack stack) {
        // 1.7.10: Use Item.itemRegistry.getNameForObject() instead of getRegistryName()
        String regName = Item.itemRegistry.getNameForObject(stack.getItem());
        ResourceLocation loc = new ResourceLocation(regName);
        IItemRenderer renderer = registeredItems.get(getWrappedLocation(loc));
        if (renderer != null) {
            renderer.render(stack);
        }
    }

    // Deprecated. Still works tho. We'll use it until it's removed.
    public static void registerCameraTransforms(Item item, ItemCameraTransforms additionalTransforms) {
        // 1.7.10: Use Item.itemRegistry.getNameForObject() instead of getRegistryName()
        String regName = Item.itemRegistry.getNameForObject(item);
        if (regName != null) {
            registeredCameraTransforms.put(new ResourceLocation(regName), additionalTransforms);
        }
    }

    public static ItemCameraTransforms getAdditionalRenderTransforms(ResourceLocation itemRegistryLocation) {
        return registeredCameraTransforms.get(itemRegistryLocation);
    }

    public static void register(Item item, IItemRenderer renderer) {
        // 1.7.10: Use Item.itemRegistry.getNameForObject() instead of getRegistryName()
        String regName = Item.itemRegistry.getNameForObject(item);
        if (regName != null) {
            ResourceLocation loc = new ResourceLocation(regName);
            registeredItems.put(getWrappedLocation(loc), renderer);
        }

        // 1.7.10: ModelLoader.setCustomMeshDefinition doesn't exist
        // In 1.7.10, IItemRenderer is registered directly through MinecraftForgeClient
        // The dummy mesh definition is not needed
    }

    private static ResourceLocation getWrappedLocation(ResourceLocation regEntry) {
        return new ResourceLocation(regEntry.getResourceDomain(), "models/item/" + regEntry.getResourcePath());
    }
}
