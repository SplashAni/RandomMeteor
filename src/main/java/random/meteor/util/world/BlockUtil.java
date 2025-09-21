package random.meteor.util.world;

import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import random.meteor.manager.Manager;
import random.meteor.util.player.PlayerUtil;
import random.meteor.util.setting.groups.*;
import random.meteor.util.setting.modes.CenterTiming;
import random.meteor.util.setting.modes.SwapMode;
import random.meteor.util.setting.modes.SwingMode;
import random.meteor.util.setting.modes.WorldHeight;
import random.meteor.util.system.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static meteordevelopment.meteorclient.MeteorClient.mc;


public class BlockUtil extends Manager { // make a class handler to sqeudle runnabled to run in the world

    private static PathFinder pathFinder;
    private static List<BlockPos> paths;
    private static BlockPos lastTarget;


    @Override
    public void onInitialize() {
        pathFinder = new PathFinder();
        paths = new ArrayList<>();
        lastTarget = null;
    }

    boolean outSideBorder = false;
    WorldHeight worldHeight = WorldHeight.New; // create placeholders for now


    public static BlockPlaceResult place(BlockPos blockPos, PlaceSettingGroup placeSettings, RangeSettingGroup rangeSettings,
                                         SwapSettingGroup swapSettings, SwingSettingGroup swingSettings, CenterSettingGroup centerSettingGroup, Mod module) {

        if (!BlockUtils.canPlace(blockPos, placeSettings.checkEntities.get())) return BlockPlaceResult.Fail;
        if (!PlayerUtils.isWithin(blockPos, rangeSettings.range.get())) return BlockPlaceResult.Fail;
        if (rangeSettings.eyeOnly.get() && !isWithinWalls(blockPos, rangeSettings.wallsRange.get()))
            return BlockPlaceResult.Fail;

        FindItemResult block = InvUtils.findInHotbar(itemStack ->
            placeSettings.blocks.get().contains(Block.getBlockFromItem(itemStack.getItem())));

        if (!block.found()) {
            module.debug("No blocks found...", module.debugLogic.get());
            return BlockPlaceResult.Fail;
        }

        SwapMode swapMode = swapSettings.swapMode.get();

        ResultStatus resultStatus = getBlockHitResult(blockPos, swapMode, block, placeSettings, centerSettingGroup, swingSettings);

        boolean action = module.debugActions.get();
        if (resultStatus == null) {
            module.debug("Unable to find a neighbour to place the block onto.", action);
            return BlockPlaceResult.Fail;
        }

        if (resultStatus.status == -1) {
            module.debug("Unable to place support block", action);
            return BlockPlaceResult.WaitForSupport;
        }

        if (resultStatus.status == 0) {
            module.debug("Successfully placed a support block " + paths.size() + " support blocks remaining.", action);
            return BlockPlaceResult.WaitForSupport;
        }


        if (centerSettingGroup.centerTiming.get().equals(CenterTiming.Placing)) PlayerUtil.centerEvent(centerSettingGroup);
        InventoryUtils.applySwap(swapMode, block);
        interact(resultStatus.bhr, block, swingSettings.handMode.get());
        InventoryUtils.swapBack(swapMode);

        if (resultStatus.status == 1) module.debug("Successfully air-placed the block.", action);
        else module.debug("Successfully placed the block onto the nearest neighbour.", action);


        return BlockPlaceResult.Success;
    }


    public static ResultStatus getBlockHitResult(BlockPos pos, SwapMode swapMode, FindItemResult itemResult,
                                                 PlaceSettingGroup placeSettings, CenterSettingGroup center, SwingSettingGroup swingSettingGroup) { // todo: add debugging here too
        Vec3d hitPos = Vec3d.ofCenter(pos);

        // can place off a neigbhour , does this by default
        Direction side = BlockUtils.getPlaceSide(pos);
        if (side != null) {
            return new ResultStatus(new BlockHitResult(hitPos, side.getOpposite(), pos.offset(side), false), 2);
        }

        // airplacing since theres no blocks to place off
        if (placeSettings.airPlace.get()) {
            return new ResultStatus(new BlockHitResult(hitPos, Direction.UP, pos, true), 1);
        }



        // creates a support path
        if (placeSettings.support.get()) {
            if (paths.isEmpty() || !pos.equals(lastTarget)) {
                paths = pathFinder.getPath(pos, placeSettings.supportRange.get());
                lastTarget = pos;
            }

            if (!paths.isEmpty()) {
                for (BlockPos supportPos : paths) {

                    Direction neighbour = BlockUtils.getPlaceSide(supportPos);
                    if (neighbour != null) {

                        if (center.centerTiming.get().equals(CenterTiming.Placing)) PlayerUtil.centerEvent(center);
                        InventoryUtils.applySwap(swapMode, itemResult);
                        interact(new BlockHitResult(supportPos.toCenterPos(), BlockUtils.getPlaceSide(supportPos).getOpposite(), supportPos, false), itemResult, swingSettingGroup.handMode.get());
                        InventoryUtils.swapBack(swapMode);

                        if (!BlockUtils.canPlace(supportPos, false)) { // successsufully placed support
                            RenderUtils.renderTickingBlock(
                                supportPos, Color.RED, Color.BLUE, ShapeMode.Both,
                                0, 5, true, true
                            );
                            paths.remove(supportPos);
                            return new ResultStatus(null, 0); // 0 indicates that it successfully palced a SUPPORT block ; 1 is the bhr for the pos itself (=
                        }
                    } else return new ResultStatus(null, -1);
                }
                paths.clear();
                lastTarget = null;
                return null;
            }
        }

        return null;
    }

    private static void interact(BlockHitResult bhr, FindItemResult itemResult, SwingMode swingMode) {
        if (mc.interactionManager != null) {
            ActionResult actionResult = mc.interactionManager.interactBlock(mc.player, itemResult.getHand(), bhr);
            if (actionResult.isAccepted()) if (mc.player != null && itemResult.getHand() != null) {
                swing(swingMode, itemResult.getHand());
            }
        }
    }

    private static boolean isWithinWalls(BlockPos pos, double wallsRange) {

        Vec3d eyes = new Vec3d(Objects.requireNonNull(mc.player).getX(), mc.player.getY() + mc.player.getStandingEyeHeight(), mc.player.getZ());


        Vec3d target = pos.toCenterPos();

        HitResult result = mc.world.raycast(new RaycastContext(
            eyes,
            target,
            RaycastContext.ShapeType.COLLIDER,
            RaycastContext.FluidHandling.NONE,
            mc.player
        ));

        return result.getType() == HitResult.Type.MISS;
    }


    private static void swing(SwingMode swingMode, Hand hand) {
        switch (swingMode) {
            case Interacted -> swing(hand);
            case Packet -> Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(new HandSwingC2SPacket(hand));
        }
    }


    private static void swing(Hand hand) {
        if (mc.player != null) {
            mc.player.swingHand(hand);
        }
    }

    public record ResultStatus(BlockHitResult bhr,
                               int status) {
    }
}
