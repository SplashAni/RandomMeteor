/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package random.meteor.Modules.misc;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.MeteorIdentifier;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import random.meteor.Main;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class CapesBypass extends Module {
    private static final Map<String, Cape> TEXTURES = new HashMap<>();

    private static final List<Cape> TO_REGISTER = new ArrayList<>();
    private static final List<Cape> TO_REMOVE = new ArrayList<>();

    public CapesBypass() {
        super(Main.MISC,"CapeBypass","saves 10 euroes");
        // Load cape textures from local files
        File capesDir = new File(MeteorClient.FOLDER, "capes" + "cape.png");
        if (!capesDir.exists() || !capesDir.isDirectory()) {
            capesDir.mkdirs();
        }

        for (File file : capesDir.listFiles()) {
            String name = file.getName();
            if (!name.endsWith(".png")) continue;
            name = name.substring(0, name.lastIndexOf('.'));
            TEXTURES.put(name, new Cape(name, file));
        }

        MeteorClient.EVENT_BUS.subscribe(CapesBypass.class);
    }

    @EventHandler
    private static void onTick(TickEvent.Post event) {
        synchronized (TO_REGISTER) {
            for (Cape cape : TO_REGISTER) cape.register();
            TO_REGISTER.clear();
        }

        synchronized (TO_REMOVE) {
            for (Cape cape : TO_REMOVE) {
                TEXTURES.remove(cape.name);
                TO_REGISTER.remove(cape);
            }

            TO_REMOVE.clear();
        }
    }


    private class Cape extends MeteorIdentifier {
        private final String name;

        private NativeImage img;

        public Cape(String name, File file) {
            super("capes/" + name);

            this.name = name;

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
