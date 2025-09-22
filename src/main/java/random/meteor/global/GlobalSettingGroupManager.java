package random.meteor.global;

import random.meteor.Main;
import random.meteor.manager.Manager;
import random.meteor.manager.ModuleManager;
import random.meteor.util.setting.GlobalSettingGroup;
import random.meteor.util.system.Mod;

import java.util.ArrayList;
import java.util.List;

public class GlobalSettingGroupManager extends Manager {
    List<ModGroups> modGroups = new ArrayList<>();

    @Override
    public void onInitialize() {

        ModuleManager moduleManager = Main.MANAGERS.getManager(ModuleManager.class);

        if (moduleManager.getModules().isEmpty()) {
            Main.LOGGER.error("Calling global setting manager before the module manager has been initialized.");
        } else {
            for (Mod module : moduleManager.getModules()) {
                modGroups.add(module,module.get)
            }
        }

        super.onInitialize();
    }

    public record ModGroups(Mod mod, List<GlobalSettingGroup> groups) {

    }
}
