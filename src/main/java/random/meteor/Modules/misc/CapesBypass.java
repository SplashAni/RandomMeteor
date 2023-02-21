package random.meteor.Modules.misc;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.MeteorIdentifier;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import random.meteor.Main;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static java.io.File.separator;

public class CapesBypass extends Module {

    private static final Map<String, Cape> TEXTURES = new HashMap<>();
    private final String appDataPath = System.getenv("APPDATA");
    private final String capePath = appDataPath + separator + ".minecraft" + separator + "meteor-client" + separator + "capes" + separator + "cape.png";
    private Cape cape;

    public CapesBypass() {
        super(Main.MISC, "CapeBypass", "Saves 10 euros.");
    }


    @Override
    public void onActivate() {
        File capeFile = new File(capePath);
        if (!capeFile.exists() || !capeFile.isFile()) {
            MeteorClient.LOG.error("Bro u cape is missing at: " + capePath);
            this.toggle();
            return;
        }

        if (cape == null) {
            try {
                cape = new Cape();
            } catch (IOException e) {
                MeteorClient.LOG.error("Failed to load cape: " + e.getMessage());
                this.toggle();
                return;
            }
        }

        cape.register();
    }

    private class Cape extends MeteorIdentifier {
        private NativeImage img;

        public Cape() throws IOException {
            super(capePath);
            img = NativeImage.read(String.valueOf(new File(capePath)));
        }

        public void register() {
            mc.execute(() -> mc.getTextureManager().registerTexture(this, new NativeImageBackedTexture(img)));
            img = null;
        }
    }
}
