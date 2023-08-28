package random.meteor.systems.modules.RM.PistonAura;

import meteordevelopment.meteorclient.utils.world.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import random.meteor.systems.modules.RM.Blockpos;
import random.meteor.systems.modules.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static meteordevelopment.meteorclient.utils.world.BlockUtils.canPlace;

public class PistonPositions {
    boolean checkExisting;
    BlockPos pos;

    public Positions pistonPostion(BlockPos pos, boolean checkExisting) {
        this.checkExisting = checkExisting;
        this.pos = pos;

        return
    }

    public BlockPos calcPosition() {

    }

    public BlockPos poses() {
        List<BlockPos> p = new ArrayList<>();

        BlockPos[] offsets = {
            new BlockPos(0, 0, -2), // north
            new BlockPos(0, 0, 2),  // south
            new BlockPos(-2, 0, 0), // west
            new BlockPos(2, 0, 0)    // east
        };

        for (BlockPos poses : offsets) {
            if (isValidPiston(pos)) p.add(poses);
        }

        if (p.isEmpty() || p.size() > 2) return null;

        return p.get(0);
    }

    public boolean isValidPiston(BlockPos p) {

        if (checkExisting) {
            Block block = Utils.state(p);
            return block == Blocks.PISTON || block == Blocks.STICKY_PISTON;
        }

        return BlockUtils.canPlace(p);
    }
    public record Positions(BlockPos obsidianPos,BlockPos pistonPos,BlockPos redstonePos){

    }
}

