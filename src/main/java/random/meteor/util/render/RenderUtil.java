package random.meteor.util.render;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.BlockPos;
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
                block.renderShape = newBlock.renderShape;
                block.renderMode = newBlock.renderMode;
                block.shrink = newBlock.shrink;
                block.slide = newBlock.slide;
                block.fade = newBlock.fade;

                return;
            }
        }

        blocks.add(newBlock);
    }


    public static void renderBlock(Render3DEvent event, BlockPos pos, RenderShape renderShape,
                                   RenderMode renderMode, Color fill, Color lines) {
        switch (renderShape) {
            case Normal -> event.renderer.box(pos, fill, lines, renderMode.toShapeMode(), 0);
        }
    }

    @EventHandler
    public void render(Render3DEvent event) {
        Iterator<RenderBlock> it = blocks.iterator();
        while (it.hasNext()) {
            RenderBlock block = it.next();
            block.render(event);

            if (block.isExpired()) {
                it.remove();
            }
        }
    }

    @EventHandler
    public void tick(TickEvent.Pre event) {
        for (RenderBlock block : blocks) {
            block.tick(); /
        }
    }

}
