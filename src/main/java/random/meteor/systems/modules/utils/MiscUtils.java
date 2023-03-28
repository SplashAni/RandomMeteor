package random.meteor.systems.modules.utils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionType;
import org.lwjgl.glfw.GLFW;
import static meteordevelopment.meteorclient.MeteorClient.mc;
import static random.meteor.systems.modules.utils.CombatUtils.getPlayer;

public class MiscUtils {
    public static long lastSwapTime = 0;

    public static boolean shouldTrigger(long delay) {
        if (GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_RIGHT) != GLFW.GLFW_PRESS || mc.currentScreen != null) return false;
        long timeSinceLastSwap = System.currentTimeMillis() - lastSwapTime;
        return timeSinceLastSwap >= delay; // custom delay
    }
    public static DimensionType dimension(){
        return mc.world.getDimension();
    }
}
