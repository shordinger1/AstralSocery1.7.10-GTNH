/*******************************************************************************
 * Astral Sorcery - Minecraft 1.7.10 Port
 *
 * OBJ Model Loader - Loads Wavefront OBJ models for rendering
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.renderer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.util.LogHelper;

/**
 * OBJ Model Loader for 1.7.10
 * <p>
 * Loads and renders Wavefront .obj models for blocks and items.
 * Supports vertices, texture coordinates, normals, and faces.
 * <p>
 * Usage:
 * 
 * <pre>
 * // Load model
 * ObjModel model = ObjModelLoader.INSTANCE.loadModel("models/obj/block/altar.obj");
 *
 * // Render model
 * model.render();
 * </pre>
 */
@SideOnly(Side.CLIENT)
public class ObjModelLoader {

    public static final ObjModelLoader INSTANCE = new ObjModelLoader();

    /** Cache of loaded models */
    private final Map<String, ObjModel> modelCache = new HashMap<>();

    private ObjModelLoader() {
        // Singleton instance
    }

    /**
     * Load an OBJ model from resources
     *
     * @param path Model path (e.g., "models/obj/block/altar.obj")
     * @return The loaded model, or null if failed
     */
    public ObjModel loadModel(String path) {
        // Check cache first
        if (modelCache.containsKey(path)) {
            return modelCache.get(path);
        }

        try {
            ResourceLocation location = new ResourceLocation("astralsorcery", path);
            InputStream stream = getResourceAsStream(location);

            if (stream == null) {
                LogHelper.warn("Failed to load OBJ model: " + path + " (not found)");
                return null;
            }

            ObjModel model = parseObj(stream, path);
            modelCache.put(path, model);

            LogHelper.debug(
                "Loaded OBJ model: " + path
                    + " ("
                    + model.getVertexCount()
                    + " vertices, "
                    + model.getFaceCount()
                    + " faces)");

            return model;

        } catch (Exception e) {
            LogHelper.error("Failed to load OBJ model: " + path, e);
            return null;
        }
    }

