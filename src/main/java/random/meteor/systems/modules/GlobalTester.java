package random.meteor.systems.modules;

import meteordevelopment.meteorclient.settings.SettingGroup;
import random.meteor.Main;
import random.meteor.manager.ModuleManager;
import random.meteor.util.setting.GlobalSettingGroup;
import random.meteor.util.setting.IGlobalManaged;
import random.meteor.util.setting.groups.RangeSettingGroup;
import random.meteor.util.system.Category;
import random.meteor.util.system.Mod;

public class GlobalTester extends Mod {
    Mod testball = null;

    public GlobalTester() {
        super("mod-test", Category.PVP);
    }

    @Override
    public void onActivate() {
        for (Mod mod : Main.MANAGERS.getManager(ModuleManager.class).getModules()) {
            if (mod.getName().equals("feet-trap")) {
                testball = mod;
                break;
            }
        }

        if (testball != null) {
            for (GlobalSettingGroup globalSettingGroup : testball.getGlobalSettingGroupList()) {
                if (globalSettingGroup instanceof RangeSettingGroup gr) {
                    SettingGroup sg = gr.getSettingGroup();
                    if (sg instanceof IGlobalManaged managed) {
                        managed.randomMeteor$setHideSettings(true);
                    }
                }
            }
        }
        super.onActivate();
    }

    @Override
    public void onDeactivate() {
        if (testball != null) {
            for (GlobalSettingGroup globalSettingGroup : testball.getGlobalSettingGroupList()) {
                if (globalSettingGroup instanceof RangeSettingGroup gr) {
                    SettingGroup sg = gr.getSettingGroup();
                    if (sg instanceof IGlobalManaged managed) {
                        managed.randomMeteor$setHideSettings(false);
                    }
                }
            }
        }
        super.onDeactivate();
    }
}
