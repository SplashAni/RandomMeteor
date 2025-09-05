package random.meteor.systems.modules;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import random.meteor.util.setting.groups.PlaceSettingGroup;
import random.meteor.util.setting.groups.RangeSettingGroup;
import random.meteor.util.setting.groups.SwapSettingGroup;
import random.meteor.util.setting.groups.SwingSettingGroup;
import random.meteor.util.system.Category;
import random.meteor.util.system.Mod;
import random.meteor.util.world.PathFinder;
import random.meteor.util.world.BlockUtil;

import java.util.List;

public class FeetTrap extends Mod {
    private final SettingGroup sgRender = settings.createGroup("Render");

    // Render

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


    PlaceSettingGroup placeSettingGroup = new PlaceSettingGroup(this);
    RangeSettingGroup rangeSettingGroup = new RangeSettingGroup(this);
    SwapSettingGroup swapSettingGroup = new SwapSettingGroup(this);
    SwingSettingGroup swingSettingGroup = new SwingSettingGroup(this);

    PathFinder pathFinder;

    @Override
    public void onActivate() {
        pathFinder = new PathFinder();
        super.onActivate();
    }

    public FeetTrap() {
        super("feet-trap", Category.PVP);
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {

        BlockPos offset = mc.player.getBlockPos().offset(Direction.DOWN, 1);
        BlockUtil.place(offset, placeSettingGroup, rangeSettingGroup, swapSettingGroup, swingSettingGroup);
    }
    @EventHandler
    public void render3d(Render3DEvent event){
        BlockPos target = mc.player.getBlockPos().offset(Direction.DOWN, 1);

        List<BlockPos> path = pathFinder.getPath(target, 5);
        for (BlockPos pos : path) {
            event.renderer.box(pos, sideColor.get(), lineColor.get(), shapeMode.get(), 0);
        }

    }

}
