/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.resource;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AssetLibrary
 * Created by HellFirePvP
 * Date: 09.08.2016 / 11:00
 *
 * 1.7.10 Port:
 * - Simplified texture loading library
 * - Manages texture resources for particle effects
 */
public class AssetLibrary {

    public static boolean reloading = false;
    private static final Map<String, BindableResource> loadedTextures = new HashMap<>();

    /**
     * Load a texture from the effects folder
     * 1.7.10: Simplified texture loading
     */
    public static BindableResource loadTexture(TextureLocation location, String name) {
        if (name.endsWith(".png")) {
            throw new IllegalArgumentException("Tried to loadTexture with appended .png from the AssetLibrary!");
        }

        String key = location.prefix + name;

        if (loadedTextures.containsKey(key)) {
            return loadedTextures.get(key);
        }

        BindableResource res = new BindableResource("astralsorcery", location.prefix + name + ".png");
        loadedTextures.put(key, res);
        return res;
    }

    /**
     * Texture location enum
     */
    public static enum TextureLocation {

        EFFECT("textures/effects/"),
        PARTICLE("textures/particles/");

        private final String prefix;

        TextureLocation(String prefix) {
            this.prefix = prefix;
        }

        public String getPrefix() {
            return prefix;
        }
    }

}
