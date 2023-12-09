package random.meteor.systems.modules;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import random.meteor.Main;
import random.meteor.utils.Utils;

public class PistonAura extends Module {
    public PistonAura() {
        super(Main.RM, "piston-aura", "iced out");
    }

    CalculatedPos calculated = null;

    @EventHandler
    public void onTick(TickEvent.Pre event) {

        for (Direction direction : Direction.values()) {
            if (direction == Direction.UP || direction == Direction.DOWN) continue;

            BlockPos offset = mc.player.getBlockPos().offset(direction);

            if (!BlockUtils.canPlace(offset) && Utils.state(offset) != Blocks.OBSIDIAN) continue;

            BlockPos pistonPos = offset.offset(direction).up();

            if (!BlockUtils.canPlace(pistonPos)) continue;

            BlockPos redstonePos = null;

            for (Direction d : Direction.values()) {
                if (BlockUtils.canPlace(pistonPos.offset(d))) redstonePos = pistonPos.offset(d);
                break;
            }

            if (redstonePos == null) continue;

            calculated = new CalculatedPos(
                    pistonPos, offset, redstonePos
            );
            break;

        }

    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        if (calculated == null) return;
        if(!calculated.isValid()) return;
        event.renderer.box(calculated.basePos, Color.BLUE.a(120), Color.BLUE.toSetting(), ShapeMode.Sides, 0);
        event.renderer.box(calculated.pistonPos, Color.YELLOW.a(120), Color.BLUE.toSetting(), ShapeMode.Sides, 0);
        event.renderer.box(calculated.redstonePos, Color.RED.a(120), Color.BLUE.toSetting(), ShapeMode.Sides, 0);

    }

    public record CalculatedPos(BlockPos pistonPos, BlockPos basePos, BlockPos redstonePos) {
        public boolean isValid() {
            return  pistonPos != null && basePos != null && redstonePos != null;
        }
    }
}