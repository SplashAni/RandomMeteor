package random.meteor.systems.modules.RM;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
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

    //RENDER
    private final Setting<ShapeMode> shapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>()
            .name("shape-mode")
            .description("How the shapes are rendered.")
            .defaultValue(ShapeMode.Both)
            .build()
    );

    private final Setting<SettingColor> sideColor = sgRender.add(new ColorSetting.Builder()
            .name("side-color")
            .description("The side color of the rendering.")
            .defaultValue(new SettingColor(225, 0, 0, 75))
            .build()
    );

    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder()
            .name("line-color")
            .description("The line color of the rendering.")
            .defaultValue(new SettingColor(225, 0, 0, 255))
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

        event.renderer.box(render, sideColor.get(), lineColor.get(), shapeMode.get(), 0);
    }

    public enum Direction{
        Down,
        Up
    }
}
