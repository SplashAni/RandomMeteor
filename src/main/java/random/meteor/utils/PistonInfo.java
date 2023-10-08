package random.meteor.utils;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class PistonInfo {/*made as a class for pisaton aura (:*/
    Direction direction;
    BlockPos pos;
    public PistonInfo(BlockPos pos, Direction direction){
        this.pos = pos;
        this.direction = direction;
    }

    public Direction direction() {
        return direction;
    }

    public BlockPos pos() {
        return pos;
    }
}
