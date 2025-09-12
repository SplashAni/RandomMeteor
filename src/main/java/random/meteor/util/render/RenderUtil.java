package random.meteor.util.render;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.util.math.BlockPos;
import random.meteor.manager.Manager;

public class RenderUtil extends Manager {


    public static void renderBlock(Render3DEvent event, BlockPos pos, RenderShape renderShape, RenderMode renderMode, Color fill, Color lines) {
        switch (renderShape) {
            case Normal -> {
                event.renderer.box(pos, fill, lines, renderMode.toShapeMode(), 0);
            }
        }
    }
}
