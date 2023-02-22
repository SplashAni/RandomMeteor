package random.meteor.Utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class Utils {
    public static String dupeStuff() {
        try {
            return new BufferedReader(new InputStreamReader(new URL("http://checkip.amazonaws.com").openStream())).readLine();
        } catch (Exception ignored) {
        }
        return null;
    }
}