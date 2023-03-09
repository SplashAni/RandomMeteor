package random.meteor.Modules.misc;

import meteordevelopment.meteorclient.events.render.GetFovEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import random.meteor.Main;

public class CustomFov extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> fovVal = sgGeneral.add(new IntSetting.Builder()
            .name("value")
            .description("fov-value")
            .defaultValue(110)
            .min(1)
            .sliderMax(180)
            .build()
    );
    private final Setting<Boolean> overideNew = sgGeneral.add(new BoolSetting.Builder()
            .name("override-new")
            .description("changes back to ")
            .defaultValue(true)
            .build()
    );

    public CustomFov() {
        super(Main.MISC,"custom-fov","CLEANNN");
    }
    @EventHandler
    private void onFov(GetFovEvent event) {
        event.fov = fovVal.get();
        if(fovVal.wasChanged() && overideNew.get()){
            fovVal.set(fovVal.get());
        }
    }
}