    /**
     * Get input stream for resource location
     */
    private InputStream getResourceAsStream(ResourceLocation location) {
        try {
            net.minecraft.client.resources.IResource resource = net.minecraft.client.Minecraft.getMinecraft()
                .getResourceManager()
                .getResource(location);
            return resource.getInputStream();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Parse OBJ file from input stream
     */
    private ObjModel parseObj(InputStream stream, String path) throws IOException {
        List<float[]> vertices = new ArrayList<>();
        List<float[]> uvs = new ArrayList<>();
        List<float[]> normals = new ArrayList<>();
        List<ObjFace> faces = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line;

        int lineNum = 0;
        while ((line = reader.readLine()) != null) {
            lineNum++;
            line = line.trim();

            // Skip empty lines and comments
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            String[] parts = line.split("\\s+");
            String type = parts[0];

            try {
                switch (type) {
                    case "v": // Vertex
                        if (parts.length >= 4) {
                            float x = Float.parseFloat(parts[1]);
                            float y = Float.parseFloat(parts[2]);
                            float z = Float.parseFloat(parts[3]);
                            vertices.add(new float[] { x, y, z });
                        }
                        break;

                    case "vt": // Texture coordinate
                        if (parts.length >= 3) {
                            float u = Float.parseFloat(parts[1]);
                            float v = Float.parseFloat(parts[2]);
                            uvs.add(new float[] { u, v });
                        }
                        break;

                    case "vn": // Normal
                        if (parts.length >= 4) {
                            float nx = Float.parseFloat(parts[1]);
                            float ny = Float.parseFloat(parts[2]);
                            float nz = Float.parseFloat(parts[3]);
                            normals.add(new float[] { nx, ny, nz });
                        }
                        break;

                    case "f": // Face
                        if (parts.length >= 4) {
                            ObjFace face = parseFace(parts, vertices.size(), uvs.size(), normals.size());
                            if (face != null) {
                                faces.add(face);
                            }
                        }
                        break;

                    case "o": // Object name
                    case "g": // Group
                    case "mtllib": // Material library
                    case "usemtl": // Use material
                        // Ignore for now
                        break;
                }
            } catch (NumberFormatException e) {
                LogHelper.warn("Invalid number in OBJ file " + path + " at line " + lineNum + ": " + line);
            }
        }

        reader.close();
        return new ObjModel(vertices, uvs, normals, faces, path);
    }

    /**
     * Parse face definition
     * Format: f v1/vt1/vn1 v2/vt2/vn2 v3/vt3/vn3 ...
     */
    private ObjFace parseFace(String[] parts, int vertCount, int uvCount, int normCount) {
        // Skip the "f" at index 0
        List<int[]> vertices = new ArrayList<>();

        for (int i = 1; i < parts.length; i++) {
            String vertexDef = parts[i];
            String[] indices = vertexDef.split("/");

            int v = 0, vt = 0, vn = 0;

            // Parse vertex index (required)
            if (!indices[0].isEmpty()) {
                v = Integer.parseInt(indices[0]);
                // Convert from 1-based to 0-based, handle negative indices
                if (v < 0) {
                    v = vertCount + v + 1;
                } else {
                    v = v - 1;
                }
            }

            // Parse UV index (optional)
            if (indices.length > 1 && !indices[1].isEmpty()) {
                vt = Integer.parseInt(indices[1]);
                if (vt < 0) {
                    vt = uvCount + vt + 1;
                } else {
                    vt = vt - 1;
                }
            }

            // Parse normal index (optional)
            if (indices.length > 2 && !indices[2].isEmpty()) {
                vn = Integer.parseInt(indices[2]);
                if (vn < 0) {
                    vn = normCount + vn + 1;
                } else {
                    vn = vn - 1;
                }
            }

            vertices.add(new int[] { v, vt, vn });
        }

        return new ObjFace(vertices);
    }

    /**
     * OBJ Model representation
     */
    public static class ObjModel {

        private final List<float[]> vertices;
        private final List<float[]> uvs;
        private final List<float[]> normals;
        private final List<ObjFace> faces;
        private final String path;

        public ObjModel(List<float[]> vertices, List<float[]> uvs, List<float[]> normals, List<ObjFace> faces,
            String path) {
            this.vertices = vertices;
            this.uvs = uvs;
            this.normals = normals;
            this.faces = faces;
            this.path = path;
        }

        /**
         * Render the model using tessellator
         */
        public void render() {
            Tessellator tessellator = Tessellator.instance;

            for (ObjFace face : faces) {
                face.render(tessellator, vertices, uvs, normals);
            }
        }

        public int getVertexCount() {
            return vertices.size();
        }

        public int getFaceCount() {
            return faces.size();
        }

        public String getPath() {
            return path;
        }
    }

    /**
     * Face definition
     * Contains multiple vertices forming a polygon (usually triangle or quad)
     */
    public static class ObjFace {

        private final List<int[]> vertices; // Each entry: [vertexIndex, uvIndex, normalIndex]

        public ObjFace(List<int[]> vertices) {
            this.vertices = vertices;
        }

        /**
         * Render this face
         * Triangulates quads if necessary
         */
        public void render(Tessellator tessellator, List<float[]> vertList, List<float[]> uvList,
            List<float[]> normList) {
            if (vertices.size() < 3) {
                return; // Need at least 3 vertices
            }

            // Triangulate: fan from first vertex
            int[] v0 = vertices.get(0);

            for (int i = 1; i < vertices.size() - 1; i++) {
                int[] v1 = vertices.get(i);
                int[] v2 = vertices.get(i + 1);

                // Vertex 0
                if (v0[0] < vertList.size() && v0[1] < uvList.size() && v0[2] < normList.size()) {
                    float[] vert = vertList.get(v0[0]);
                    float[] uv = uvList.get(v0[1]);
                    float[] norm = normList.get(v0[2]);

                    tessellator.setNormal(norm[0], norm[1], norm[2]);
                    tessellator.addVertexWithUV(vert[0], vert[1], vert[2], uv[0], uv[1]);
                }

                // Vertex 1
                if (v1[0] < vertList.size() && v1[1] < uvList.size() && v1[2] < normList.size()) {
                    float[] vert = vertList.get(v1[0]);
                    float[] uv = uvList.get(v1[1]);
                    float[] norm = normList.get(v1[2]);

                    tessellator.setNormal(norm[0], norm[1], norm[2]);
                    tessellator.addVertexWithUV(vert[0], vert[1], vert[2], uv[0], uv[1]);
                }

                // Vertex 2
                if (v2[0] < vertList.size() && v2[1] < uvList.size() && v2[2] < normList.size()) {
                    float[] vert = vertList.get(v2[0]);
                    float[] uv = uvList.get(v2[1]);
                    float[] norm = normList.get(v2[2]);

                    tessellator.setNormal(norm[0], norm[1], norm[2]);
                    tessellator.addVertexWithUV(vert[0], vert[1], vert[2], uv[0], uv[1]);
                }
            }
        }
    }
}
