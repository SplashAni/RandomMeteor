package random.meteor.Utils;
import org.lwjgl.glfw.GLFW;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class MiscUtils {

    public static long lastSwapTime = 0;


    public static boolean shouldTrigger() {
        if (GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_RIGHT) != GLFW.GLFW_PRESS) {
            return false;
        }
        if (mc.currentScreen != null) {
            return false;
        }
        long currentTime = System.currentTimeMillis();
        long timeSinceLastSwap = currentTime - lastSwapTime;
        return timeSinceLastSwap >= 1000; // 1 second delay
    }
}
