package random.meteor.util.setting.groups;

import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import random.meteor.util.setting.DefaultSettingGroup;
import random.meteor.util.setting.EventType;
import random.meteor.util.setting.modes.SwapMode;
import random.meteor.util.system.Mod;

public class SwapSettingGroup extends DefaultSettingGroup {
    public Setting<Double> delay;
    public Setting<SwapMode> swapMode;

    public SwapSettingGroup(Mod mod) {
        super(mod);

        delay = getSettingGroup().add(new DoubleSetting.Builder()
            .name("swap-delay")
            .description("Delay to swap.")
            .defaultValue(0)
            .min(0)
            .build()
        );
        swapMode = getSettingGroup().add(new EnumSetting.Builder<SwapMode>()
            .name("swing-event")
            .description("Weather to swing before or after interaction.")
            .defaultValue(SwapMode.Silent)
            .build()
        );
    }



}
