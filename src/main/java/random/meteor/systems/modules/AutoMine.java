package random.meteor.systems.modules;

import meteordevelopment.meteorclient.events.entity.player.StartBreakingBlockEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import random.meteor.systems.Mod;


public class AutoMine extends Mod {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgRender = settings.createGroup("Render");
    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>().name("mode").defaultValue(Mode.Normal).build());
    private final Setting<SwapMode> swapMode = sgGeneral.add(new EnumSetting.Builder<SwapMode>().name("swap-mode").defaultValue(SwapMode.Silent).build());

    private final Setting<ShapeMode> shapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>().name("shape-mode").description("How the shapes are rendered.").defaultValue(ShapeMode.Both).build());
    private final Setting<SettingColor> sideColor = sgRender.add(new ColorSetting.Builder().name("side-color").description("The color of the sides of the blocks being rendered.").defaultValue(new SettingColor(204, 0, 0, 10)).build());

    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder().name("line-color").description("The color of the lines of the blocks being rendered.").defaultValue(new SettingColor(204, 0, 0, 255)).build());
    BlockState prevState;
    BlockPos pos;
    Direction direction;
    double progress;
    boolean swap, speedRemine;

    public AutoMine() {
        super("auto-mine", "insane");
    }


    @EventHandler
    private void onStartBreakingBlock(StartBreakingBlockEvent event) {
        if (!BlockUtils.canBreak(event.blockPos)) return;


        if (pos != null) {
            if (pos == event.blockPos) return;
            mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, pos, direction));
        }
        prevState = null;
        pos = event.blockPos;
        direction = event.direction;
        progress = 0d;

        mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, direction));

    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {

        if (swap) {
            swapBack();
            swap = false;
        }

        if (pos == null || direction == null) return;

        int bestSlot = InvUtils.findFastestTool(getState()).slot();

        if (progress >= 1.0f) {


            if (!mc.world.getBlockState(pos).isAir()) {
                prevState = mc.world.getBlockState(pos);
                mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, direction));
                swap(bestSlot);
                swap = true;
            } else {

                if (mode.equals(Mode.Normal)) {
                    pos = null;
                    direction = null;
                } else if (mode.equals(Mode.SpeedRemine)) {
                    speedRemine = true;
                }
                progress = 0.0f;

            }

        } else {

            progress += BlockUtils.getBreakDelta(bestSlot != -1 ? bestSlot :
                mc.player.getInventory().getSelectedSlot(), getState());

        }
    }

    public void swapBack() {
        switch (swapMode.get()) {
            case Held -> InvUtils.swapBack();
            case Silent ->
                mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.getInventory().getSelectedSlot()));
        }
    }

    public void swap(int slot) {
        switch (swapMode.get()) {
            case None -> {
            }
            case Normal -> {
            }
            case Held -> InvUtils.swap(slot, true);
            case Silent -> mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(slot));
        }
    }

    private BlockState getState() {
        return mode.get().equals(Mode.SpeedRemine) ? prevState == null ? mc.world.getBlockState(pos) : prevState : mc.world.getBlockState(pos);
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        if (pos == null) return;
        renderFadingBlock(event, pos, sideColor.get(), lineColor.get(), (float) progress, true, true, shapeMode.get());
    }

    public void renderFadingBlock(Render3DEvent event, BlockPos pos, Color sideColor, Color lineColor, float progress, boolean shrink, boolean fade, ShapeMode shapeMode) {
        if (progress < 0.0f || progress > 1.0f) {
            return;
        }

        int originalSideAlpha = sideColor.a;
        int originalLineAlpha = lineColor.a;

        double x1 = pos.getX(), y1 = pos.getY(), z1 = pos.getZ();
        double x2 = pos.getX() + 1, y2 = pos.getY() + 1, z2 = pos.getZ() + 1;


        if (shrink) {
            double oscillation = 0.5 * (1.0 + Math.cos(progress * Math.PI * 2));
            oscillation = Math.max(0, Math.min(1, oscillation));
            double offset = (1.0 - oscillation) / 2.0;
            x1 += offset;
            y1 += offset;
            z1 += offset;
            x2 -= offset;
            y2 -= offset;
            z2 -= offset;
        }


        event.renderer.box(x1, y1, z1, x2, y2, z2, sideColor, lineColor, shapeMode, 0);

        sideColor.a = originalSideAlpha;
        lineColor.a = originalLineAlpha;
    }

    public enum SwapMode {
        None, Normal, Held, Silent
    }

    public enum Mode {
        Normal, SpeedRemine
    }
}
