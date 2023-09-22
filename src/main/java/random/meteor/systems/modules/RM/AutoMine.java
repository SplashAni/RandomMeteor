package random.meteor.systems.modules.RM;

import meteordevelopment.meteorclient.events.entity.player.StartBreakingBlockEvent;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
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
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
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
    Color top = Color.RED.a(15);
    Color bottom = Color.RED.a(15);
    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (pos == null) return;

        if (progress < 30) {
            top = Color.RED;
        } else if (progress >= 30 && progress <= 50) {
            top = Color.ORANGE;
        } else if (progress >= 51 && progress <= 70) {
            top = Color.YELLOW;

        } else if (progress >= 71 && progress < 95) {
            int r = (int) (top.r + ((95 - progress) * (top.r - bottom.r) / 25));
            int g = (int) (top.g + ((95 - progress) * (top.g - bottom.g) / 25));
            int b = (int) (top.b + ((95 - progress) * (top.b - bottom.b) / 25));
            top = new Color(r, g, b);
        }
        System.lineSeparator();

        FindItemResult tool = InvUtils.findFastestTool(mc.world.getBlockState(pos));

        progress += BlockUtils.getBreakDelta(tool.slot() != -1 ? tool.slot() : mc.player.getInventory().selectedSlot, mc.world.getBlockState(pos));

        mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, Direction.UP));
        mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, Direction.UP));

        if (progress >= 1) {
            pos = null;
            progress = 0.0f;

            if (tool.slot() != -1) {
                /*

                InvUtils.move().from(tool.slot()).to(mc.player.getInventory().selectedSlot);
                InvUtils.move().from(mc.player.getInventory().selectedSlot).to(tool.slot());

                * */
                mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(tool.slot()));

               // Utils.updateHotbar();
                pos = null;
            }
        }
    }

    @EventHandler
    private void onRender2D(Render2DEvent event) {
        int i = 1;
        if (i == 1) return;

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

    @EventHandler
    private void onRender(Render3DEvent event) {
        if(this.pos == null) return;


        int height = 1;

        Vec3d pos = Vec3d.of(this.pos);

        int x = (int) pos.x;
        int y = (int) pos.y;
        int z = (int) pos.z;

        for (Direction d : Direction.values()) {
            switch (d) {
                case UP -> event.renderer.quad(x, y + height, z, x, y + height, z + 1, x + 1, y + height, z + 1, x + 1, y + height, z, top);
                case DOWN -> event.renderer.quad(x, y, z, x, y, z + 1, x + 1, y, z + 1, x + 1, y, z, bottom);
                case NORTH -> event.renderer.gradientQuadVertical(x, y, z, x + 1, y + height, z, top, bottom);
                case SOUTH -> event.renderer.gradientQuadVertical(x, y, z + 1, x + 1, y + height, z + 1, top, bottom);
                case WEST -> event.renderer.gradientQuadVertical(x, y, z, x, y + height, z + 1, top, bottom);
                case EAST -> event.renderer.gradientQuadVertical(x + 1, y, z, x + 1, y + height, z + 1, top, bottom);
            }
        }
    }
}