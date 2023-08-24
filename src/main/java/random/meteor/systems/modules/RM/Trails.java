package random.meteor.systems.modules.RM;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.world.dimension.DimensionType;

import java.util.ArrayList;
import java.util.List;

public class Trails extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<SettingColor> color = sgGeneral.add(new ColorSetting.Builder()
        .name("color")
        .description("The color of the Breadcrumbs trail.")
        .defaultValue(new SettingColor(225, 25, 25))
        .build()
    );

    private final Setting<Integer> maxPositions = sgGeneral.add(new IntSetting.Builder()
        .name("max-positions")
        .description("The maximum number of positions.")
        .defaultValue(1000)
        .min(1)
        .sliderRange(1, 5000)
        .build()
    );

    private final Setting<Double> positionDistance = sgGeneral.add(new DoubleSetting.Builder()
        .name("position-distance")
        .description("The minimum distance between recorded positions in blocks.")
        .defaultValue(0.5)
        .min(0)
        .sliderMax(1)
        .build()
    );

    private final List<Position> positions = new ArrayList<>();

    private DimensionType prevDimension;

    public Trails() {
        super(Categories.Render, "trails", ":yawn:");
    }

    @Override
    public void onActivate() {
        positions.clear();
        positions.add(new Position(mc.player.getX(), mc.player.getY(), mc.player.getZ()));
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (prevDimension != mc.world.getDimension()) {
            positions.clear();
        }

        Position lastPos = positions.get(positions.size() - 1);
        double distanceSq = lastPos.distanceTo(mc.player.getX(), mc.player.getY(), mc.player.getZ());

        if (distanceSq >= positionDistance.get() * positionDistance.get()) {
            if (positions.size() >= maxPositions.get()) {
                positions.remove(0);
            }
            positions.add(new Position(mc.player.getX(), mc.player.getY(), mc.player.getZ()));
        }

        prevDimension = mc.world.getDimension();
        if(prevDimension != mc.player.world.getDimension()){ /* awahhh wearryy :Wear::yyy*/
            positions.clear();
        }
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        int iLast = -1;

        for (Position position : positions) {
            if (iLast == -1) {
                iLast = event.renderer.lines.vec3(position.x, position.y, position.z).color(color.get()).next();
            } else {
                int i = event.renderer.lines.vec3(position.x, position.y, position.z).color(color.get()).next();
                event.renderer.lines.line(iLast, i);
                iLast = i;
            }
        }
    }

    private static class Position {
        public double x, y, z;

        public Position(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public double distanceTo(double x, double y, double z) {
            double dx = this.x - x;
            double dy = this.y - y;
            double dz = this.z - z;
            return dx * dx + dy * dy + dz * dz;
        }
    }
}
