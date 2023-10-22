package random.meteor.utils;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;
import java.util.Random;

import static meteordevelopment.meteorclient.MeteorClient.mc;
import static meteordevelopment.meteorclient.utils.player.InvUtils.swap;

public class Utils {
    private static final Random random = new Random();

    public static void placeCrystal(BlockPos pos, FindItemResult crystal){
        InvUtils.swap(crystal.slot(),false);
        assert mc.interactionManager != null;
        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(pos.toCenterPos(), Direction.UP, pos, true));
    }
    public static void updateHotbar() {
        assert mc.player != null;
        mc.player.getInventory();
        mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.getInventory().selectedSlot));
    }
    public static void updatePosition(){
        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                mc.player.getX(),
                mc.player.getY(),
                mc.player.getZ(),
                mc.player.isOnGround()
        ));
    }

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


    /*thanks daddy @EurekaEffect :wink:
     * https://media.discordapp.net/attachments/955857188130283621/1144293552550068224/image.png?width=593&height=456*/

    public static void move(int from, int to) {
        ScreenHandler handler = mc.player.currentScreenHandler;

        Int2ObjectArrayMap<ItemStack> stack = new Int2ObjectArrayMap<>();
        stack.put(to, handler.getSlot(to).getStack());

        mc.getNetworkHandler().sendPacket(new ClickSlotC2SPacket(handler.syncId, handler.getRevision(), PlayerInventory.MAIN_SIZE + from, to, SlotActionType.SWAP, handler.getCursorStack().copy(), stack));
    }

    public Block[] xd(){
        Block[] blocks = new Block[1];
        return blocks;
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


    public static boolean isNether() {
        return mc.world.getDimension().respawnAnchorWorks();
    }

    public static boolean isSelf(LivingEntity target) {
        return mc.player.getBlockPos().getX() == target.getBlockPos().getX() && mc.player.getBlockPos().getZ() == target.getBlockPos().getZ() && mc.player.getBlockPos().getY() == target.getBlockPos().getY();
    }

    public static boolean canContinue(int timer, int delay) {
        return timer >= delay;
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
    public static EndCrystalEntity getCrystal(BlockPos pos){
        assert mc.world != null;
        for (Entity entity : mc.world.getEntities()){

            if(!(entity instanceof EndCrystalEntity)) continue;

            if (entity.getBlockPos().equals(pos.up())) return (EndCrystalEntity) entity;
        }
        return null;
    }

    public static boolean isBlock(BlockPos p){
        return state(p).equals(Blocks.AIR);
    }
}
