package random.meteor.systems.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.orbit.EventHandler;
import random.meteor.util.player.RotationType;
import random.meteor.util.player.RotationUtil;
import random.meteor.util.system.Category;
import random.meteor.util.system.Mod;

public class RotationTests extends Mod {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<RotationType> type = sgGeneral.add(new EnumSetting.Builder<RotationType>()
        .name("rot type")
        .defaultValue(RotationType.Client)
        .build()
    );
    private final Setting<Integer> pitch = sgGeneral.add(new IntSetting.Builder()
        .name("pitch")
        .defaultValue(20)
        .min(-180)
        .sliderMax(180)
        .build()
    );
    private final Setting<Integer> yaw = sgGeneral.add(new IntSetting.Builder()
        .name("yaw")
        .defaultValue(20)
        .min(-180)
        .sliderMax(180)
        .build()
    );
    public RotationTests() {
        super("rotation-test",Category.PVP);
    }
    @EventHandler
    public void onTick(TickEvent.Pre event){

        RotationUtil.rotate(type.get(), yaw
            .get(), pitch.get());
    }
}
