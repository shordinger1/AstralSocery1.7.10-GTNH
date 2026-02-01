/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * Resource helper utilities
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import hellfirepvp.astralsorcery.common.lib.Constants;

/**
 * Resource helper utilities for Astral Sorcery
 *
 * Provides convenient methods for resource location strings.
 */
public class ResourceHelper {

    /**
     * Get a resource location for a block
     *
     * @param name The block name
     * @return The full resource location (e.g., "astralsorcery:block_name")
     */
    public static String block(String name) {
        return Constants.RESOURCE_ROOT + name;
    }

    /**
     * Get a resource location for an item
     *
     * @param name The item name
     * @return The full resource location (e.g., "astralsorcery:item_name")
     */
    public static String item(String name) {
        return Constants.RESOURCE_ROOT + name;
    }

    /**
     * Get a resource location for an entity
     *
     * @param name The entity name
     * @return The full resource location (e.g., "astralsorcery:entity_name")
     */
    public static String entity(String name) {
        return Constants.RESOURCE_ROOT + name;
    }

    /**
     * Get a resource location for a texture
     *
     * @param type The texture type (e.g., "blocks", "items")
     * @param name The texture name
     * @return The full texture path (e.g., "astralsorcery:blocks/texture_name")
     */
    public static String texture(String type, String name) {
        return Constants.RESOURCE_ROOT + type + "/" + name;
    }

    /**
     * Get a resource location for a block texture
     *
     * @param name The texture name
     * @return The full texture path (e.g., "astralsorcery:blocks/texture_name")
     */
    public static String blockTexture(String name) {
        return texture("blocks", name);
    }

    /**
     * Get a resource location for an item texture
     *
     * @param name The texture name
     * @return The full texture path (e.g., "astralsorcery:items/texture_name")
     */
    public static String itemTexture(String name) {
        return texture("items", name);
    }

    /**
     * Get a localization key for an item
     *
     * @param name The item name
     * @return The localization key (e.g., "item.astralsorcery:item_name.name")
     */
    public static String itemLang(String name) {
        return "item." + item(name) + ".name";
    }

    /**
     * Get a localization key for a block
     *
     * @param name The block name
     * @return The localization key (e.g., "tile.astralsorcery:block_name.name")
     */
    public static String blockLang(String name) {
        return "tile." + block(name) + ".name";
    }

    /**
     * Get a localization key for an entity
     *
     * @param name The entity name
     * @return The localization key (e.g., "entity.astralsorcery:entity_name.name")
     */
    public static String entityLang(String name) {
        return "entity." + entity(name) + ".name";
    }

    /**
     * Get a resource location for a GUI
     *
     * @param name The GUI name
     * @return The full resource location (e.g., "astralsorcery:gui/gui_name")
     */
    public static String gui(String name) {
        return Constants.RESOURCE_ROOT + "gui/" + name;
    }

    /**
     * Get a resource location for a model
     *
     * @param name The model name
     * @return The full resource location (e.g., "astralsorcery:model/model_name")
     */
    public static String model(String name) {
        return Constants.RESOURCE_ROOT + "model/" + name;
    }

    /**
     * Get a resource path without the domain
     *
     * @param path The path
     * @return The path without mod ID prefix
     */
    public static String raw(String path) {
        return path;
    }

    /**
     * Join multiple parts into a resource path
     *
     * @param parts The parts to join
     * @return The joined path (e.g., "astralsorcery:part1/part2/part3")
     */
    public static String join(String... parts) {
        if (parts == null || parts.length == 0) {
            return Constants.RESOURCE_ROOT;
        }

        StringBuilder sb = new StringBuilder(Constants.RESOURCE_ROOT);
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                sb.append("/");
            }
            sb.append(parts[i]);
        }

        return sb.toString();
    }

    /**
     * Format a resource location with format arguments
     *
     * @param format The format string (e.g., "prefix_%s_suffix")
     * @param args   The arguments
     * @return The formatted resource location
     */
    public static String format(String format, Object... args) {
        return String.format(Constants.MODID + ":" + format, args);
    }

    /**
     * Check if a resource string has the mod ID prefix
     *
     * @param resource The resource string
     * @return True if the resource has the mod ID prefix
     */
    public static boolean hasModPrefix(String resource) {
        return resource != null && resource.startsWith(Constants.RESOURCE_ROOT);
    }

    /**
     * Strip the mod ID prefix from a resource
     *
     * @param resource The resource string
     * @return The resource without the mod ID prefix
     */
    public static String stripModPrefix(String resource) {
        if (hasModPrefix(resource)) {
            return resource.substring(Constants.RESOURCE_ROOT.length());
        }
        return resource;
    }

    /**
     * Add the mod ID prefix to a resource if not present
     *
     * @param resource The resource string
     * @return The resource with the mod ID prefix
     */
    public static String addModPrefix(String resource) {
        if (!hasModPrefix(resource)) {
            return Constants.RESOURCE_ROOT + resource;
        }
        return resource;
    }

    /**
     * Create a full resource path from parts
     *
     * @param parts The parts to join
     * @return The full resource location
     */
    public static String path(String... parts) {
        return join(parts);
    }

    /**
     * Get a safe resource name (replaces invalid characters)
     *
     * @param name The input name
     * @return The safe resource name
     */
    public static String toSafeName(String name) {
        if (name == null) {
            return "";
        }

        // Replace invalid characters with underscores
        return name.replaceAll("[^a-zA-Z0-9_]", "_");
    }

    /**
     * Get a resource name in PascalCase
     *
     * @param name The input name (e.g., "item_example")
     * @return The name in PascalCase (e.g., "ItemExample")
     */
    public static String toPascalCase(String name) {
        if (name == null || name.isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;

        for (char c : name.toCharArray()) {
            if (c == '_') {
                capitalizeNext = true;
            } else {
                if (capitalizeNext) {
                    result.append(Character.toUpperCase(c));
                    capitalizeNext = false;
                } else {
                    result.append(Character.toLowerCase(c));
                }
            }
        }

        return result.toString();
    }
}
