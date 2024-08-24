package random.meteor.systems.modules;

import meteordevelopment.meteorclient.events.render.GetFovEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.orbit.EventHandler;
import random.meteor.systems.Mod;

public class CustomFov extends Mod {
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
        super("custom-fov","Allows you to exceed minecraft's fov limit");
    }
    @EventHandler
    private void onFov(GetFovEvent event) {
        event.fov = fovVal.get();
        if(fovVal.wasChanged()){
            fovVal.set(fovVal.get());
        }
    }
}
