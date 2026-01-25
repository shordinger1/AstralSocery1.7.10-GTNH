/*******************************************************************************
 * Migration Compatibility Layer for 1.7.10
 * ModelResourceLocation class for model resource locations
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

import java.util.Locale;

import net.minecraft.util.ResourceLocation;

/**
 * Compatibility class for 1.7.10.
 * In 1.12.2: net.minecraft.client.renderer.block.model.ModelResourceLocation
 * In 1.7.10: Different resource location system
 */
public class ModelResourceLocation extends ResourceLocation {

    private final String variant;

    public ModelResourceLocation(String location) {
        this(0, parsePathString(location));
    }

    public ModelResourceLocation(String domain, String path) {
        super(domain, path);
        this.variant = "normal";
    }

    public ModelResourceLocation(ResourceLocation location, String variantIn) {
        this(0, parsePathString(location.toString() + '#' + (variantIn == null ? "normal" : variantIn)));
    }

    private ModelResourceLocation(int unused, String... resourceName) {
        super(resourceName[0], resourceName[1]);
        this.variant = (resourceName[2] == null || resourceName[2].isEmpty()) ? "normal"
            : resourceName[2].toLowerCase(Locale.ROOT);
    }

    protected static String[] parsePathString(String pathIn) {
        String[] astring = new String[] { null, pathIn, null };
        int i = pathIn.indexOf('#');
        String s = pathIn;

        if (i >= 0) {
            astring[2] = pathIn.substring(i + 1, pathIn.length());

            if (i > 1) {
                s = pathIn.substring(0, i);
            }
        }

        // Split domain:path for 1.7.10
        int colonIdx = s.indexOf(':');
        if (colonIdx >= 0) {
            astring[0] = s.substring(0, colonIdx);
            astring[1] = s.substring(colonIdx + 1);
        } else {
            astring[0] = "minecraft";
            astring[1] = s;
        }
        return astring;
    }

    /**
     * Get the variant of this model resource location.
     */
    public String getVariant() {
        return this.variant;
    }

    @Override
    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        } else if (p_equals_1_ instanceof ModelResourceLocation && super.equals(p_equals_1_)) {
            ModelResourceLocation modelresourcelocation = (ModelResourceLocation) p_equals_1_;
            return this.variant.equals(modelresourcelocation.variant);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + this.variant.hashCode();
    }

    @Override
    public String toString() {
        return super.toString() + '#' + this.variant;
    }
}
