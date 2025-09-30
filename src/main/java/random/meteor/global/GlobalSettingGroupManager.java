package random.meteor.global;

import random.meteor.Main;
import random.meteor.manager.Manager;
import random.meteor.util.setting.GlobalSettingGroup;
import random.meteor.util.setting.groups.*;
import random.meteor.util.system.Mod;

import java.util.HashMap;
import java.util.Map;

public class GlobalSettingGroupManager extends Manager { // stores global settings xD
    private final Mod GLOBAL_MOD = new Mod("GLOBALHOLDER", "Global default settings", null);

    public Mod getGLOBAL_MOD() {
        return GLOBAL_MOD;
    }

    private final Map<Class<? extends GlobalSettingGroup>, GlobalSettingGroup> globalGroups = new HashMap<>();
    private final Map<Mod, Map<Class<? extends GlobalSettingGroup>, GlobalSettingGroup>> modGroups = new HashMap<>();
    private final Map<Mod, Map<Class<? extends GlobalSettingGroup>, Boolean>> useGlobalFlags = new HashMap<>();

    public Map<Mod, Map<Class<? extends GlobalSettingGroup>, GlobalSettingGroup>> getModGroups() {
        return modGroups;
    }

    @Override
    public void onInitialize() {
        registerGroups(CenterSettingGroup.class, DelaySettingGroup.class, PlaceSettingGroup.class, RangeSettingGroup.class, RenderSettingGroup.class, SwapSettingGroup.class, SwingSettingGroup.class);
        super.onInitialize();
    }

    @SuppressWarnings("unchecked")
    @SafeVarargs
    private void registerGroups(Class<? extends GlobalSettingGroup>... groups) {
        for (Class<? extends GlobalSettingGroup> groupClass : groups) {
            try {
                GlobalSettingGroup group = groupClass.getConstructor(Mod.class).newInstance(GLOBAL_MOD);
                globalGroups.put(groupClass, group);

                if (!GLOBAL_MOD.getGlobalSettingGroupList().contains(group)) {
                    group.getSettingGroup().add(
                        new ModListSetting.Builder<>()
                            .name("modules")
                            .description("Modules that use this global setting")
                            .groupClass((Class<GlobalSettingGroup>) groupClass) // <-- cast here
                            .build()
                    );

                    GLOBAL_MOD.getGlobalSettingGroupList().add(group);
                }

            } catch (Exception e) {
                Main.LOGGER.error("cont initialize global group {} {}", groupClass.getSimpleName(), e.getMessage());
            }
        }
    }



    public <T extends GlobalSettingGroup> T registerGroupForMod(Mod mod, Class<T> groupClass, boolean useGlobal) {
        Map<Class<? extends GlobalSettingGroup>, GlobalSettingGroup> modGroupMap = modGroups.computeIfAbsent(mod, k -> new HashMap<>());
        Map<Class<? extends GlobalSettingGroup>, Boolean> flagMap = useGlobalFlags.computeIfAbsent(mod, k -> new HashMap<>());

        if (modGroupMap.containsKey(groupClass)) return groupClass.cast(modGroupMap.get(groupClass));

        try {
            T group = useGlobal ? groupClass.cast(globalGroups.get(groupClass)) : groupClass.getConstructor(Mod.class).newInstance(mod);

            modGroupMap.put(groupClass, group);
            flagMap.put(groupClass, useGlobal);

            if (!mod.getGlobalSettingGroupList().contains(group)) {
                mod.getGlobalSettingGroupList().add(group);
            }

            return group;
        } catch (Exception e) {
            Main.LOGGER.error("cant register group {} for module {}: {}", groupClass.getSimpleName(), mod.getName(), e.getMessage());
            return null;
        }
    }
}
