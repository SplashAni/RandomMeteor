/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package random.meteor.global;

import meteordevelopment.meteorclient.settings.IVisible;
import meteordevelopment.meteorclient.settings.Setting;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import random.meteor.Main;
import random.meteor.manager.ModuleManager;
import random.meteor.util.system.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ModListSetting extends Setting<List<Mod>> {
    private static List<String> suggestions;

    public ModListSetting(String name, String description, List<Mod> defaultValue, Consumer<List<Mod>> onChanged, Consumer<Setting<List<Mod>>> onModActivated, IVisible visible) {
        super(name, description, defaultValue, onChanged, onModActivated, visible);
    }

    @Override
    public void resetImpl() {
        value = new ArrayList<>(defaultValue);
    }

    @Override
    protected List<Mod> parseImpl(String str) {
        String[] values = str.split(",");
        List<Mod> Mods = new ArrayList<>(values.length);

        try {
            for (String value : values) {
                Mod Mod = moduleManager().getMod(value.trim());
                if (Mod != null) Mods.add(Mod);
            }
        } catch (Exception ignored) {
        }

        return Mods;
    }

    @Override
    protected boolean isValueValid(List<Mod> value) {
        return true;
    }

    @Override
    public List<String> getSuggestions() {
        if (suggestions == null) {
            suggestions = new ArrayList<>(moduleManager().getModules().size());
            for (Mod xd : moduleManager().getModules()) {
                suggestions.add(xd.getName());
            }
        }

        return suggestions;
    }

    @Override
    public NbtCompound save(NbtCompound tag) {
        NbtList ModsTag = new NbtList();
        for (Mod xd : get()) {
            ModsTag.add(NbtString.of(xd.getName()));
        }
        tag.put("Mods", ModsTag);

        return tag;
    }

    @Override
    public List<Mod> load(NbtCompound tag) {
        get().clear();

        NbtList valueTag = tag.getListOrEmpty("Mods");
        for (NbtElement tagI : valueTag) {
            Mod Mod = moduleManager().getMod(tagI.asString().orElse(""));
            if (Mod != null) get().add(Mod);
        }

        return get();
    }

    private static ModuleManager moduleManager() {
        return Main.MANAGERS.getManager(ModuleManager.class);
    }

    public static class Builder extends SettingBuilder<Builder, List<Mod>, ModListSetting> {
        public Builder() {
            super(new ArrayList<>(0));
        }

        @SafeVarargs
        public final Builder defaultValue(Class<? extends Mod>... defaults) {
            List<Mod> Mods = new ArrayList<>();

            for (Class<? extends Mod> klass : defaults) {
                if (moduleManager().getMod(klass) != null) Mods.add(moduleManager().getMod(klass));
            }

            return defaultValue(Mods);
        }

        @Override
        public ModListSetting build() {
            return new ModListSetting(name, description, defaultValue, onChanged, listSetting -> {
                System.out.println("added module");
            }, visible);
        }
    }
}
