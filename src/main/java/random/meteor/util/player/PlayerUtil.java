package random.meteor.util.player;

import meteordevelopment.meteorclient.utils.world.BlockUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class PlayerUtil {

    private static BlockPos[] getOffsets(BlockPos pos) {
        return new BlockPos[]{pos.north(), pos.east(), pos.south(), pos.west()};
    }

    public static List<BlockPos> getNeighbours() {
        BlockPos playerPos = Objects.requireNonNull(mc.player).getBlockPos();
        int y = playerPos.getY();
        Box box = mc.player.getBoundingBox();

        List<BlockPos> neighbours = new ArrayList<>();

        for (int x = (int) Math.floor(box.minX); x <= (int) Math.floor(box.maxX); x++) {
            for (int z = (int) Math.floor(box.minZ); z <= (int) Math.floor(box.maxZ); z++) {
                BlockPos center = new BlockPos(x, y, z);
                addWithOffsets(neighbours, center);
            }
        }

        for (BlockPos feet : getFeet()) {
            if (BlockUtils.canPlace(feet)) neighbours.add(feet);
        }


        return neighbours.stream().distinct().toList();
    }

    private static void addWithOffsets(List<BlockPos> list, BlockPos pos) {
        if (BlockUtils.canPlace(pos)) list.add(pos);
        for (BlockPos offset : getOffsets(pos)) {
            if (BlockUtils.canPlace(offset)) list.add(offset);
        }
    }

    private static List<BlockPos> getFeet() { // this took way to long to figure out..
        List<BlockPos> feetBlocks = new ArrayList<>();

        int baseX = mc.player.getBlockX();
        int baseY = mc.player.getBlockY() - 1;
        int baseZ = mc.player.getBlockZ();

        feetBlocks.add(new BlockPos(baseX, baseY, baseZ));

        double offsetX = mc.player.getX() - baseX;
        double offsetZ = mc.player.getZ() - baseZ;

        int xOffset = (offsetX > 0.7) ? 1 : 0;
        int zOffset = (offsetZ > 0.7) ? 1 : 0;

        for (int xDifference = 0; xDifference <= xOffset; xDifference++) {
            for (int zDifference = 0; zDifference <= zOffset; zDifference++) {
                if (xDifference != 0 || zDifference != 0) {
                    feetBlocks.add(new BlockPos(baseX + xDifference, baseY, baseZ + zDifference));
                }
            }
        }

        return feetBlocks;

    }


}
