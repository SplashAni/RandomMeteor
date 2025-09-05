package random.meteor.util.world;
/*
 * Uses A* ALGORUHTNMM
 * https://www.baeldung.com/java-a-star-pathfinding kind of helped
 * */
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.*;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class PathFinder {


    public List<BlockPos> getPath(BlockPos target,int radius) {
        BlockPos start = getStartBlock(target, radius);
        if (start == null) return List.of();

        Queue<BlockPos> queue = new ArrayDeque<>();
        Map<BlockPos, BlockPos> cameFrom = new HashMap<>();
        Set<BlockPos> visited = new HashSet<>();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();

            if (current.equals(target)) return rebuildPatj(cameFrom, start, target);

            for (Direction dir : Direction.values()) {
                BlockPos neighbor = current.offset(dir);

                if (!visited.contains(neighbor) &&
                    isWithinCube(neighbor, target, radius) &&
                    (isAir(neighbor) || neighbor.equals(target))) {

                    visited.add(neighbor);
                    cameFrom.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }

        return List.of();
    }


    private List<BlockPos> rebuildPatj(Map<BlockPos, BlockPos> cameFrom, BlockPos start, BlockPos target) {
        List<BlockPos> path = new ArrayList<>();
        BlockPos current = target;

        while (!current.equals(start)) {
            path.add(current);
            current = cameFrom.get(current);
            if (current == null) break;
        }

        Collections.reverse(path);
        return path;
    }

    private boolean isAir(BlockPos pos) {
        return mc.world != null && mc.world.isAir(pos);
    }

    private boolean isWithinCube(BlockPos pos, BlockPos center, int radius) {
        return Math.abs(pos.getX() - center.getX()) <= radius &&
            Math.abs(pos.getY() - center.getY()) <= radius &&
            Math.abs(pos.getZ() - center.getZ()) <= radius;
    }


    private BlockPos getStartBlock(BlockPos center, int radius) {
        BlockPos.Mutable pos = new BlockPos.Mutable();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    pos.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
                    if (mc.world != null && !mc.world.isAir(pos)) {
                        return pos.toImmutable();
                    }
                }
            }
        }

        return null;
    }
}
