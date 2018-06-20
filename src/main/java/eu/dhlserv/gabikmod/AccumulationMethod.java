package eu.dhlserv.gabikmod;

import org.lwjgl.opengl.GL11;

public class AccumulationMethod {
    
    public static void createAccumulation() {
        float value = GabikMod.inst().getAccumulationValue();
        GL11.glAccum(GL11.GL_MULT, value);
        GL11.glAccum(GL11.GL_ACCUM, 1F - value);
        GL11.glAccum(GL11.GL_RETURN, 1F);
    }

}
