package random.meteor.Utils;

import meteordevelopment.meteorclient.utils.network.MeteorExecutor;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import static java.io.File.separator;

public class MiscUtils {
    static String appData = System.getenv("APPDATA");
    static String config = appData + separator + ".minecraft" + separator + "meteor-client" + separator + "capes";

    public static void createCapeConfig() {

        File folder = new File(config);

        if (!folder.exists()) {
            if (folder.mkdirs()) {
                downloadDefault();
            }

        }
    }
        private static void downloadDefault(){
            URL url = null;
            try {
                url = new URL("https://cdn.discordapp.com/attachments/1076740815419887619/1076943314676355163/cape.png");
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            URLConnection conn = null;
            try {
                conn = url.openConnection();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            InputStream in = null;
            try {
                in = conn.getInputStream();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(config + separator + "cape.png");
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

            byte[] buffer = new byte[1024];
            int bytesRead;
            while (true) {
                try {
                    if (!((bytesRead = in.read(buffer)) != -1)) break;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    out.write(buffer, 0, bytesRead);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            try {
                out.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                in.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
