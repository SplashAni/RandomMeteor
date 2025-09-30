package random.meteor.manager;

import meteordevelopment.meteorclient.systems.modules.Modules;
import org.reflections.Reflections;
import random.meteor.Main;
import random.meteor.global.ModListSetting;
import random.meteor.util.setting.GlobalSettingGroup;
import random.meteor.util.system.Mod;

import java.util.*;

public class ModuleManager extends Manager {
    private final List<Mod> modules = new ArrayList<>();
    private final Map<Class<? extends GlobalSettingGroup>, List<Mod>> groupSettings = new HashMap<>();

    @Override
    public void onInitialize() {
        Reflections reflections = new Reflections("random.meteor.systems.modules");
        Set<Class<? extends Mod>> moduleClasses = reflections.getSubTypesOf(Mod.class);

        for (Class<? extends Mod> modClass : moduleClasses) {
            try {
                Mod mod = modClass.getDeclaredConstructor().newInstance();
                Modules.get().add(mod);

                if (!mod.getGlobalSettingGroupList().isEmpty())
                    for (GlobalSettingGroup globalSettingGroup : mod.getGlobalSettingGroupList()) {
                        groupSettings.computeIfAbsent(globalSettingGroup.getClass(), k -> new ArrayList<>()).add(mod);
                    }

                modules.add(mod);
            } catch (Exception e) {
                Main.LOGGER.error("Unable to load module: {}", modClass.getName(), e);
            }
        }
    }

    public Mod getMod(String name) {
        for (Mod module : modules) {
            if (module.getName().equalsIgnoreCase(name)) return module;
        }
        return null;
    }

    public List<Mod> getModWithSettingGroup(ModListSetting<?> groupSetting) { // ACTAULY FIANLLY AFTER SDO LONG WHAT THE HELLL
        Class<? extends GlobalSettingGroup> groupClass = groupSetting.getGroupClass();
        return groupSettings.getOrDefault(groupClass, Collections.emptyList());
    }

    public <T extends GlobalSettingGroup> List<Mod> getModWithSettingGroup(Class<T> groupClass) {
        return groupSettings.getOrDefault(groupClass, Collections.emptyList());
    }

    public List<Mod> getModules() {
        return modules;
    }
}
