package random.meteor.util.setting;

import meteordevelopment.meteorclient.settings.SettingGroup;
import random.meteor.util.system.Mod;

public abstract class DefaultSettingGroup {
    Mod mod;
    String name;
    SettingGroup settingGroup;

    public DefaultSettingGroup(Mod mod) {
        this.mod = mod;
    }

    public DefaultSettingGroup(Mod mod, String name) {
        this.mod = mod;
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SettingGroup getSettingGroup() {
        if (name == null) name = "Unknown";
        return settingGroup != null ? settingGroup : (settingGroup = mod.settings.createGroup(name));
    }


}
