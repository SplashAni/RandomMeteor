package random.meteor.global;

import meteordevelopment.meteorclient.settings.IVisible;
import meteordevelopment.meteorclient.settings.Setting;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import random.meteor.Main;
import random.meteor.manager.ModuleManager;
import random.meteor.util.setting.GlobalSettingGroup;
import random.meteor.util.system.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ModListSetting<T extends GlobalSettingGroup> extends Setting<List<Mod>> {
    private static List<String> suggestions;
    private final Class<T> groupClass;

    public ModListSetting(String name, String description, List<Mod> defaultValue, Consumer<List<Mod>> onChanged, Consumer<Setting<List<Mod>>> onListUpdated, IVisible visible, Class<T> groupClass) {
        super(name, description, defaultValue, onChanged, onListUpdated, visible);
        this.groupClass = groupClass;
    }

    private static ModuleManager getModManager() {
        return Main.MANAGERS.getManager(ModuleManager.class);
    }

    @Override
    public void resetImpl() {
        value = new ArrayList<>(defaultValue);
    }

    @Override
    protected boolean isValueValid(List<Mod> value) {
        return true;
    }

    @Override
    protected List<Mod> parseImpl(String str) {
        String[] values = str.split(",");
        List<Mod> mods = new ArrayList<>(values.length);

        for (String value : values) {
            Mod mod = getModManager().getMod(value.trim());
            if (mod != null) mods.add(mod);
        }

        return mods;
    }



    @Override
    public NbtCompound save(NbtCompound tag) {
        NbtList modsTag = new NbtList();
        for (Mod mod : get()) {
            if (mod.getGlobalSettingGroupList().stream().anyMatch(g -> g.getClass().equals(groupClass))) {
                modsTag.add(NbtString.of(mod.getName()));
            }
        }
        tag.put("Mods", modsTag);
        return tag;
    }

    @Override
    public List<Mod> load(NbtCompound tag) {
        get().clear();
        NbtList valueTag = tag.getListOrEmpty("Mods");
        for (NbtElement tagI : valueTag) {
            Mod mod = getModManager().getMod(tagI.asString().orElse(""));
            if (mod != null && mod.getGlobalSettingGroupList().stream().anyMatch(g -> g.getClass().equals(groupClass))) {
                get().add(mod);
            }
        }
        return get();
    }

    public Class<T> getGroupClass() {
        return groupClass;
    }

    @Override
    public List<String> getSuggestions() {
        if (suggestions == null) {
            suggestions = new ArrayList<>();
            for (Mod mod : getModManager().getModWithSettingGroup(groupClass)) {
                suggestions.add(mod.getName());
            }
        }
        return suggestions;
    }


    public static class Builder<T extends GlobalSettingGroup> extends SettingBuilder<Builder<T>, List<Mod>, ModListSetting<T>> {
        private Class<T> groupClass;

        public Builder() {
            super(new ArrayList<>(0));
        }

        private static void accept(Setting<List<Mod>> listSetting) {
        }

        public Builder<T> groupClass(Class<T> groupClass) {
            this.groupClass = groupClass;
            return this;
        }

        @Override
        public ModListSetting<T> build() {
            return new ModListSetting<>(name, description, defaultValue, onChanged, Builder::accept, visible, groupClass);
        }
    }
}
