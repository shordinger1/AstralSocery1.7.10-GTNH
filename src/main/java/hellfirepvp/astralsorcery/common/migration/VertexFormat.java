/*******************************************************************************
 * Migration Compatibility Layer for 1.7.10
 * VertexFormat class for vertex formats
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.migration;

/**
 * Compatibility class for 1.7.10.
 * In 1.12.2: net.minecraft.client.renderer.vertex.VertexFormat
 * In 1.7.10: Different vertex format system - these are placeholder constants
 */
public class VertexFormat {

    // Common vertex format constants for 1.12.2 compatibility
    // In 1.7.10, Tessellator doesn't use explicit format objects
    public static final VertexFormat POSITION = new VertexFormat("POSITION");
    public static final VertexFormat POSITION_TEX = new VertexFormat("POSITION_TEX");
    public static final VertexFormat POSITION_TEX_COLOR = new VertexFormat("POSITION_TEX_COLOR");
    public static final VertexFormat POSITION_TEX_NORMAL = new VertexFormat("POSITION_TEX_NORMAL");
    public static final VertexFormat POSITION_COLOR = new VertexFormat("POSITION_COLOR");
    public static final VertexFormat ELEMENTS = POSITION_TEX_COLOR;
    public static final VertexFormat BLOCK = new VertexFormat("BLOCK");

    private final String name;

    private VertexFormat(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "VertexFormat." + name;
    }
}
