package random.meteor.util.setting.groups;

import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import random.meteor.util.setting.DefaultSettingGroup;
import random.meteor.util.system.Mod;

public class DelaySettingGroup extends DefaultSettingGroup { // todo : seconds mode too

    public Setting<Double> retryDelay;
    public Setting<Double> delay;
    public Setting<Double> customCounterValue;
    public Setting<Boolean> customCounter;

    public DelaySettingGroup(Mod mod) {
        super(mod);
        setName("Delay Settings");



        delay = getSettingGroup().add(new DoubleSetting.Builder()
            .name("interaction-delay")
            .description("Delay, in ticks, between interactions.")
            .defaultValue(2.0)
            .min(0)
            .build()
        );


        customCounter = getSettingGroup().add(new BoolSetting.Builder()
            .name("custom-counter")
            .description("Weather to count down 1 tick or you can define a more specific value")
            .defaultValue(false)
            .build()
        );
        customCounterValue = getSettingGroup().add(new DoubleSetting.Builder()
            .name("custom-counter-value")
            .description("Specifically indicate how fast the delay occurs")
            .defaultValue(1.5)
            .min(0)
            .visible(customCounter::get)
            .build()
        );
        retryDelay = getSettingGroup().add(new DoubleSetting.Builder()
            .name("retry-delay")
            .description("Delay, in ticks, between interactions.")
            .defaultValue(5.0)
            .min(0)
            .build()
        );

    }
}
