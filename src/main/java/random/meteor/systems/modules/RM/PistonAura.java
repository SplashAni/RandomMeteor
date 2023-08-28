package random.meteor.systems.modules.RM;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import random.meteor.Main;
import random.meteor.systems.modules.utils.Utils;

public class PistonAura extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Integer> range = sgGeneral.add(new IntSetting.Builder()
        .name("target-range")
        .description("Range to find target.")
        .defaultValue(12)
        .range(1, 12)
        .sliderMax(12)
        .build()
    );
    private final Setting<Boolean> debug = sgGeneral.add(new BoolSetting.Builder()
        .name("debug")
        .defaultValue(true)
        .build()
    );

    public PistonAura() {
        super(Main.RM, "piston-aura", "");
    }

    int ticks;
    PlayerEntity target;

    @Override
    public void onActivate() {

        super.onActivate();
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {


        FindItemResult torch = InvUtils.findInHotbar(Items.REDSTONE_TORCH);

        if (torch.isHotbar()) {
            BlockPos p = mc.player.getBlockPos().up().add(1, 0, 0);
            BlockUtils.place(torchPos(p), torch, 60, true);
        }
    }

    public BlockPos torchPos(BlockPos pos) {
        FindItemResult blockToPlace = InvUtils.findInHotbar(Items.OBSIDIAN);
        BlockPos blockPosToPlace = null;

        for (Direction d : Direction.values()) {
            if (d == Direction.UP || d == Direction.DOWN) continue;

            BlockPos p = pos.down().offset(d);

            if (Utils.isBlock(p)) {
                blockPosToPlace = p.up();
            } else {
                blockPosToPlace = p;
                if (BlockUtils.place(p, blockToPlace, true, 60, true)) {
                }
            }
        }

        return blockPosToPlace;
    }
}
