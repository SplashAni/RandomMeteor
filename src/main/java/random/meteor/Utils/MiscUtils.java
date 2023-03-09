package random.meteor.Utils;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
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
    public static boolean isFalling() {
        return (mc.player.isFallFlying() && mc.player.getSafeFallDistance() > 3) ? true : false;
    }
    public static int getFallDistance() {
        if (!isFalling()) {
            return 0;
        }
        Vec3d pos = mc.player.getPos();
        Vec3d motion = mc.player.getVelocity();
        double distance = 0.0;
        while (distance < 256.0) { //TODOD: stop looping error
            Vec3d targetPos = pos.add(motion.multiply(distance, distance, distance));
            BlockHitResult result = mc.world.raycast(new RaycastContext(pos, targetPos, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player));
            if (result.getType() != HitResult.Type.MISS) {
                return (int) Math.ceil(distance - result.getPos().distanceTo(pos));
            }
            distance += 0.5;
        }
        return 0;
    }
}
