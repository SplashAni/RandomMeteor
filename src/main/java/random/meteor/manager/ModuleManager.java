package random.meteor.manager;

import meteordevelopment.meteorclient.systems.modules.Modules;
import org.reflections.Reflections;
import random.meteor.Main;
import random.meteor.util.system.Mod;

import java.util.*;

public class ModuleManager extends Manager {
    private final List<Mod> modules = new ArrayList<>();
    private final Map<Class<? extends Mod>, Mod> moduleMap = new HashMap<>();

    @Override
    public void onInitialize() {
        Reflections reflections = new Reflections("random.meteor.systems.modules");
        Set<Class<? extends Mod>> moduleClasses = reflections.getSubTypesOf(Mod.class);

        for (Class<? extends Mod> modClass : moduleClasses) {
            try {
                Mod mod = modClass.getDeclaredConstructor().newInstance();
                Modules.get().add(mod);
                modules.add(mod);
                moduleMap.put(modClass, mod); // keep track of class → instance
            } catch (Exception e) {
                Main.LOGGER.
                    error("Unable to load module: " + modClass.getName(), e);
            }
        }

        System.out.println("Loaded " + modules.size() + " modules.");
    }

    public Mod getMod(String name) {
        for (Mod module : modules) {
            if (module.getName().equalsIgnoreCase(name)) return module;
        }
        return null;
    }

    public Mod getMod(Class<? extends Mod> clazz) {
        return moduleMap.get(clazz);
    }

    public List<Mod> getModules() {
        return modules;
    }

    public Map<Class<? extends Mod>, Mod> getModuleMap() {
        return moduleMap;
    }
}
