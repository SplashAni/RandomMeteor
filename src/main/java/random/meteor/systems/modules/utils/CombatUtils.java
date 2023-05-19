package random.meteor.systems.modules.utils;

import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import org.spongepowered.asm.util.PrettyPrinter;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class CombatUtils {
    public static void throwPearl(int value) {
        FindItemResult pearl = InvUtils.findInHotbar(Items.ENDER_PEARL);
        if (!pearl.found()) return;

        Rotations.rotate(mc.player.getYaw(), value, () -> {
            if (pearl.getHand() != null) {
                mc.interactionManager.interactItem(mc.player, pearl.getHand());
            } else {
                InvUtils.swap(pearl.slot(), true);
                mc.interactionManager.interactItem(mc.player, pearl.getHand());
                InvUtils.swapBack();
            }
        });
    }

    public static PlayerEntity nearestTarget() {
        if(mc.world != null || mc.player != null){
            for(PlayerEntity player : mc.world.getPlayers()){
            return player;
            }
        }
        return null;
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
}