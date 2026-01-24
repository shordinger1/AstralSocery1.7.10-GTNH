/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.compound;

import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.util.Blending;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CompoundObjectEffect
 * Created by HellFirePvP
 * Date: 16.02.2017 / 16:34
 */
public abstract class CompoundObjectEffect extends EntityComplexFX {

    @Override
    public final void render(float pTicks) {
        GL11.glPushMatrix();
        Tessellator tes = Tessellator.instance;
        getGroup().beginDrawing(tes);
        render(tes, pTicks);
        tes.draw();
        GL11.glPopMatrix();
    }

    public abstract ObjectGroup getGroup();

    public abstract void render(Tessellator vb, float pTicks);

    public enum ObjectGroup {

        SOLID_COLOR_SPHERE;

        public void beginDrawing(Tessellator tes) {
            switch (this) {
                case SOLID_COLOR_SPHERE:
                    tes.startDrawing(GL11.GL_TRIANGLES);
                    break;
                default:
                    break;
            }
        }

        public void prepareGLContext() {
            switch (this) {
                case SOLID_COLOR_SPHERE:
                    Blending.DEFAULT.apply();
                    break;
                default:
                    break;
            }
        }

        public void revertGLContext() {
            switch (this) {
                case SOLID_COLOR_SPHERE:
                    // Reset blending handled by GLState
                    break;
                default:
                    break;
            }
        }

    }

}
