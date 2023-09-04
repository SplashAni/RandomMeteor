package random.meteor.systems.modules.RM;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.Dir;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.Vec3d;
import random.meteor.Main;

public class GradientOverlay extends Module {
    private final SettingGroup sgRender = settings.createGroup("Render");

    private final Setting<SettingColor> gradient = sgRender.add(new ColorSetting.Builder()
        .name("gradient")
        .description("The side color of the rendering.")
        .defaultValue(new SettingColor(225, 0, 0, 75))
        .build()
    );

    private final Setting<SettingColor> bottom = sgRender.add(new ColorSetting.Builder()
        .name("bottom")
        .description("The line color of the rendering.")
        .defaultValue(new SettingColor(225, 0, 0, 255,true))
        .build()
    );

    public GradientOverlay() {
        super(Main.RM,"gradient-overlay","rewnders hot overlay over u screen");
    }
    @EventHandler
    public void onTick(TickEvent.Pre event){
        Color c = gradient.get();

    }
    @EventHandler
    private void onRender(Render3DEvent event) {

        Color top = gradient.get();

        Color bottom = this.bottom.get();

        int height = 1;

        Vec3d pos = Vec3d.of(mc.player.getBlockPos().up());

        int x = (int) pos.x;
        int y = (int) pos.y;
        int z = (int) pos.z;

        if (Dir.isNot(0, Dir.UP)) event.renderer.quad(x, y + height, z, x, y + height, z + 1, x + 1, y + height, z + 1, x + 1, y + height, z, top); // Top
        if (Dir.isNot(0, Dir.DOWN)) event.renderer.quad(x, y, z, x, y, z + 1, x + 1, y, z + 1, x + 1, y, z, bottom);

        if (Dir.isNot(0, Dir.NORTH)) event.renderer.gradientQuadVertical(x, y, z, x + 1, y + height, z, top, bottom);
        if (Dir.isNot(0, Dir.SOUTH)) event.renderer.gradientQuadVertical(x, y, z + 1, x + 1, y + height, z + 1, top, bottom);

        if (Dir.isNot(0, Dir.WEST)) event.renderer.gradientQuadVertical(x, y, z, x, y + height, z + 1, top, bottom);
        if (Dir.isNot(0, Dir.EAST)) event.renderer.gradientQuadVertical(x + 1, y, z, x + 1, y + height, z + 1, top, bottom);

    }

}
