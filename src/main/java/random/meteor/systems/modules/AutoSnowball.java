package random.meteor.systems.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import random.meteor.systems.Mod;

import java.util.Objects;

import static meteordevelopment.meteorclient.utils.entity.TargetUtils.getPlayerTarget;
import static meteordevelopment.meteorclient.utils.entity.TargetUtils.isBadTarget;

public class AutoSnowball extends Mod {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> range = sgGeneral.add(new IntSetting.Builder()
            .name("target-range")
            .description("Range to find target.")
            .defaultValue(6)
            .range(1, 6)
            .sliderMax(12)
            .build()
    );
    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
            .name("delay")
            .defaultValue(5)
            .range(1, 50)
            .sliderMax(50)
            .build()
    );
    private final Setting<Boolean> silent = sgGeneral.add(new BoolSetting.Builder()
            .name("silent-swap")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> swing = sgGeneral.add(new BoolSetting.Builder()
            .name("swing")
            .defaultValue(true)
            .build()
    );

    public AutoSnowball() {
        super("auto-snowball", "Shoots snowballs at enemies");
    }

    int ticks;

    @EventHandler
    public void onTick(TickEvent.Pre e) {

        FindItemResult snowball = InvUtils.findInHotbar(Items.SNOWBALL);

        if (!snowball.isHotbar()) return;

        PlayerEntity target = getPlayerTarget(range.get(), SortPriority.LowestDistance);

        if (isBadTarget(target, range.get()) || !Objects.requireNonNull(mc.player).canSee(target)) return;


        if (ticks > 0) {
            ticks--;
            return;
        }

        Rotations.rotate(
                Rotations.getYaw(target), Rotations.getPitch(target), () -> {

                    InvUtils.swap(snowball.slot(), silent.get());

                    mc.interactionManager.interactItem(mc.player, snowball.getHand());

                    if (swing.get()) mc.player.swingHand(snowball.getHand());

                    if (silent.get()) InvUtils.swapBack();
                }
        );

        ticks = delay.get();
    }
}
