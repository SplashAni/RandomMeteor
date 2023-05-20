package random.meteor.systems.modules.RM;

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

    public CustomFov() {
        super(Main.RM,"custom-fov","CLEANNN");
    }
    @EventHandler
    private void onFov(GetFovEvent event) {
        event.fov = fovVal.get();
        if(fovVal.wasChanged()){
            fovVal.set(fovVal.get());
        }
    }
}
