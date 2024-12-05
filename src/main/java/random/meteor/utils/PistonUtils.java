package random.meteor.utils;

import meteordevelopment.meteorclient.utils.world.BlockUtils;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.*;
import java.util.stream.Collectors;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class PistonUtils {
    private List<BlockPos> offsets(BlockPos pos, Direction[] directions) {
        return Arrays.stream(directions)
            .map(pos::offset)
            .collect(Collectors.toList());
    }

    public Map<Direction, BlockPos> getValidPosition(PlayerEntity entity) {
        Map<Direction, BlockPos> placeablePositions = new EnumMap<>(Direction.class);

        offsets(entity.getBlockPos(), Direction.Type.HORIZONTAL.facingArray).stream().filter(offset
            -> (Utils.state(offset) == Blocks.OBSIDIAN || Utils.state(offset) == Blocks.BEDROCK) &&
            Objects.requireNonNull(mc.world).isAir(offset.up())).forEach(offset -> {
            Direction direction = Direction.fromVector(
                offset.getX() - entity.getBlockPos().getX(),
                offset.getY() - entity.getBlockPos().getY(),
                offset.getZ() - entity.getBlockPos().getZ()
            );

            BlockPos placeablePos = offset.offset(direction).up();
            if (BlockUtils.canPlace(placeablePos) && BlockUtils.canPlace(placeablePos.offset(direction))) {
                placeablePositions.put(direction, offset);
            }
        });
        return placeablePositions;
    }


}
