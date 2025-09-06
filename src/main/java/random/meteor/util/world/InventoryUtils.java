package random.meteor.util.world;

import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import random.meteor.util.setting.modes.SwapMode;

public class InventoryUtils {
    public static void applySwap(SwapMode swapMode, FindItemResult block) {
        if (swapMode == SwapMode.Normal || swapMode == SwapMode.Silent) {
            InvUtils.swap(block.slot(), swapMode.equals(SwapMode.Silent));
        }
    }

    public static void swapBack(SwapMode swapMode) {
        if (swapMode.equals(SwapMode.Silent)) InvUtils.swapBack();
    }
}
