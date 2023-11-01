package random.meteor.systems.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import random.meteor.Main;
import random.meteor.utils.PistonInfo;
import random.meteor.utils.Utils;

public class PistonAura extends Module {
    public PistonAura() {
        super(Main.RM, "piston-aura", "iced out");
    }

    @Override
    public void onActivate() {
        super.onActivate();
    }

    BlockPos pistonPos;
    PistonInfo pistonInfo;
    BaseInfo baseInfo;
    PlayerEntity target;

    @EventHandler
    public void onTick(TickEvent.Pre event) {


        for (Direction d : Direction.values()) {

            if (d == Direction.UP || d == Direction.DOWN) continue;
            Block state = Utils.state(target.getBlockPos().offset(d));

            if (state == Blocks.AIR) baseInfo = new BaseInfo(true);

            if (state == Blocks.OBSIDIAN || state == Blocks.BEDROCK) baseInfo = new BaseInfo(false);
        }

        if(baseInfo == null) return;

        

    }

    public record BaseInfo(boolean place) {

    }
}
