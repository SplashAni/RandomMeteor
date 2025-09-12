package random.meteor.systems.modules;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.BlockPos;
import random.meteor.util.render.RenderMode;
import random.meteor.util.render.RenderShape;
import random.meteor.util.render.RenderUtil;
import random.meteor.util.system.Category;
import random.meteor.util.system.Mod;

public class RenderTest extends Mod {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<RenderShape> shape = sgGeneral.add(new EnumSetting.Builder<RenderShape>()
        .name("render-shape")
        .defaultValue(RenderShape.Normal)
        .build()
    );

    private final Setting<RenderMode> renderMode = sgGeneral.add(new EnumSetting.Builder<RenderMode>()
        .name("render-mode")
        .defaultValue(RenderMode.Both)
        .build()
    );


    private final Setting<SettingColor> sideColor = sgGeneral.add(new ColorSetting.Builder()
        .name("side-color")
        .description("The side color of the rendering.")
        .defaultValue(new SettingColor(225, 0, 0, 75))
        .build()
    );

    private final Setting<SettingColor> lineColor = sgGeneral.add(new ColorSetting.Builder()
        .name("line-color")
        .description("The line color of the rendering.")
        .defaultValue(new SettingColor(225, 0, 0, 255))
        .build()
    );


    public RenderTest() {
        super("render-test", Category.PVP);
    }

    @EventHandler
    public void testredner(Render3DEvent event) {
        BlockPos pos = mc.player.getBlockPos().up(2);
        RenderUtil.renderBlock(event, pos, shape.get(), renderMode.get(), sideColor.get(), lineColor.get());
    }
}
