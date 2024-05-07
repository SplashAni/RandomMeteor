package random.meteor.systems.modules;

import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector3d;
import random.meteor.Main;
import random.meteor.systems.Mod;

import static meteordevelopment.meteorclient.utils.entity.TargetUtils.getPlayerTarget;
import static meteordevelopment.meteorclient.utils.entity.TargetUtils.isBadTarget;

public class BurrowEsp extends Mod {
    private final SettingGroup sgRender = settings.createGroup("Render");

    private final Setting<Integer> range = sgRender.add(new IntSetting.Builder()
            .name("range")
            .description("")
            .defaultValue(12)
            .range(1, 25)
            .sliderMax(25)
            .build()
    );
    private final Setting<Boolean> renderText = sgRender.add(new BoolSetting.Builder()
            .name("render-text")
            .defaultValue(false)
            .build()
    );
    private final Setting<Double> scale = sgRender.add(new DoubleSetting.Builder()
            .name("scale")
            .defaultValue(1.5)
            .sliderRange(0.01, 3)
            .build());

    private final Setting<String> text = sgRender.add(new StringSetting.Builder()
            .name("Text")
            .defaultValue("Burrowed")
            .visible(renderText::get)
            .build());

    private final Setting<SettingColor> textColor = sgRender.add(new ColorSetting.Builder()
            .name("text-color")
            .defaultValue(new SettingColor(1, 1, 1, 255))
            .visible(renderText::get)
            .build()
    );

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

    public BurrowEsp() {
        super( "burrow-esp", "Renders a block that the target is burrowed into");
    }

    private BlockPos pos;
    PlayerEntity target;

    @Override
    public void onActivate() {
        pos = null;
        super.onActivate();
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {

        target = getPlayerTarget(range.get(), SortPriority.LowestDistance);

        if (isBadTarget(target, range.get())) {
            return;
        }

        pos = isValid(target.getBlockPos()) ? target.getBlockPos() : null;
    }

    @EventHandler
    public void on2DRender(Render2DEvent event) {
        if (pos == null) return;

        Vector3d vec = new Vector3d(this.pos.toCenterPos().toVector3f());

        vec.set(vec);

        if (NametagUtils.to2D(vec, scale.get())) {
            NametagUtils.begin(vec);
            TextRenderer.get().begin(1.0, false, true);

            TextRenderer.get().render(text.toString(), -TextRenderer.get().getWidth(text.toString()) / 2, 0.0, textColor.get(), true);

            TextRenderer.get().end();
            NametagUtils.end();

        }
    }

    public boolean isValid(BlockPos vec) {
        return (mc.world != null ? mc.world.getBlockState(vec).getBlock().getBlastResistance() : 0) >= 1200 || mc.world.getBlockState(vec).getBlock() == Blocks.COBWEB;
    }

    @EventHandler
    public void onRender(Render3DEvent event) {
        if (pos == null) return;
        event.renderer.box(pos, sideColor.get(), lineColor.get(), shapeMode.get(), 0);
    }
}
