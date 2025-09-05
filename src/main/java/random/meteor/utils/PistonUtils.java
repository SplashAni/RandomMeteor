package random.meteor.utils;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import random.meteor.systems.modules.PistonAura;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static meteordevelopment.meteorclient.MeteorClient.mc;
import static meteordevelopment.meteorclient.utils.world.BlockUtils.interact;

public class PistonUtils {
    private List<Pair<BlockPos, Direction>> offsets(BlockPos pos, Direction[] directions) {
        return Arrays.stream(directions)
            .map(direction -> new Pair<>(pos.offset(direction), direction))
            .collect(Collectors.toList());
    }

    public Map<Direction, BlockPos> getValidPosition(PlayerEntity entity, boolean top, PistonAura.Mode mode) {
     return null;
    }

    public boolean canActivate(BlockPos pos, Direction direction, PistonAura.Mode mode) {
        return switch (mode) {
            case Button -> getButtonPos(pos, direction) != null;
            case RedstoneBlock -> BlockUtils.canPlace(pos.offset(direction));
        };
    }

    public BlockPos getButtonPos(BlockPos pos, Direction direction) {
        List<Pair<BlockPos, Direction>> offsetList = offsets(pos.down(), Direction.values());

        AtomicBoolean canButton = new AtomicBoolean(false);
        AtomicReference<BlockPos> torchPos = new AtomicReference<>(null);

        offsetList.forEach(blockPosDirectionPair -> {
            if (!Objects.requireNonNull(mc.world).getBlockState(blockPosDirectionPair.getLeft()).isAir() &&
                BlockUtils.canPlace(blockPosDirectionPair.getLeft().up(1))
                && !(blockPosDirectionPair.getLeft().equals(pos.down().offset(direction.getOpposite())))) {
                canButton.set(true);
                torchPos.set(blockPosDirectionPair.getLeft());
            }
        });
        return canButton.get() ? torchPos.get() : null;
    }

    public void placeTorch(BlockPos pos, FindItemResult torch, boolean silent) {
        InvUtils.swap(torch.slot(), silent);

        interact(new BlockHitResult(pos.down().toCenterPos(),Direction.UP,pos,true), Hand.MAIN_HAND,true);

        if (silent) InvUtils.swapBack();
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

    public boolean isPiston(BlockPos pos) {
        return Utils.state(pos) == Blocks.PISTON || Utils.state(pos) == Blocks.STICKY_PISTON;
    }
}
