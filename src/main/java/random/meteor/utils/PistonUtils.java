package random.meteor.utils;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
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
            BlockUtils.canPlace(offset.up())).forEach(offset -> {
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

    public void renderPistonHead(Render3DEvent event, BlockPos pistonPos,
                                 Color side, Color lines, ShapeMode shapeMode) {

        BlockPos bp;
        if (Utils.state(pistonPos) == Blocks.PISTON || Utils.state(pistonPos) == Blocks.STICKY_PISTON) {
            if (mc.world.getBlockState(pistonPos).get(PistonBlock.EXTENDED)) {
                Direction facing = mc.world.getBlockState(pistonPos).get(PistonBlock.FACING);
                if (Utils.state(pistonPos.offset(facing)) == Blocks.PISTON_HEAD) bp = pistonPos.offset(facing);
                else {
                    bp = null;
                }
            } else {
                bp = null;
            }
        } else {
            return;
        }

        if (bp == null) return;

        VoxelShape shape = mc.world.getBlockState(bp).getOutlineShape(mc.world, bp);

        if (shape.isEmpty()) return;

        if (shapeMode == ShapeMode.Both || shapeMode == ShapeMode.Lines) {
            shape.forEachEdge((minX, minY, minZ, maxX, maxY, maxZ) -> {
                event.renderer.line(bp.getX() + minX, bp.getY() + minY, bp.getZ() + minZ, bp.getX() + maxX, bp.getY() + maxY, bp.getZ() + maxZ, lines);
            });
        }

        if (shapeMode == ShapeMode.Both || shapeMode == ShapeMode.Sides) {
            for (Box box : shape.getBoundingBoxes()) {

                event.renderer.box(bp.getX() +
                        box.minX, bp.getY() + box.minY,
                    bp.getZ() + box.minZ, bp.getX() + box.maxX,
                    bp.getY() + box.maxY, bp.getZ() +
                        box.maxZ, side, lines, shapeMode,
                    0);

            }
        }
    }

}
