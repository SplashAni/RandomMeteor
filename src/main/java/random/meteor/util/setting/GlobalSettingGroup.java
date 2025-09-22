package random.meteor.util.setting;

import meteordevelopment.meteorclient.settings.SettingGroup;
import random.meteor.util.system.Mod;

public abstract class GlobalSettingGroup {
    public Mod mod;
    String name;
    SettingGroup settingGroup;

    public GlobalSettingGroup(Mod mod, String name) {
        this.mod = mod;
        this.name = name;
    }


    public SettingGroup getSettingGroup() {
        return settingGroup != null ? settingGroup : (settingGroup = mod.settings.createGroup(name + " Settings"));
    }
    public void onActivate(){

    }
    public void onPreTick(){

    }


}
