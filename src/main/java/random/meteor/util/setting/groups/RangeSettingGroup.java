package random.meteor.util.setting.groups;

import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import random.meteor.util.setting.DefaultSettingGroup;
import random.meteor.util.system.Mod;

public class RangeSettingGroup extends DefaultSettingGroup {

    public Setting<Double> wallsRange;
    public Setting<Double> range;
    public Setting<Boolean> eyeOnly;

    public RangeSettingGroup(Mod mod) {
        super(mod);


        range = getSettingGroup().add(new DoubleSetting.Builder()
            .name("place-range")
            .description("Range in which to interact.")
            .defaultValue(4.5)
            .min(0)
            .sliderMax(6)
            .build()
        );

        wallsRange = getSettingGroup().add(new DoubleSetting.Builder()
            .name("walls-range")
            .description("Range in which to interact when behind blocks.")
            .defaultValue(4.5)
            .min(0)
            .sliderMax(6)
            .build()
        );
        eyeOnly = getSettingGroup().add(new BoolSetting.Builder()
            .name("eye-only")
            .description("Only places where the player can see")
            .defaultValue(false)
            .build()
        );
    }
}
