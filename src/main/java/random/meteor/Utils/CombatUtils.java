package random.meteor.Utils;

import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;

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
    public static PlayerEntity player() {
        PlayerEntity player = mc.player;
        return player;
    }
}