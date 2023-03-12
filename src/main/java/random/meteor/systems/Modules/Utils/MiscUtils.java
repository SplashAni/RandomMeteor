package random.meteor.systems.Modules.Utils;
import org.lwjgl.glfw.GLFW;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class MiscUtils {

    public static long lastSwapTime = 0;

    public static boolean shouldTrigger() {
        if (GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_RIGHT) != GLFW.GLFW_PRESS || mc.currentScreen != null) return false;
        long timeSinceLastSwap = System.currentTimeMillis() - lastSwapTime;
        return timeSinceLastSwap >= 1000; // 1 second delay
    }
}
