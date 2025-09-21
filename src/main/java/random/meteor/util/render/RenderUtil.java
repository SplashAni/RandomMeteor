package random.meteor.util.render;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.Renderer3D;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.orbit.EventHandler;
import random.meteor.manager.Manager;
import random.meteor.util.setting.groups.RenderSettingGroup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RenderUtil extends Manager {
    private static final List<RenderBlock> blocks = new ArrayList<>();

    @Override
    public void onInitialize() {
        MeteorClient.EVENT_BUS.subscribe(this);
        super.onInitialize();
    }

    public static void addBlock(RenderBlock newBlock) {
        for (RenderBlock block : blocks) {
            if (block.pos.equals(newBlock.pos)) {

                block.ticks = 0;

                block.renderTime = newBlock.renderTime;
                block.lineColor = newBlock.lineColor;
                block.sideColor = newBlock.sideColor;
                block.settings = newBlock.settings;

                return;
            }
        }

        blocks.add(newBlock);
    }

    public static void sidesGradient(Renderer3D renderer, double x1, double y1, double z1, double x2, double y2, double z2, RenderSettingGroup settings) {

        Color topColor = settings.sideColor.get();
        Color top = new Color(topColor.r, topColor.g, topColor.b, topColor.a);
        Color bottom = new Color(topColor.r, topColor.g, topColor.b, 0);

        switch (settings.gradientMode.get()) {
            case Bottom -> {
                Color tmp = top;
                top = bottom;
                bottom = tmp;
            }
            case Full -> bottom = settings.sideColor2.get();
        }

        switch (settings.gradientMode.get()) {
            case Bottom -> renderer.quadHorizontal(x1, y1, z1, x2, z2, bottom);
            case Top -> renderer.quadHorizontal(x1, y2, z1, x2, z2, top);
            case Full -> {
                renderer.quadHorizontal(x1, y1, z1, x2, z2, bottom);
                renderer.quadHorizontal(x1, y2, z1, x2, z2, top);
            }
        }

        renderer.gradientQuadVertical(x1, y2, z1, x2, y1, z1, bottom, top);
        renderer.gradientQuadVertical(x1, y2, z1, x1, y1, z2, bottom, top);
        renderer.gradientQuadVertical(x2, y2, z1, x2, y1, z2, bottom, top);
        renderer.gradientQuadVertical(x1, y2, z2, x2, y1, z2, bottom, top);
    }

    public static void lineGradient(Renderer3D renderer, double x1, double y1, double z1, double x2, double y2, double z2, RenderSettingGroup settings) {

        Color topColor = settings.sideColor.get();

        Color top = new Color(topColor.r, topColor.g, topColor.b, topColor.a);
        Color bottom = new Color(topColor.r, topColor.g, topColor.b, 0);

        switch (settings.gradientMode.get()) {
            case Bottom -> {
                Color tmp = top;
                top = bottom;
                bottom = tmp;
            }
            case Full -> bottom = settings.lineColor2.get();
        }

        switch (settings.gradientMode.get()) {
            case Bottom -> {
                renderer.line(x1, y1, z1, x2, y1, z1, bottom);
                renderer.line(x1, y1, z1, x1, y1, z2, bottom);
                renderer.line(x2, y1, z1, x2, y1, z2, bottom);
                renderer.line(x1, y1, z2, x2, y1, z2, bottom);
            }
            case Top -> {
                renderer.line(x1, y2, z1, x2, y2, z1, top);
                renderer.line(x1, y2, z1, x1, y2, z2, top);
                renderer.line(x2, y2, z1, x2, y2, z2, top);
                renderer.line(x1, y2, z2, x2, y2, z2, top);
            }
            case Full -> {
                renderer.line(x1, y1, z1, x2, y1, z1, bottom);
                renderer.line(x1, y1, z1, x1, y1, z2, bottom);
                renderer.line(x2, y1, z1, x2, y1, z2, bottom);
                renderer.line(x1, y1, z2, x2, y1, z2, bottom);
                renderer.line(x1, y2, z1, x2, y2, z1, top);
                renderer.line(x1, y2, z1, x1, y2, z2, top);
                renderer.line(x2, y2, z1, x2, y2, z2, top);
                renderer.line(x1, y2, z2, x2, y2, z2, top);
            }
        }

        renderer.line(x1, y2, z1, x1, y1, z1, top, bottom);
        renderer.line(x2, y2, z1, x2, y1, z1, top, bottom);
        renderer.line(x1, y2, z2, x1, y1, z2, top, bottom);
        renderer.line(x2, y2, z2, x2, y1, z2, top, bottom);
    }


    @EventHandler
    public void render(Render3DEvent event) {
        Iterator<RenderBlock> iterator = blocks.iterator();
        while (iterator.hasNext()) {
            RenderBlock block = iterator.next();
            block.render(event);

            if (block.isDoneRendering()) {
                iterator.remove();
            }
        }
    }

    @EventHandler
    public void tick(TickEvent.Pre event) {
        for (RenderBlock block : blocks) {
            block.updateTimer();
        }
    }

}
