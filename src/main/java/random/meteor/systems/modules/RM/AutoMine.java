package random.meteor.systems.modules.RM;

import meteordevelopment.meteorclient.events.entity.player.StartBreakingBlockEvent;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.joml.Vector3d;
import random.meteor.Main;

public class AutoMine extends Module {
    public AutoMine() {
        super(Main.RM, "auto-mine", "insane");
    }

    BlockPos pos;
    float progress = 0.0f;

    @EventHandler
    private void onStartBreakingBlock(StartBreakingBlockEvent event) {
        if (!BlockUtils.canBreak(event.blockPos)) return;

        pos = event.blockPos;
        progress = 0.0f;
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (pos == null) return;

        FindItemResult tool = InvUtils.findFastestTool(mc.world.getBlockState(pos));

        progress += BlockUtils.getBreakDelta(tool.slot() != -1 ? tool.slot() : mc.player.getInventory().selectedSlot, mc.world.getBlockState(pos));

        mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, Direction.UP));
        mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, Direction.UP));

        if (progress >= 1) {
            pos = null;
            progress = 0.0f;

            if(tool.slot() != -1){
                /*
                    Utils.move(tool.slot(),mc.player.getInventory().selectedSlot);
                Utils.move(mc.player.getInventory().selectedSlot,tool.slot());
                * */


                InvUtils.move().from(tool.slot()).to(mc.player.getInventory().selectedSlot);
                InvUtils.move().from(mc.player.getInventory().selectedSlot).to(tool.slot());

                
            }
        }
    }

    @EventHandler
    private void onRender2D(Render2DEvent event) {
        if (pos == null) return;
        Vector3d v = new Vector3d();
        v.set(pos.toCenterPos().x, pos.toCenterPos().y, pos.toCenterPos().z);

        if (NametagUtils.to2D(v, 1)) {
            NametagUtils.begin(v);
            TextRenderer.get().begin(1.2, false, true);

            String text = String.format("%.0f%%", progress * 100);
            double w = TextRenderer.get().getWidth(text) / 2;
            TextRenderer.get().render(text, -w, 0, Color.WHITE, true);

            TextRenderer.get().end();
            NametagUtils.end();
        }
    }
}
