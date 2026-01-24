/*******************************************************************************
 * Migration Compatibility Layer for 1.7.10
 * DataParameter maps to DataWatcher ID
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Compatibility layer for 1.12+ DataParameter system
 * Maps to 1.7.10 DataWatcher IDs
 */
public class DataParameter<T> {

    private static final AtomicInteger ID_ALLOCATOR = new AtomicInteger(0);

    private final int id;
    private final DataSerializer<T> serializer;

    public DataParameter(DataSerializer<T> serializer) {
        this.id = ID_ALLOCATOR.getAndIncrement();
        this.serializer = serializer;
    }

    public int getId() {
        return id;
    }

    public DataSerializer<T> getSerializer() {
        return serializer;
    }
}
