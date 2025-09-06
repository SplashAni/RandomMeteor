package random.meteor.util.system;

import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.systems.modules.Module;
import random.meteor.Main;

public class Mod extends Module {
    String name, desc;
    Category category; // will be used to represent the moduel catgegory in future...;
    public Setting<Boolean> debug;
    public Setting<Boolean> debugTimer;
    public Setting<Boolean> debugActions;
    boolean allowDebug = true;

    public Mod(String name, String desc, Category category) {
        super(Main.RM, name, desc);
        this.name = name;
        this.desc = desc;
        this.category = category;
    }

    public Mod(String name, Category category) {
        super(Main.RM, name, "No description yet...");
        this.name = name;
        this.desc = "";
        this.category = category;
        setAllowDebug(true);
    }

    public void setAllowDebug(boolean allowDebug) {
        this.allowDebug = allowDebug;
        if (allowDebug && debug == null) {
            debug = settings.getDefaultGroup().add(new BoolSetting.Builder()
                .name("debug")
                .description("Writes whats happening more detail to chat.")
                .defaultValue(false)
                .build()
            );
            debugTimer = settings.getDefaultGroup().add(new BoolSetting.Builder()
                .name("debug-timer")
                .defaultValue(false)
                    .visible(debug::get)
                .build()
            );
            debugActions = settings.getDefaultGroup().add(new BoolSetting.Builder()
                .name("debug-actions")
                .defaultValue(false)
                .visible(debug::get)
                .build()
            );
        }
    }

    public void debug(String message) { // todo: fancy debug prefix xd
        if (debug.get()) info(message);
    }
}
