package random.meteor.util.render;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.Renderer3D;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.orbit.EventHandler;
import random.meteor.manager.Manager;

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
    public static void fillGradient(Renderer3D renderer,
                                    double x1, double y1, double z1,
                                    double x2, double y2, double z2,
                                    Color top, Color bottom, GradientMode gradientMode) {

        if (gradientMode == GradientMode.Bottom) renderer.quadHorizontal(x1, y1, z1, x2, z2, bottom);
        else if (gradientMode == GradientMode.Top) renderer.quadHorizontal(x1, y2, z1, x2, z2, top);

        renderer.gradientQuadVertical(x1, y2, z1, x2, y1, z1, bottom, top);
        renderer.gradientQuadVertical(x1, y2, z1, x1, y1, z2, bottom, top);
        renderer.gradientQuadVertical(x2, y2, z1, x2, y1, z2, bottom, top);
        renderer.gradientQuadVertical(x1, y2, z2, x2, y1, z2, bottom, top);
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
