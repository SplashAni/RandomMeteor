package random.meteor.systems.modules.RM;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
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

        ticks++;
        if(ticks >= delay.get()) {
            mc.options.sneakKey.setPressed(!mc.player.isSneaking()); // the most simplest way tbh
            ticks = 0;
        }
    }
}
