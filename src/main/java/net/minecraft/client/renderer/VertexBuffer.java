/*******************************************************************************
 * Compatibility Class for VertexBuffer (1.12.2) in 1.7.10
 * Simplified implementation for display list compatibility
 ******************************************************************************/

package net.minecraft.client.renderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

public class VertexBuffer {

    private final VertexFormat format;
    private int displayList = -1;
    private boolean isVBO = false;
    private int vboId = -1;
    private int count = 0;

    public VertexBuffer(VertexFormat format) {
        this.format = format;
    }

    public void bufferData(int[] buffer) {
        // VBO support not available in 1.7.10
        // Use display lists instead
    }

    public void deleteGlBuffers() {
        if (vboId > 0) {
            GL15.glDeleteBuffers(vboId);
            vboId = -1;
        }
    }

    public void drawBuffer() {
        if (displayList > 0) {
            GL11.glCallList(displayList);
        } else if (vboId > 0) {
            // VBO drawing would go here
            // For now, stub implementation
        }
    }

    public VertexFormat getVertexFormat() {
        return format;
    }
}
