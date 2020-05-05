package utils;

import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.glGetError;

public class DebugUtils {
    public static void errorCheck(String placement, boolean printIfNoError) {
        int i = glGetError();
        if (i != GL_NO_ERROR) {
            System.out.println((placement == null ? "" : "[" + placement + "] ") + "Error code: " + i);
        }
        else if(printIfNoError) {
            System.out.println((placement == null ? "" : "[" + placement + "] ") + "No errors.");
        }
    }

    public static void errorCheck(String placement) {
        errorCheck(placement, true);
    }

    public static void errorCheck() {
        errorCheck(null);
    }
}
