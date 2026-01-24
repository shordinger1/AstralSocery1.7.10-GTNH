/*******************************************************************************
 * Migration Compatibility Layer for 1.7.10
 * EntityDataManager wraps DataWatcher
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

import net.minecraft.entity.DataWatcher;

/**
 * Compatibility wrapper for 1.7.10 DataWatcher
 * Mimics 1.12+ EntityDataManager API
 */
public class EntityDataManager {

    private final DataWatcher dataWatcher;

    public EntityDataManager(DataWatcher dataWatcher) {
        this.dataWatcher = dataWatcher;
    }

    /**
     * Register a parameter with its default value
     */
    public <T> void register(DataParameter<T> key, T value) {
        DataSerializer<T> serializer = key.getSerializer();
        DataWatcher.WatchableObject watchable = serializer.createWatchableObject(key.getId(), value);
        // Add the watchable object to DataWatcher
        dataWatcher.watchedObjects.put(key.getId(), watchable);
        dataWatcher.isBlank = false;
    }

    /**
     * Get the value of a parameter
     */
    public <T> T get(DataParameter<T> key) {
        DataWatcher.WatchableObject watchable = dataWatcher.getWatchedObject(key.getId());
        return (T) watchable.getObject();
    }

    /**
     * Set the value of a parameter
     */
    public <T> void set(DataParameter<T> key, T value) {
        dataWatcher.updateObject(key.getId(), value);
    }

    /**
     * Mark a parameter as dirty (for sync)
     */
    public <T> void setDirty(DataParameter<T> key) {
        dataWatcher.objectChanged = true;
    }

    /**
     * Check if a parameter is dirty
     */
    public boolean isDirty() {
        return dataWatcher.objectChanged;
    }

    /**
     * Clear dirty flag
     */
    public void setClean() {
        dataWatcher.objectChanged = false;
    }

    /**
     * Get the underlying DataWatcher
     */
    public DataWatcher getDataWatcher() {
        return dataWatcher;
    }

    /**
     * Static helper to create a key for an entity class
     */
    public static <T> DataParameter<T> createKey(Class<?> entityClass, DataSerializer<T> serializer) {
        return new DataParameter<>(serializer);
    }
}
