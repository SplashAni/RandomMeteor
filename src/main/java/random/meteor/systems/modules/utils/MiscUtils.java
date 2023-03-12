package random.meteor.systems.modules.utils;
import org.lwjgl.glfw.GLFW;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class MiscUtils {
    public static long lastSwapTime = 0;

    public static boolean shouldTrigger(long delay) {
        if (GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_RIGHT) != GLFW.GLFW_PRESS || mc.currentScreen != null) return false;
        long timeSinceLastSwap = System.currentTimeMillis() - lastSwapTime;
        return timeSinceLastSwap >= delay; // custom delay
    }
}
