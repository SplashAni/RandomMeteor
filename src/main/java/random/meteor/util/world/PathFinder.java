package random.meteor.util.world;

/*
* Uses A* ALGORUHTNMM
* https://www.baeldung.com/java-a-star-pathfinding kind of helped
* */

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.*;
import java.util.stream.Collectors;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class PathFinder {

    public List<BlockPos> getPath(BlockPos target, int searchRadius) {

        BlockPos start = getStartBlock(target, searchRadius);

        if (start == null) return Collections.emptyList();

        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingDouble(score -> score.score));

        HashSet<BlockPos> notDiscovered = new HashSet<>();
        HashSet<BlockPos> discovered = new HashSet<>();

        Map<BlockPos, BlockPos> calculated = new HashMap<>();
        Map<BlockPos, Double> scores = new HashMap<>();

        scores.put(start, 0.0);
        queue.add(new Node(start, start.getSquaredDistance(target)));
        notDiscovered.add(start);

        while (!queue.isEmpty()) {

            Node currentNode = queue.poll();

            BlockPos current = currentNode.pos;
            notDiscovered.remove(current);

            if (current.equals(target)) return redoPath(calculated, current);

            discovered.add(current);

            for (BlockPos neighbor : getOffsets(current)) {

                if (!Objects.requireNonNull(mc.world).isAir(neighbor) && !neighbor.equals(target)) continue;

                if (discovered.contains(neighbor)) continue;

                double pathCost = scores.getOrDefault(current, Double.MAX_VALUE) + 1; // okay we can work on this??? maybe more stricter to prevent that snap;py bug

                if (pathCost < scores.getOrDefault(neighbor, Double.MAX_VALUE)) {

                    calculated.put(neighbor, current);
                    scores.put(neighbor, pathCost);
                    double newScore = pathCost + neighbor.getSquaredDistance(target);

                    if (!notDiscovered.contains(neighbor)) {
                        queue.add(new Node(neighbor, newScore));
                        notDiscovered.add(neighbor);
                    }

                }
            }
        }

        return Collections.emptyList();
    }

    private List<BlockPos> redoPath(Map<BlockPos, BlockPos> cameFrom, BlockPos current) {
        List<BlockPos> newPath = new ArrayList<>();
        newPath.add(current);

        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            newPath.add(current);
        }

        Collections.reverse(newPath);
        return newPath;
    }

    private BlockPos getStartBlock(BlockPos to, int radius) {
        BlockPos.Mutable pos = new BlockPos.Mutable();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    pos.set(to.getX() + x, to.getY() + y, to.getZ() + z);
                    if (mc.world != null && !mc.world.isAir(pos)) return pos.toImmutable();
                }
            }
        }
        return null;
    }

    private List<BlockPos> getOffsets(BlockPos pos) {
        return Arrays.stream(Direction.values()).map(pos::offset).collect(Collectors.toList());
    }

    private record Node(BlockPos pos, double score) {

    }
}
