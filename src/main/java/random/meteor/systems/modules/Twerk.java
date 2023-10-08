package random.meteor.systems.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import random.meteor.Main;

public class Twerk extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
            .name("twerk-delay")
            .description("")
            .defaultValue(10)
            .range(1,120)
            .sliderMax(120)
            .build()
    );
    int ticks;
    public Twerk() {
        super(Main.RM,"twerk","");
    }
    @EventHandler
    public void onTick(TickEvent.Pre event){
        if(ticks > 0 ){ /*deincrementing hits different :wear:*/
            ticks--;
            return;
        }

        mc.options.sneakKey.setPressed(!mc.player.isSneaking());
        ticks = delay.get();

    }
}
