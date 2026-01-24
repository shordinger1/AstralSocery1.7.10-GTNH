/*******************************************************************************
 * Compatibility Class for VertexFormat (1.12.2) in 1.7.10
 ******************************************************************************/

package net.minecraft.client.renderer;

import java.util.ArrayList;
import java.util.List;

public class VertexFormat {

    private final List<VertexFormatElement> elements = new ArrayList<>();

    public VertexFormat() {}

    public void addElement(VertexFormatElement element) {
        elements.add(element);
    }

    public List<VertexFormatElement> getElements() {
        return elements;
    }

    public int getNextOffset() {
        return elements.size() * 16; // Simplified
    }
}
