package random.meteor.systems.modules.RM;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import random.meteor.Main;

public class GradientEsp extends Module {
    private final SettingGroup sgRender = settings.createGroup("Render");

    private final Setting<SettingColor> gradient = sgRender.add(new ColorSetting.Builder()
        .name("gradient")
        .description("The side color of the rendering.")
        .defaultValue(new SettingColor(225, 0, 0, 75, true))
        .build()
    );

    private final Setting<SettingColor> bottom = sgRender.add(new ColorSetting.Builder()
        .name("bottom")
        .description("The line color of the rendering.")
        .defaultValue(new SettingColor(225, 0, 0, 255, true))
        .build()
    );
    private final Setting<Double> xx = sgRender.add(new DoubleSetting.Builder()
            .name("x")
            .description("The line color of the rendering.")
            .defaultValue(0.1)
                    .range(0.1,5)
            .build()
    );
    private final Setting<Double> yy = sgRender.add(new DoubleSetting.Builder()
            .name("y")
            .description("The line color of the rendering.")
            .defaultValue(0.0001)
            .range(0.0001,5)
            .build()
    );
    private final Setting<Double> zz = sgRender.add(new DoubleSetting.Builder()
            .name("z")
            .description("The line color of the rendering.")
            .defaultValue(0.1)
            .range(0.1,5)
            .build()
    );

    public GradientEsp() {
        super(Main.RM, "gradient-esp", "ye");
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        Color top = gradient.get();
        Color bottom = this.bottom.get();
        double sizeX = xx.get(); // Adjust the size along the X-axis
        double sizeY = yy.get(); // Adjust the size along the Y-axis
        double sizeZ = zz.get(); // Adjust the size along the Z-axis

        Vec3d pos = mc.player.getPos();
        double x = pos.x;
        double y = pos.y;
        double z = pos.z;

        for (Direction d : Direction.values()) {
            switch (d) {
                case UP -> event.renderer.quad(
                        x - sizeX, y + 2, z - sizeZ,
                        x - sizeX, y + 2 + sizeY, z + sizeZ,
                        x + sizeX, y + 2 + sizeY, z + sizeZ,
                        x + sizeX, y + 2, z - sizeZ,
                        top
                );
                case DOWN -> event.renderer.quad(
                        x - sizeX, y, z - sizeZ,
                        x - sizeX, y + sizeY, z + sizeZ,
                        x + sizeX, y + sizeY, z + sizeZ,
                        x + sizeX, y, z - sizeZ,
                        bottom
                );
                case NORTH -> event.renderer.gradientQuadVertical(
                        x - sizeX, y, z - sizeZ,
                        x + sizeX, y + 2 + sizeY, z - sizeZ,
                        top, bottom
                );
                case SOUTH -> event.renderer.gradientQuadVertical(
                        x - sizeX, y, z + sizeZ,
                        x + sizeX, y + 2 + sizeY, z + sizeZ,
                        top, bottom
                );
                case WEST -> event.renderer.gradientQuadVertical(
                        x - sizeX, y, z - sizeZ,
                        x - sizeX, y + 2 + sizeY, z + sizeZ,
                        top, bottom
                );
                case EAST -> event.renderer.gradientQuadVertical(
                        x + sizeX, y, z - sizeZ,
                        x + sizeX, y + 2 + sizeY, z + sizeZ,
                        top, bottom
                );
            }
        }
    }
}