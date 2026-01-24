/*******************************************************************************
 * Compatibility Class for VertexFormatElement (1.12.2) in 1.7.10
 ******************************************************************************/

package net.minecraft.client.renderer;

public class VertexFormatElement {

    private final int index;
    private final EnumType type;
    private final EnumUsage usage;
    private final int count;

    public VertexFormatElement(int index, EnumType type, EnumUsage usage, int count) {
        this.index = index;
        this.type = type;
        this.usage = usage;
        this.count = count;
    }

    public int getIndex() {
        return index;
    }

    public EnumType getType() {
        return type;
    }

    public EnumUsage getUsage() {
        return usage;
    }

    public int getCount() {
        return count;
    }

    public enum EnumType {
        FLOAT,
        INT,
        BYTE
    }

    public enum EnumUsage {
        POSITION,
        UV,
        COLOR,
        NORMAL,
        PADDING
    }
}
