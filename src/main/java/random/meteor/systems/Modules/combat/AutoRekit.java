package random.meteor.systems.Modules.combat;

import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import random.meteor.Main;

import static random.meteor.systems.Modules.Utils.CombatUtils.getPlayer;

public class AutoRekit extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder().name("Delay").sliderRange(1,90).defaultValue(20).build());

    private final Setting<String> kitcommand = sgGeneral.add(new StringSetting.Builder()
        .name("kit-command")
        .description("kit command")
        .defaultValue("/kit")
        .build()
    );
    private final Setting<String> kit = sgGeneral.add(new StringSetting.Builder()
            .name("kit-name")
            .description("kits name")
            .defaultValue("splash")
            .build()
    );

    public AutoRekit() {

        super(Main.MISC,"Auto-kit","rekits on death");
    }

    public static String queue;
    public static int p = 0;
    @EventHandler
    private void onTick(){
        p++;
        if(getPlayer().getHealth() == 0 && !getPlayer().isAlive()){
            queue = (kitcommand.get() + " " +kit.get());
        }
       if (p > delay.get()){p = 0;
       if (queue != ""){
           ChatUtils.sendPlayerMsg(queue);
           queue = "";
       }
       }
    }
}
