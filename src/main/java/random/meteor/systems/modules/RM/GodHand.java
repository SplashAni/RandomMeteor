package random.meteor.systems.modules.RM;

import meteordevelopment.meteorclient.events.entity.player.StartBreakingBlockEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import random.meteor.Main;
import random.meteor.systems.modules.utils.Utils;

public class GodHand extends Module {
    public GodHand() {
        super(Main.RM,"god-hand","instant tool hand switch 0:");
    }
    BlockPos pos;
    Direction dir;
    @EventHandler
    public void onTick(TickEvent.Pre event){
        if(pos == null || dir == null) return;

        if(!mc.interactionManager.isBreakingBlock() && !mc.options.attackKey.isPressed()) return;

        BlockState state = Utils.state(pos).getDefaultState();

        FindItemResult tool = InvUtils.findFastestTool(state);
        if(!BlockUtils.canBreak(pos) || mc.player.getInventory().selectedSlot == tool.slot() || tool.equals(-1)) return;

        InvUtils.swap(tool.slot(),true);

        InvUtils.swapBack();
    }


    @EventHandler(priority = EventPriority.HIGH)
    private void onStartBreakingBlock(StartBreakingBlockEvent event) {
        dir = event.direction;
        pos = event.blockPos;
    }
}
