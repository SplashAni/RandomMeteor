package random.meteor.systems.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import net.minecraft.util.math.BlockPos;
import random.meteor.util.player.PlayerUtil;
import random.meteor.util.setting.groups.*;
import random.meteor.util.system.Category;
import random.meteor.util.system.Mod;
import random.meteor.util.world.BlockUtil;
import random.meteor.util.world.TimerUtil;

import java.util.ArrayList;
import java.util.List;

public class Feetrap extends Mod {

    double ticks;
    List<BlockPos> poses = new ArrayList<>();

    public Feetrap() {
        super("feet-trap", Category.PVP);

        register(
            PlaceSettingGroup.class,
            CenterSettingGroup.class,
            RangeSettingGroup.class,
            DelaySettingGroup.class,
            SwapSettingGroup.class,
            SwingSettingGroup.class,
            RenderSettingGroup.class
        );

    }


    @Override
    public void onPreTick(TickEvent.Pre event) {


        poses = PlayerUtil.getNeighbours();
        if (poses.isEmpty()) return;

        if (ticks > 0) {
            ticks = TimerUtil.updateTimer(ticks, getDelaySettings(), this);
            return;
        }


        if (getPlaceSettings().instant.get()) {
            for (BlockPos pose : poses) {
                ticks = TimerUtil.applyDelay(
                    BlockUtil.place(pose, getPlaceSettings(), getRangeSettings(), getSwapSettings(), getSwingSettings(), getCenterSettings(), this),
                    getDelaySettings()
                );
            }
        } else {
            ticks = TimerUtil.applyDelay(
                BlockUtil.place(poses.getFirst(), getPlaceSettings(), getRangeSettings(), getSwapSettings(), getSwingSettings(), getCenterSettings(), this),
                getDelaySettings()
            );
        }
        super.onPreTick(event);
    }


}
