package random.meteor.util.system;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import random.meteor.Main;
import random.meteor.util.player.PlayerUtil;
import random.meteor.util.setting.DefaultSettingGroup;
import random.meteor.util.setting.groups.CenterSettingGroup;
import random.meteor.util.setting.modes.CenterTiming;

import java.util.function.BooleanSupplier;

public class Mod extends Module {
    String name, desc;
    Category category; // will be used to represent the moduel catgegory in future...;
    public Setting<Boolean> debug;
    public Setting<Boolean> debugTimer;
    public Setting<Boolean> debugActions;
    public Setting<Boolean> debugLogic;
    boolean allowDebug = true;
    boolean centerUntil;

    // all methods that might need custom events instead of repeating myself.. lOL
    CenterSettingGroup centerSettingGroup;

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
    protected <T extends DefaultSettingGroup> T register(Class<T> groupClass) {
        try {
            T group = groupClass.getConstructor(Mod.class).newInstance(this);

            if (group instanceof CenterSettingGroup) {
                centerSettingGroup = (CenterSettingGroup) group; // lets getthighhh tonightt
            }

            group.getSettingGroup();
            return group;
        } catch (Exception e) {
            Main.LOGGER.error("Unable to register Setting Group {}", e.getMessage());
            return null;
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
        centerUntil = centerSettingGroup != null &&
            centerSettingGroup.centerTiming.get() == CenterTiming.OnActivate;

        super.onActivate();
    }

    @EventHandler
    public void onPreTick(TickEvent.Pre event) {
        if (centerSettingGroup == null) return;

        boolean shouldCenter = centerSettingGroup.centerTiming.get() == CenterTiming.Always
            || centerUntil;

        if (shouldCenter && PlayerUtil.centerEvent(centerSettingGroup)) {
            debug(centerUntil ? "Finished centering on on activation" : "Centered player",debugActions.get());
            if (centerUntil) centerUntil = false;
        }
    }

    public void debug(String message, boolean confirmer) {
        if (allowDebug && debug != null && debug.get() && confirmer) info(message);
    }
}
