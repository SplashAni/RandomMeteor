package random.meteor.systems.modules.utils;

import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

import static meteordevelopment.meteorclient.MeteorClient.mc;
import static meteordevelopment.meteorclient.utils.player.InvUtils.swap;

public class Utils {
    private static final Random random = new Random();

    public static void throwPearl(int value) {
        FindItemResult pearl = InvUtils.findInHotbar(Items.ENDER_PEARL);
        if (!pearl.found()) return;

        assert mc.player != null;
        Rotations.rotate(mc.player.getYaw(), value, () -> {
            if (pearl.getHand() != null) {
                mc.interactionManager.interactItem(mc.player, pearl.getHand());
            } else {
                swap(pearl.slot(), true);
                assert mc.interactionManager != null;
                mc.interactionManager.interactItem(mc.player, pearl.getHand());
                InvUtils.swapBack();
            }
        });
    }

    private boolean canPlace(BlockPos pos, int range) {
        //  return (mc.world.getBlockState(pos).isAir() || mc.world.getBlockState(pos).getFluidState().getFluid() instanceof FlowableFluid) && Math.sqrt(mc.player.getBlockPos().getSquaredDistance(pos)) <= placeRange.get() && getDamagePlace(pos);
        return false;
    }

    public static final Block[] SHULKER_BLOCKS = new Block[]{
        Blocks.SHULKER_BOX,
        Blocks.WHITE_SHULKER_BOX,
        Blocks.ORANGE_SHULKER_BOX,
        Blocks.MAGENTA_SHULKER_BOX,
        Blocks.LIGHT_BLUE_SHULKER_BOX,
        Blocks.YELLOW_SHULKER_BOX,
        Blocks.LIME_SHULKER_BOX,
        Blocks.PINK_SHULKER_BOX,
        Blocks.GRAY_SHULKER_BOX,
        Blocks.LIGHT_GRAY_SHULKER_BOX,
        Blocks.CYAN_SHULKER_BOX,
        Blocks.PURPLE_SHULKER_BOX,
        Blocks.BLUE_SHULKER_BOX,
        Blocks.BROWN_SHULKER_BOX,
        Blocks.GREEN_SHULKER_BOX,
        Blocks.RED_SHULKER_BOX,
        Blocks.BLACK_SHULKER_BOX
    };

    public static boolean goldArmor() {
        for (ItemStack armor : mc.player.getArmorItems()) {
            if (armor.getItem() instanceof ArmorItem && ((ArmorItem) armor.getItem()).getMaterial() == ArmorMaterials.GOLD) {
                return true;
            }
        }
        return false;
    }

    public static void rotate(EntityType en) {
        double closest = Double.MAX_VALUE;
        Vec3d pos = mc.player.getPos();
        Vec3d nearest = null;

        for (Entity entity : mc.world.getEntities()) {
            if (entity.getType() == en) {
                double distance = entity.getPos().squaredDistanceTo(pos);
                if (distance < closest) {
                    closest = distance;
                    nearest = entity.getPos();
                }
            }
        }

        if (nearest != null) {
            double yaw = Math.atan2(nearest.getZ() - pos.getZ(), nearest.getX() - pos.getX()) * (180 / Math.PI) - 90;
            double pitch = Math.atan2(nearest.getY() - pos.getY(), Math.sqrt(Math.pow(nearest.getX() - pos.getX(), 2) + Math.pow(nearest.getZ() - pos.getZ(), 2))) * (180 / Math.PI);
            Rotations.rotate(yaw, pitch, 100);
        }
    }

    public static void switchToGold() {
        int goldSlot = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() == Items.GOLD_INGOT || stack.getItem() == Items.GOLD_BLOCK) {
                goldSlot = i;
                break;
            }
        }
        if (goldSlot != -1) {
            mc.player.getInventory().selectedSlot = goldSlot;
        }
    }

    public static Direction getBestDirection() {
        Direction[] directions = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
        return directions[random.nextInt(directions.length)];
    }

    public static float getYawFromDirection(Direction direction) {
        if (direction == Direction.NORTH) {
            return 180;
        } else if (direction == Direction.SOUTH) {
            return 360;
        } else if (direction == Direction.WEST) {
            return 90;
        } else if (direction == Direction.EAST) {
            return 270;
        }
        return 0;
    }

    public static double distanceTo(BlockPos blockPos1, BlockPos blockPos2) {
        double d = blockPos1.getX() - blockPos2.getX();
        double e = blockPos1.getY() - blockPos2.getY();
        double f = blockPos1.getZ() - blockPos2.getZ();
        return MathHelper.sqrt((float) (d * d + e * e + f * f));
    }

    public static boolean isNether() {
        return mc.world.getDimension().respawnAnchorWorks();
    }

    public static boolean isSelf(LivingEntity target) {
        return mc.player.getBlockPos().getX() == target.getBlockPos().getX() && mc.player.getBlockPos().getZ() == target.getBlockPos().getZ() && mc.player.getBlockPos().getY() == target.getBlockPos().getY();
    }

    public static Block state(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock();
    }

    public static Entity crystal() {
        for (Entity e : mc.world.getEntities()) {
            if (e.getType().equals(EntityType.END_CRYSTAL)) return e;
        }
        return null;
    }

    public static void swapRun(int slot, boolean back, Runnable execute) {
        InvUtils.swap(slot, back);
        execute.run();
        if (back) InvUtils.swapBack();
    }
}
