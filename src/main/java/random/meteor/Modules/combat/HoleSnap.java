package random.meteor.Modules.combat;

import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import net.minecraft.util.math.BlockPos;
import random.meteor.Main;

import java.util.ArrayList;
import java.util.List;

public class HoleSnap extends Module {

    private final List<BlockPos> holes = new ArrayList<>();

    public HoleSnap() {
        super(Main.COMBAT, "hole-snap", "Best rewrite");
    }

    @Override
    public void onActivate() {
        if (PlayerUtils.isInHole(true)) {
            toggle();
        } else {
            BlockPos playerPos = mc.player.getBlockPos();
            BlockPos nearestHole = findNearestHole(playerPos);
            if (nearestHole != null) {
                info(String.valueOf(nearestHole.getX()  + nearestHole.getY()  + nearestHole.getZ()));
            } else {
                info("No holes found toggling...");
                this.toggle();
            }
        }
        super.onActivate();
    }

    private BlockPos findNearestHole(BlockPos playerPos) {
        BlockPos nearestHole = null;
        double nearestDistance = Double.MAX_VALUE;
        for (BlockPos holePos : holes) {
            double distance = playerPos.getSquaredDistance(holePos);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestHole = holePos;
            }
        }
        return nearestHole;
    }
}