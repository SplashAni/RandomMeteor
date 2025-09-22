package random.meteor.global;

import random.meteor.Main;
import random.meteor.manager.Manager;
import random.meteor.manager.ModuleManager;
import random.meteor.util.setting.GlobalSettingGroup;
import random.meteor.util.setting.IGlobalManaged;
import random.meteor.util.setting.groups.RangeSettingGroup;
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
                modGroups.add(new ModGroups(module, module.getGlobalSettingGroupList()));
            }
        }
        super.onInitialize();
    }

    public void setGlobal(Mod mod) {
        for (GlobalSettingGroup modSettingGroup : mod.getGlobalSettingGroupList()) {
            ((IGlobalManaged)modSettingGroup.getSettingGroup()).randomMeteor$setHideSettings(true); // thanks !!duck on fabrics discord i always forget
        }
    }


    public record ModGroups(Mod mod, List<GlobalSettingGroup> groups) {

    }
}
