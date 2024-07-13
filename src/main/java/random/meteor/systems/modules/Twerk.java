package random.meteor.systems.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.orbit.EventHandler;
import random.meteor.systems.Mod;

import java.util.Objects;

public class Twerk extends Mod {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("twerk-delay")
        .description("")
        .defaultValue(10)
        .range(1, 60)
        .sliderMax(60)
        .build()
    );
    int ticks;

    public Twerk() {
        super("twerk", "");
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (ticks > 0) {
            ticks--;
            return;
        }

        mc.options.sneakKey.setPressed(!Objects.requireNonNull(mc.player).isSneaking());

        ticks = delay.get();
    }
}
