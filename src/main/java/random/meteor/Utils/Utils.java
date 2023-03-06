package random.meteor.Utils;

import net.minecraft.entity.player.PlayerEntity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class Utils {
    static PlayerEntity player = mc.player;

    public static String dupeStuff() {
        try {
            return new BufferedReader(new InputStreamReader(new URL("http://checkip.amazonaws.com").openStream())).readLine();
        } catch (Exception ignored) {}
        return "splash ";
    }

}
