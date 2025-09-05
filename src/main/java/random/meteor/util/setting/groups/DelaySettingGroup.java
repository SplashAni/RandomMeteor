package random.meteor.util.setting.groups;

import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import random.meteor.util.setting.DefaultSettingGroup;
import random.meteor.util.system.Mod;

public class DelaySettingGroup extends DefaultSettingGroup { // todo : seconds mode too

    Setting<Integer> randomDelay;
    Setting<Double> delay;

    public DelaySettingGroup(Mod mod) {
        super(mod);
        setName("Delay Settings");

        delay = getSettingGroup().add(new DoubleSetting.Builder()
            .name("delay")
            .description("Delay, in ticks, between interactions.")
            .defaultValue(1.0)
            .min(0)
            .build()
        );

        Setting<Boolean> randomOffset = getSettingGroup().add(new BoolSetting.Builder()
            .name("random-offset")
            .defaultValue(false)
            .build()
        );
        randomDelay = getSettingGroup().add(new IntSetting.Builder()
            .name("walls-range")
            .description("0 to specific number")
            .defaultValue(2)
            .min(0)
            .visible(randomOffset::get)
            .build()
        );
    }
}
