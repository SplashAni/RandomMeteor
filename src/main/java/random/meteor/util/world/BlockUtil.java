package random.meteor.util.world;

import com.nimbusds.openid.connect.sdk.id.SectorID;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.Renderer3D;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import random.meteor.util.setting.groups.PlaceSettingGroup;
import random.meteor.util.setting.groups.RangeSettingGroup;
import random.meteor.util.setting.groups.SwapSettingGroup;
import random.meteor.util.setting.groups.SwingSettingGroup;
import random.meteor.util.setting.modes.HandMode;
import random.meteor.util.setting.modes.SwapMode;
import random.meteor.util.setting.modes.WorldHeight;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static meteordevelopment.meteorclient.MeteorClient.mc;


public class BlockUtil { // make a class handler to sqeudle runnabled to run in the world

    private static final PathFinder pathFinder;
    private static List<BlockPos> paths = new ArrayList<>();
    private static BlockPos lastTarget = null;

    static {
        pathFinder = new PathFinder();
    }


    boolean outSideBorder = false;
    WorldHeight worldHeight = WorldHeight.New; // create placeholders for now

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


    public static BlockPlaceResult place(BlockPos blockPos, PlaceSettingGroup placeSettings, RangeSettingGroup rangeSettings, SwapSettingGroup swapSettings,
                                         SwingSettingGroup swingSettings) {

        if (!BlockUtils.canPlace(blockPos, placeSettings.checkEntities.get())) return BlockPlaceResult.Fail;
        if (!PlayerUtils.isWithin(blockPos, rangeSettings.range.get())) return BlockPlaceResult.Fail;
        if (rangeSettings.eyeOnly.get() && !isWithinWalls(blockPos, rangeSettings.wallsRange.get()))
            return BlockPlaceResult.Fail;

        FindItemResult block = InvUtils.findInHotbar(itemStack -> placeSettings.blocks.get().contains(Block.getBlockFromItem(itemStack.getItem())));

        SwapMode swapMode = swapSettings.swapMode.get();


        if (swapMode == SwapMode.Normal || swapMode == SwapMode.Silent) {
            InvUtils.swap(block.slot(), swapMode.equals(SwapMode.Silent));
        }

        Hand hand = block.getHand();


        Vec3d hitPos = Vec3d.ofCenter(blockPos);


        HandMode handMode = swingSettings.handMode.get();

        BlockHitResult bhr = null;

        if (!placeSettings.airPlace.get()) {
            Direction side = BlockUtils.getPlaceSide(blockPos);

            if (side != null) {
                bhr = new BlockHitResult(hitPos, side.getOpposite(), blockPos.offset(side), false);

            } else if (placeSettings.support.get()) {
                if (paths.isEmpty() || !blockPos.equals(lastTarget)) {
                    paths = pathFinder.getPath(blockPos,placeSettings.supportRange.get());
                    lastTarget = blockPos;
                }

                if (!paths.isEmpty()) {
                    boolean placedSupport = false;

                    for (BlockPos pos : paths) {
                        if (BlockUtils.place(pos, block, true, 0, false, true, true)) {
                            RenderUtils.renderTickingBlock(
                                pos, Color.RED,
                                Color.BLUE, ShapeMode.Both,
                                0, 5, true,
                                true
                            );
                            placedSupport = true;
                            break;
                        }
                    }

                    if (!placedSupport) {
                        paths.clear();
                        lastTarget = null;
                        return BlockPlaceResult.WaitForSupport;
                    }
                } else {
                    return BlockPlaceResult.Fail;
                }

            } else {
                return BlockPlaceResult.Fail;
            }

        } else {
            bhr = new BlockHitResult(hitPos, Direction.UP, blockPos, true);
        }


        if (mc.interactionManager != null && bhr != null) {
            mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, bhr);
        }

        return BlockPlaceResult.Success;
    }

    private static void swing(HandMode handMode, Hand hand) {
        switch (handMode) {
            case MainHand -> swing(Hand.MAIN_HAND);
            case Offhand -> swing(Hand.OFF_HAND);
            case Interacted -> swing(hand);
            case Both -> {
                swing(Hand.MAIN_HAND);
                swing(Hand.OFF_HAND);
            }
            case Packet -> mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(hand));
        }
    }

    private static void swing(Hand hand) {
        if (mc.player != null) {
            mc.player.swingHand(hand);
        }
    }

}
