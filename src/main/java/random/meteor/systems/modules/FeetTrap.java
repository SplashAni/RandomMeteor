package random.meteor.systems.modules;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector3d;
import random.meteor.util.player.PlayerUtil;
import random.meteor.util.render.RenderMode;
import random.meteor.util.render.RenderShape;
import random.meteor.util.render.RenderUtil;
import random.meteor.util.setting.groups.*;
import random.meteor.util.system.Category;
import random.meteor.util.system.Mod;
import random.meteor.util.world.BlockUtil;
import random.meteor.util.world.PathFinder;
import random.meteor.util.world.TimerUtil;

import java.util.ArrayList;
import java.util.List;

public class FeetTrap extends Mod {
    private final SettingGroup sgRender = settings.createGroup("Render");

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

    PlaceSettingGroup placeSettingGroup;
    RangeSettingGroup rangeSettingGroup;
    DelaySettingGroup delaySettingGroup;
    SwapSettingGroup swapSettingGroup;
    SwingSettingGroup swingSettingGroup;

    PathFinder pathFinder;
    double ticks;
    List<BlockPos> poses = new ArrayList<>();
    public FeetTrap() {
        super("feet-trap", Category.PVP);

        placeSettingGroup = register(PlaceSettingGroup.class);
        rangeSettingGroup = register(RangeSettingGroup.class);
        delaySettingGroup = register(DelaySettingGroup.class);
        swapSettingGroup = register(SwapSettingGroup.class);
        swingSettingGroup = register(SwingSettingGroup.class);
    }


    @Override
    public void onActivate() {
        pathFinder = new PathFinder();
        super.onActivate();
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {

        poses = PlayerUtil.getNeighbours();
        if (poses.isEmpty()) return;

        if (ticks > 0) {
            ticks = TimerUtil.updateTimer(ticks, delaySettingGroup, this);
            return;
        }


        if (placeSettingGroup.instant.get()) {
            for (BlockPos pose : poses) {
                ticks = TimerUtil.applyDelay(
                    BlockUtil.place(pose, placeSettingGroup, rangeSettingGroup, swapSettingGroup, swingSettingGroup, this),
                    delaySettingGroup
                );
            }
        } else {
            ticks = TimerUtil.applyDelay(
                BlockUtil.place(poses.getFirst(), placeSettingGroup, rangeSettingGroup, swapSettingGroup, swingSettingGroup, this),
                delaySettingGroup
            );
        }
    }

    @EventHandler
    public void render(Render3DEvent event) {
        if (poses.isEmpty()) return;

        for (int i = 0; i < poses.size(); i++) {

        /*    Vector3d vec3 = new Vector3d();
            vec3.set(poses.get(i).getX() + 0.5, poses.get(i).getY() + 0.5, poses.get(i).getZ() + 0.5);

            if (NametagUtils.to2D(vec3, 1)) {
                NametagUtils.begin(vec3);
                TextRenderer.get().begin(1, false, true);

                String text = "hi";
                double w = TextRenderer.get().getWidth(text) / 2;
                TextRenderer.get().render(text, -w, 0,Color.WHITE, true);

                TextRenderer.get().end();
                NametagUtils.end();
            }
*/
            RenderUtil.renderBlock(event, poses.get(i), RenderShape.Normal, RenderMode.Both, new Color(255, 0, 0, 100), Color.RED);
        }

    }


}
