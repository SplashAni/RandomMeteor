package random.meteor.util.setting.groups;

import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import random.meteor.util.setting.GlobalSettingGroup;
import random.meteor.util.setting.modes.SwapMode;
import random.meteor.util.system.Mod;

public class SwapSettingGroup extends GlobalSettingGroup {
    public Setting<Double> delay;
    public Setting<SwapMode> swapMode;

    public SwapSettingGroup(Mod mod) {
        super(mod, "Swap");

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
