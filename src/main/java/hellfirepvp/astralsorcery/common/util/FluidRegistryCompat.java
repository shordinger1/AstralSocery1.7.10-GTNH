package hellfirepvp.astralsorcery.common.util;

/**
 * Compatibility layer for FluidRegistry differences between versions.
 * Universal Bucket system was added in Forge 1.10+, doesn't exist in 1.7.10.
 */
public class FluidRegistryCompat {

    /**
     * Enable universal bucket support.
     * In 1.10+: This enables the Forge Universal Bucket system.
     * In 1.7.10: This is a no-op as the system doesn't exist.
     * GTNH has its own fluid handling mechanisms.
     */
    public static void enableUniversalBucket() {
        // In 1.7.10, the Universal Bucket system doesn't exist.
        // GTNH handles fluid containers differently.
        // This is a no-op method for API compatibility.
    }

    /**
     * Check if universal bucket is available.
     * In 1.7.10, this always returns false.
     */
    public static boolean isUniversalBucketEnabled() {
        return false;
    }
}
