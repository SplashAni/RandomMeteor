package random.meteor.util.world;

import random.meteor.util.setting.groups.DelaySettingGroup;
import random.meteor.util.system.Mod;

public class TimerUtil {
    public static double applyDelay(BlockPlaceResult placeResult, DelaySettingGroup delaySettingGroup) {
        switch (placeResult) {
            case WaitForSupport, Success -> {
                return delaySettingGroup.delay.get();
            }
            case Fail -> {
                return delaySettingGroup.retryDelay.get();
            }
        }
        return 0;
    }

    public static double updateTimer(double ticks, DelaySettingGroup delaySettingGroup, Mod mod) {
        double decrement = delaySettingGroup.customCounter.get()
            ? delaySettingGroup.delay.get()
            : 1.0;

        if (mod.debug.get() && mod.debugTimer.get()) {
            String action = (ticks - decrement) <= 0 ? "now." : ticks - decrement + "ticks.";
            mod.debug("Doing next action " + action, mod.debugTimer.get());
        }

        return ticks - decrement;
    }

}
