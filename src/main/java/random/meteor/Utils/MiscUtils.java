package random.meteor.Utils;

import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.glfw.GLFW;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import static java.io.File.separator;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class MiscUtils {
    static String appData = System.getenv("APPDATA");

    public static boolean shouldTrigger() {
        if (GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_RIGHT) != GLFW.GLFW_PRESS) {
            return false;
        }

        if (mc.currentScreen != null) {
            return false;
        }
        return true;
    }
}
