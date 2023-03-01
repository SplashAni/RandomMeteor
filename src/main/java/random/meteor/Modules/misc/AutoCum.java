package random.meteor.Modules.misc;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import random.meteor.Main;

public class AutoCum extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Integer> range = sgGeneral.add(new IntSetting.Builder()
            .name("range")
            .description("range to render cum")
            .defaultValue(15)
            .range(5, 30)
            .sliderMax(30)
            .build()
    );
    private final Setting<Boolean> ignoreSelf = sgGeneral.add(new BoolSetting.Builder()
            .name("ignore-self")
            .description("doesnt render you when u die")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> ignoreFriends = sgGeneral.add(new BoolSetting.Builder()
            .name("ignore-friends")
            .description("doesnt render friends")
            .defaultValue(false)
            .build()
    );


    public AutoCum() {
        super(Main.MISC, "auto-cum", "Renders a cum effect if an entity dies");
    }

    @EventHandler
    private void onTick() {

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (ignoreSelf.get() && player == mc.player) continue;
            if (Friends.get().isFriend(player) && ignoreFriends.get()) continue;
            if (PlayerUtils.isWithinCamera(player, range.get())) {
                if(player.getHealth() == 0){
                    Double x = player.getX();
                    Double y = player.getX();
                    Double z = player.getX();
                    mc.world.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y,z, 0, 0, 0);
                }
            }
        }
    }
}
