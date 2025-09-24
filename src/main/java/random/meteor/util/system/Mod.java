package random.meteor.util.system;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import random.meteor.Main;
import random.meteor.global.GlobalSettingGroupManager;
import random.meteor.global.SettingGroups;
import random.meteor.util.player.PlayerUtil;
import random.meteor.util.setting.GlobalSettingGroup;
import random.meteor.util.setting.groups.CenterSettingGroup;
import random.meteor.util.setting.modes.CenterTiming;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

public class Mod extends Module implements SettingGroups {
    String name, desc;
    Category category; // will be used to represent the moduel catgegory in future...;
    public Setting<Boolean> debug;
    public Setting<Boolean> debugTimer;
    public Setting<Boolean> debugActions;
    public Setting<Boolean> debugLogic;
    boolean allowDebug = true;
    List<GlobalSettingGroup> globalSettingGroupList = new ArrayList<>();

    public Mod(String name, String desc, Category category) {
        super(Main.RM, name, desc);
        this.name = name;
        this.desc = desc;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public Mod(String name, Category category) {
        super(Main.RM, name, "No description yet...");
        this.name = name;
        this.desc = "";
        this.category = category;
        setAllowDebug(true);
    }



    @SafeVarargs
    protected final <T extends GlobalSettingGroup> void register(Class<? extends T>... groupClasses) {
        for (Class<? extends T> groupClass : groupClasses) {
            GlobalSettingGroup group = Main.MANAGERS.getManager(GlobalSettingGroupManager.class) .registerGroupForMod(this, groupClass,false);
            if (group != null && !globalSettingGroupList.contains(group)) globalSettingGroupList.add(group);
        }
    }



    public void setAllowDebug(boolean allowDebug) {
        this.allowDebug = allowDebug;

        if (allowDebug && debug == null) {
            debug = createDebugBool("debug", "Writes whats happening to chat", () -> true);

            debugTimer = createDebugBool("debug-timer", "Debugs timer events", debug::get);
            debugActions = createDebugBool("debug-actions", "Debugs actions", debug::get);
            debugLogic = createDebugBool("debug-logic", "Debugs logic", debug::get);
        }
    }


    private Setting<Boolean> createDebugBool(String name, String desc, BooleanSupplier visible) {
        return settings.getDefaultGroup().add(new BoolSetting.Builder()
            .name(name)
            .description(desc)
            .defaultValue(false)
            .visible(visible::getAsBoolean)
            .build()
        );
    }


    @Override
    public void onActivate() {

        globalSettingGroupList.forEach(GlobalSettingGroup::onActivate);
        super.onActivate();
    }

    @EventHandler
    public void onPreTick(TickEvent.Pre event) {
        globalSettingGroupList.forEach(GlobalSettingGroup::onPreTick);
    }

    public void debug(String message, boolean confirmer) {
        if (allowDebug && debug != null && debug.get() && confirmer) info(message);
    }

    public List<GlobalSettingGroup> getGlobalSettingGroupList() {
        return globalSettingGroupList;
    }
}
