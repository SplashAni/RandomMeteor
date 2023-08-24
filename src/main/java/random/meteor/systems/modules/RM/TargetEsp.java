package random.meteor.systems.modules.RM;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import random.meteor.Main;

public class TargetEsp extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Integer> targetRange = sgGeneral.add(new IntSetting.Builder()
        .name("target-range")
        .description("Range to find target.")
        .defaultValue(8)
        .range(1,16)
        .sliderMax(16)
        .build()
    );
    private final Setting<Boolean> self = sgGeneral.add(new BoolSetting.Builder()
        .name("self")
        .description("")
        .defaultValue(true)
        .build()
    );
    private final Setting<shape> mode = sgGeneral.add(new EnumSetting.Builder<shape>()
        .name("shape")
        .description("what shape to render")
        .defaultValue(shape.circle)
        .build()
    );
    private final Setting<Integer> range = sgGeneral.add(new IntSetting.Builder()
        .name("range")
        .defaultValue(1)
        .range(1, 5)
        .sliderMax(5)
        .build()
    );
    /// move
    private final Setting<Integer> incrementSpeed = sgGeneral.add(new IntSetting.Builder()
        .name("increment-speed")
        .defaultValue(1)
        .range(1, 20)
        .sliderMax(20)
        .build()
    );
    private final Setting<Integer> deincrementSpeed = sgGeneral.add(new IntSetting.Builder()
        .name("deincrement-speed")
        .defaultValue(1)
        .range(1, 20)
        .sliderMax(20)
        .build()
    );
    private final Setting<Integer> size = sgGeneral.add(new IntSetting.Builder()
        .name("size")
        .defaultValue(15)
        .range(1, 20)
        .sliderMax(20)
        .build()
    );
    private final Setting<SettingColor> color = sgGeneral.add(new ColorSetting.Builder()
        .name("color")
        .description("The line color of the rendering.")
        .defaultValue(new SettingColor(50, 0, 255, 18))
        .build()
    );


    public TargetEsp() {
        super(Main.RM, "target-esp", "very kewl renderer");
    }

    double currentHeight = 0.0;
    private boolean isUp = true;



    @EventHandler
    public void onTick(TickEvent.Pre event) {



        if (isUp) {
            currentHeight += incrementSpeed.get() * 0.1;
            if (currentHeight >= 2) {
                isUp = false;
            }
        } else {
            currentHeight -= deincrementSpeed.get() * 0.1;
            if (currentHeight <= 0.0) {
                isUp = true;
            }
        }

        currentHeight = Math.min(2, currentHeight);

    }


    public void renderShape(Render3DEvent event) {
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (!self.get() && player == mc.player) continue;
            if(player.isInRange(mc.player,range.get())) continue;
            Vec3d center = player.getPos();

            int segments = shapeSegment();

            double yOffset = 0.0;

            for (int i = 0; i < size.get() * 10; i++) {
                for (int j = 0; j < segments; j++) {
                    double a1 = Math.PI * 2 * j / segments;
                    double a2 = Math.PI * 2 * (j + 1) / segments;

                    double x1 = center.x + range.get() * Math.cos(a1);
                    double y1 = center.y + currentHeight + yOffset;
                    double z1 = center.z + range.get() * Math.sin(a1);

                    double x2 = center.x + range.get() * Math.cos(a2);
                    double y2 = center.y + currentHeight + yOffset;
                    double z2 = center.z + range.get() * Math.sin(a2);

                    event.renderer.line(x1, y1, z1, x2, y2, z2, color.get());
                }

                yOffset -= 0.001;
            }
        }
    }
    public int shapeSegment(){
        switch (mode.get()){
            case circle -> {
                return 40;
            }
            case nonagon -> {
                return 9;
            }
            case hexagon -> {
                return 6;
            }
            case sqaure -> {
                return 4;
            }
            case triange -> {
                return 3;
            }
        }
        return 0;
    }

    @EventHandler
    public void onRender(Render3DEvent event){
        renderShape(event);
    }

    public enum shape{/* shapes work by changing the circles segmants basically like this 0:: ->
    https://cdn1.byjus.com/wp-content/uploads/2020/01/Segment-and-Area-of-a-Segment-Of-Circle-6.png
    */
        triange,
        sqaure,
        hexagon,
        nonagon,
        circle
    }
}
