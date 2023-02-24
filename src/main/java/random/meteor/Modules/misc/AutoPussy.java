package random.meteor.Modules.misc;

import meteordevelopment.meteorclient.events.entity.EntityAddedEvent;
import meteordevelopment.meteorclient.events.entity.EntityRemovedEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.player.PlayerEntity;
import random.meteor.Main;

public class AutoPussy extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> ignoreFriends = sgGeneral.add(new BoolSetting.Builder()
            .name("ignore-friends")
            .description("Useful for trust issues yk..")
            .defaultValue(false)
            .build()
    );
    private final Setting<Integer> health = sgGeneral.add(new IntSetting.Builder()
            .name("health")
            .description("Health to be at before running..")
            .defaultValue(16)
            .range(1, 36)
            .sliderMax(36)
            .build()
    );
    private final Setting<Integer> distance = sgGeneral.add(new IntSetting.Builder()
            .name("run-distance")
            .description("X,Y distance to travel")
            .defaultValue(35)
            .range(10, 150)
            .sliderMax(150)
            .build()
    );

    public AutoPussy() {
        super(Main.MISC, "auto-pussy", "Runs away from nearby players");

    }

    @EventHandler
    private void onEntityAdded(EntityAddedEvent event) {
        if (!event.entity.getUuid().equals(mc.player.getUuid())) {
            if (event.entity instanceof PlayerEntity) {
                if ((ignoreFriends.get() || !Friends.get().isFriend(((PlayerEntity) event.entity)))) {
                    run(); //sexy
                }
            }
        }
    }

    @EventHandler
    private void onEntityRemoved(EntityRemovedEvent event) {
        if (!event.entity.getUuid().equals(mc.player.getUuid())) {
            if (event.entity instanceof PlayerEntity) {
                if ((!ignoreFriends.get() || !Friends.get().isFriend(((PlayerEntity) event.entity)))) {
                    ChatUtils.sendPlayerMsg("#stop");
                }
            }

        }
    }
    private void run() {
        PlayerEntity player = mc.player;
        float x = (float) player.getX();
        float z = (float) player.getZ();
        if (player.getHealth() < health.get()) {
            ChatUtils.sendPlayerMsg("#goto " + x + distance.get() + " ~ " + z + distance.get());
        }
    }
}