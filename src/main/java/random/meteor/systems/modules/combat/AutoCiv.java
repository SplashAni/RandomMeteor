package random.meteor.systems.modules.combat;

import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import random.meteor.Main;
import random.meteor.systems.modules.utils.CombatUtils;

import static random.meteor.systems.modules.utils.CombatUtils.direction;


public class AutoCiv extends Module {
    BlockPos crystalPos;
    private EndCrystalEntity crystal;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> ignoreFriends = sgGeneral.add(new BoolSetting.Builder()
            .name("ignore-friends")
            .description("")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> ignoreSelf = sgGeneral.add(new BoolSetting.Builder()
            .name("ignore-self")
            .description("")
            .defaultValue(true)
            .build()
    );
    private final Setting<Integer> range = sgGeneral.add(new IntSetting.Builder()
            .name("range")
            .description("range to place")
            .defaultValue(6)
            .range(1,7)
            .sliderMax(7)
            .build()
    );

    public AutoCiv() {
        super(Main.COMBAT,"auto-civ","");
    }
    @EventHandler
    public void onTick(){
        for(PlayerEntity target : mc.world.getPlayers()){
            if (ignoreSelf.get() && target == mc.player) continue;
            if (Friends.get().isFriend(target) && ignoreFriends.get()) continue;

            double distance = target.distanceTo(mc.player);
            if (distance <= range.get()) {
                BlockPos pos = target.getBlockPos();
                Direction dir = direction(target);
                BlockPos obbyPos = pos.offset(dir);

            }
        }
    }
    public void placeObby(BlockPos pos,Direction dir){

    }
}
