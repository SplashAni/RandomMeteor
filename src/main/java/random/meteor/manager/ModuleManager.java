package random.meteor.manager;

import meteordevelopment.meteorclient.systems.modules.Modules;
import org.reflections.Reflections;
import random.meteor.Main;
import random.meteor.util.system.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ModuleManager extends Manager {
    private final List<Mod> modules = new ArrayList<>();

    @Override
    public void onInitialize() {
        Reflections reflections = new Reflections("random.meteor.systems.modules");
        Set<Class<? extends Mod>> moduleClasses = reflections.getSubTypesOf(Mod.class);

        for (Class<? extends Mod> modClass : moduleClasses) {
            try {
                Mod mod = modClass.getDeclaredConstructor().newInstance();
                Modules.get().add(mod);
                modules.add(mod);
            } catch (Exception e) {
                Main.LOGGER.error("Unable to load module: " + modClass.getName(), e);
            }
        }

        System.out.println("Loaded " + modules.size() + " modules.");
    }

    public List<Mod> getModules() {
        return modules;
    }
}
