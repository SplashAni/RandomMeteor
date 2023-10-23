package random.meteor.systems.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.network.OnlinePlayers;
import meteordevelopment.orbit.EventHandler;
import random.meteor.Main;

public class MeteorSpoof extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<mode> m = sgGeneral.add(new EnumSetting.Builder<mode>()
            .name("mode")
            .defaultValue(mode.Join)
            .build()
    );
    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
            .name("delay")
            .defaultValue(5)
            .min(1)
            .sliderMax(20)
            .build()
    );
    public MeteorSpoof() {
        super(Main.RM,"meteor-spoof","increment or deincrement metoers website user count");
    }
    int ticks;
    @EventHandler
    public void onTick(TickEvent.Pre event){
        ticks++;
        if(delay.get() >= ticks) {
            switch (m.get()) {
                case Join -> OnlinePlayers.update();
                case Leave -> OnlinePlayers.leave();
            }
            ticks = 0;
        }
    }
    public enum mode{
        Leave,
        Join
    }
}
