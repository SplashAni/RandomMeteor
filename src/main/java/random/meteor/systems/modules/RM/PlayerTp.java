package random.meteor.systems.modules.RM;

import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.entity.player.PlayerEntity;
import random.meteor.Main;
import random.meteor.systems.modules.utils.Utils;

public class PlayerTp extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> range = sgGeneral.add(new IntSetting.Builder()
        .name("target-range")
        .description("Range to find target.")
        .defaultValue(16)
        .range(1, 16)
        .sliderMax(16)
        .build()
    );
    private final Setting<Boolean> ignore = sgGeneral.add(new BoolSetting.Builder()
        .name("ignore-friends")
        .defaultValue(true)
        .build()
    );

    public PlayerTp() {
        super(Main.RM,"player-tp","tps to nearest player");
    }


    @Override
    public void onActivate() {
        for(PlayerEntity players : mc.world.getPlayers()){
            if(players == mc.player) continue;

            if(Friends.get().isFriend(players) && ignore.get()) continue;

            if(!players.isInRange(mc.player,range.get())) continue;


            mc.player.setPosition(
                players.getX(),
                players.getY(),
                players.getZ()
            );

            /* update position u silly*/
            Utils.updatePosition();
            toggle();
        }
        super.onActivate();
    }
}
