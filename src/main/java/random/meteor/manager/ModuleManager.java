package random.meteor.manager;

import meteordevelopment.meteorclient.systems.modules.Modules;
import org.reflections.Reflections;
import random.meteor.Main;
import random.meteor.util.system.Mod;

import java.util.Set;

public class ModuleManager implements Manager {

    @Override
    public void onInitialize() {

        Reflections reflections = new Reflections("random.meteor.systems.modules");

        Set<Class<? extends Mod>> module = reflections.getSubTypesOf(Mod.class);

        for (Class<? extends Mod> modClass : module) {
            try {
                Mod mod = modClass.getDeclaredConstructor().newInstance();
                Modules.get().add(mod); // why doesnt more devs do this instread of manually typing all day xd
            } catch (Exception e) {
                Main.LOGGER.error("Unable to load module {}", e.getMessage());
            }
        }

    }
}
