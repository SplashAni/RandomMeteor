package random.meteor.util.render;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.util.math.BlockPos;
import random.meteor.util.setting.groups.RenderSettingGroup;

public class RenderBlock {
    BlockPos pos;
    Color lineColor, sideColor;
    int ticks, renderTime;
    RenderSettingGroup settings;

    public RenderBlock(BlockPos pos, RenderSettingGroup renderSettingGroup) {
        this.pos = pos;
        this.lineColor = renderSettingGroup.lineColor.get();
        this.sideColor = renderSettingGroup.sideColor.get();
        this.ticks = 0;
        this.renderTime = renderSettingGroup.renderTime.get();
        this.settings = renderSettingGroup;

    }

    public boolean isDoneRendering() {
        return ticks >= renderTime;
    }

    public void updateTimer() {
        ticks++;
    }

    public void render(Render3DEvent event) {
        double x1 = pos.getX(), y1 = pos.getY(), z1 = pos.getZ(),
            x2 = pos.getX() + 1, y2 = pos.getY() + 1, z2 = pos.getZ() + 1;

        double progress = (double) ticks / renderTime;
        double inv = 1.0 - progress;

        Color side = new Color(sideColor);
        Color line = new Color(lineColor);
        if (settings.fade.get()) {
            side.a = (int) (side.a * inv);
            line.a = (int) (line.a * inv);
        }

        y2 = y1 + (y2 - y1) * settings.blockHeight.get();

        if (settings.shrink.get()) {
            double shrinkAmt = 0.5 * progress;
            x1 += shrinkAmt;
            y1 += shrinkAmt;
            z1 += shrinkAmt;
            x2 -= shrinkAmt;
            y2 -= shrinkAmt;
            z2 -= shrinkAmt;
        }

        if (settings.slide.get()) {
            y1 -= progress;
            y2 -= progress;
        }

        switch (settings.renderType.get()) {
            case Normal ->
                event.renderer.box(x1, y1, z1, x2, y2, z2, side, line, settings.renderMode.get().toShapeMode(), 0);
            case Gradient -> {
                RenderUtil.sidesGradient(event.renderer, x1, y1, z1, x2, y2, z2, settings);
                RenderUtil.lineGradient(event.renderer, x1, y1, z1, x2, y2, z2, settings);
            }
        }
    }
}
