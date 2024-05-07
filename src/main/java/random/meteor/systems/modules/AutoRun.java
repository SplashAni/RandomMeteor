package random.meteor.systems.modules;

import meteordevelopment.meteorclient.events.entity.EntityAddedEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
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
import random.meteor.systems.Mod;

public class AutoRun extends Mod {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> ignoreFriends = sgGeneral.add(new BoolSetting.Builder()
            .name("ignore-friends")
            .defaultValue(false)
            .build()
    );

    private final Setting<Integer> xIncrement = sgGeneral.add(new IntSetting.Builder()
            .name("x-increment")
            .defaultValue(10)
            .range(1, 50)
            .sliderMax(50)
            .build()
    );

    private final Setting<Integer> yIncrement = sgGeneral.add(new IntSetting.Builder()
            .name("y-increment")
            .defaultValue(0)
            .range(1, 50)
            .sliderMax(50)
            .build()
    );

    private final Setting<Integer> zIncrement = sgGeneral.add(new IntSetting.Builder()
            .name("z-increment")
            .defaultValue(10)
            .range(1, 50)
            .sliderMax(50)
            .build()
    );

    private boolean running;
    private PlayerEntity entity;

    public AutoRun() {
        super( "auto-run", "Automatically runs when a player enters you visual range (requires baritone)");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (running) {
            double targetX = entity.getX() + xIncrement.get();
            double targetY = entity.getY() + yIncrement.get();
            double targetZ = entity.getZ() + zIncrement.get();

            String coords = String.valueOf(targetX).concat(" ".concat(String.valueOf(targetY).concat(" ".concat(String.valueOf(targetZ)))));
            ChatUtils.sendPlayerMsg("#goto " + coords);
            running = false;
        }
    }

    @EventHandler
    private void onEntity(EntityAddedEvent event) {
        if (event.entity instanceof PlayerEntity) {
            if (Friends.get().isFriend((PlayerEntity) event.entity) && ignoreFriends.get()) return;
            entity = (PlayerEntity) event.entity;
            running = true;
        }
    }
}
