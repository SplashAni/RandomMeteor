package random.meteor.systems.modules.RM.PistonAura;

import meteordevelopment.meteorclient.utils.world.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import random.meteor.systems.modules.utils.PistonInfo;
import random.meteor.systems.modules.utils.Utils;

public class PistonPosition {
    boolean checkExisting;
    BlockPos pos;

    public PistonPosition(BlockPos pos,boolean checkExisting){
        this.checkExisting = checkExisting;
        this.pos = pos;
    }

    public Position calculated(){
        BaseInfo bp = basePosition();
        PistonInfo pi = pi();

        if(bp == null|| pi == null) return null;

        return new Position(checkExisting,pi,bp.pos,bp.create);
    }

    public PistonInfo pi() {
        BaseInfo bp = basePosition();

        if (bp == null) return null;

        BlockPos offsetPos;

        switch (bp.d) {
            case EAST -> offsetPos = bp.pos.add(1, 0, 0);
            case NORTH -> offsetPos = bp.pos.add(0, 0, -1);
            case SOUTH -> offsetPos = bp.pos.add(0, 0, 1);
            case WEST -> offsetPos = bp.pos.add(-1, 0, 0);
            default -> {
                return null;
            }
        }

        return new PistonInfo(offsetPos, bp.d);
    }

    public BaseInfo basePosition() {
        for (Direction d : Direction.values()) {
            if (d == Direction.UP || d == Direction.DOWN) continue;
            BlockPos offsetPos = pos.offset(d);
            if (BlockUtils.canPlace(offsetPos)) {
                return new BaseInfo(offsetPos, Utils.isBlock(offsetPos), d);
            }
        }
        return null;
    }



    public boolean isValidPiston(BlockPos p) {

        if (checkExisting) {
            Block block = Utils.state(p);
            return block == Blocks.PISTON || block == Blocks.STICKY_PISTON;
        }

        return BlockUtils.canPlace(p);
    }
    public record Position(boolean existing, PistonInfo pi, BlockPos base, boolean createBase){}
    public record BaseInfo(BlockPos pos, boolean create, Direction d){}
}

