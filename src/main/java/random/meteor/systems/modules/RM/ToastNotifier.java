package random.meteor.systems.modules.RM;

import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import random.meteor.Main;

public class ToastNotifier extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public ToastNotifier() {
        super(Main.RM,"toast-notifier","yes");
    }
    @EventHandler
    public void onTick(){

    }
}
