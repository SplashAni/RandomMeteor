package random.meteor.systems.modules;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.Dir;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import random.meteor.Main;

import static meteordevelopment.meteorclient.utils.entity.TargetUtils.getPlayerTarget;
import static meteordevelopment.meteorclient.utils.entity.TargetUtils.isBadTarget;

public class Blockpos extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgRender = settings.createGroup("Render");

    private final Setting<Integer> x = sgGeneral.add(new IntSetting.Builder()
            .name("x")
            .defaultValue(1)
            .range(-5,12)
            .sliderMax(12)
            .build()
    );
    private final Setting<Integer> y = sgGeneral.add(new IntSetting.Builder()
            .name("y")
            .defaultValue(1)
            .range(-5,12)
            .sliderMax(12)
            .build()
    );
    private final Setting<Integer> z = sgGeneral.add(new IntSetting.Builder()
            .name("y")
            .defaultValue(1)
            .range(-5,12)
            .sliderMax(12)
            .build()
    );
    private final Setting<Direction> direction = sgGeneral.add(new EnumSetting.Builder<Direction>()
            .name("direction")
            .description("Will try and use an axe to break target shields.")
            .defaultValue(Direction.Down)
            .build()
    );
    private final Setting<Integer> distance = sgGeneral.add(new IntSetting.Builder()
            .name("down-distance")
            .defaultValue(1)
            .range(-5,12)
            .sliderMax(12)
            .build()
    );


    private final Setting<SettingColor> tp = sgRender.add(new ColorSetting.Builder()
            .name("top")
            .description("The side color of the rendering.")
            .defaultValue(new SettingColor(225, 0, 0, 75))
            .build()
    );

    private final Setting<SettingColor> br = sgRender.add(new ColorSetting.Builder()
            .name("bottom")
            .description("The line color of the rendering.")
            .defaultValue(new SettingColor(225, 0, 0, 255))
            .build()
    );


    private final Setting<Integer> h = sgGeneral.add(new IntSetting.Builder()
        .name("height")
        .defaultValue(1)
        .range(1,4)
        .sliderMax(4)
        .build()
    );

    public Blockpos() {
        super(Main.RM,"blockpos-test","mikght stay cuz very usefull");
    }
    PlayerEntity target;
    BlockPos render;
    @EventHandler
    public void onTick(TickEvent.Pre event){
        target = getPlayerTarget(6, SortPriority.LowestDistance);
        if (isBadTarget(target, 6)) {
            return;
        }
        if(direction.get().equals(Direction.Down)) {
            render = new BlockPos(target.getBlockPos().add(x.get(), y.get(), z.get()).down(distance.get()));
        }
        else {
            render = new BlockPos(target.getBlockPos().add(x.get(), y.get(), z.get()).up(distance.get()));
        }
    }
    @EventHandler
    private void onRender(Render3DEvent event) {

        if (target == null) return;
        Color top = tp.get();
        int height = h.get();
        Color bottom = br.get();

        int x = (int) target.getX();
        int y = (int) target.getY();
        int z = (int) target.getZ();

        if (Dir.isNot(0, Dir.UP)) event.renderer.quad(x, y + height, z, x, y + height, z + 1, x + 1, y + height, z + 1, x + 1, y + height, z, top); // Top
        if (Dir.isNot(0, Dir.DOWN)) event.renderer.quad(x, y, z, x, y, z + 1, x + 1, y, z + 1, x + 1, y, z, bottom);

        if (Dir.isNot(0, Dir.NORTH)) event.renderer.gradientQuadVertical(x, y, z, x + 1, y + height, z, top, bottom);
        if (Dir.isNot(0, Dir.SOUTH)) event.renderer.gradientQuadVertical(x, y, z + 1, x + 1, y + height, z + 1, top, bottom);

        if (Dir.isNot(0, Dir.WEST)) event.renderer.gradientQuadVertical(x, y, z, x, y + height, z + 1, top, bottom);
        if (Dir.isNot(0, Dir.EAST)) event.renderer.gradientQuadVertical(x + 1, y, z, x + 1, y + height, z + 1, top, bottom);

    }

    public enum Direction{
        Down,
        Up
    }
}
