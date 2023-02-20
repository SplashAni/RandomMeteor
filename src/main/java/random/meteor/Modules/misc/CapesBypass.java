package random.meteor.Modules.misc;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.MeteorIdentifier;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import random.meteor.Main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.io.File.separator;

public class CapesBypass extends Module {
    private static final Map<String, Cape> TEXTURES = new HashMap<>();

    private static final List<Cape> TO_REGISTER = new ArrayList<>();

    public CapesBypass() {
        super(Main.MISC, "CapeBypass", "Saves 10 euros.");

        String capePath = MeteorClient.FOLDER + separator + "capes" + separator + "cape.png";

        File capeFile = new File(capePath);
        if (!capeFile.exists() || !capeFile.isFile()) {
            MeteorClient.LOG.error("Cape fil is missing" + capePath);
            this.toggle();
        } else {
            Cape cape = new Cape(capeFile);
            TEXTURES.put(capePath, cape);
        }

        MeteorClient.EVENT_BUS.subscribe(CapesBypass.class);
    }

    @EventHandler
    private static void onTick(TickEvent.Post event) {
        synchronized (TO_REGISTER) {
            for (Cape cape : TO_REGISTER) cape.register();
            TO_REGISTER.clear();
        }
    }

    private class Cape extends MeteorIdentifier {
        private final File file;
        private NativeImage img;

        public Cape(File file) {
            super(file.getAbsolutePath());
            this.file = file;
            try {
                img = NativeImage.read(String.valueOf(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void register() {
            mc.getTextureManager().registerTexture(this, new NativeImageBackedTexture(img));
            img = null;
        }
    }
}
