package random.meteor.systems.modules.RM;

import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector3d;
import random.meteor.Main;

import java.awt.*;
import java.util.Collections;
import java.util.List;

import static meteordevelopment.meteorclient.utils.entity.TargetUtils.getPlayerTarget;
import static meteordevelopment.meteorclient.utils.entity.TargetUtils.isBadTarget;

public class BurrowEsp extends Module {
    private final SettingGroup sgRender = settings.createGroup("Render");

    private final Setting<Integer> range = sgRender.add(new IntSetting.Builder()
            .name("range")
            .description("")
            .defaultValue(12)
            .range(1,25)
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

    private final Setting<List<String>> text = sgRender.add(new StringListSetting.Builder()
            .name("Burrowed")
            .defaultValue(Collections.emptyList())
            .visible(renderText::get)
            .build());

    //real render
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
        super(Main.RM,"burrow-esp","good idea vh");
    }
    private BlockPos burrowPos;
    PlayerEntity target;
    boolean isBurrowed;
    @Override
    public void onActivate() {
        burrowPos = null;
        isBurrowed = false;
        super.onActivate();
    }

    @EventHandler
    public void onTick(TickEvent.Pre event){
        target = getPlayerTarget(range.get(), SortPriority.LowestDistance);
        if (isBadTarget(target, range.get())) {
            return;
        }
        burrowPos = target.getBlockPos();

        BlockState state = mc.world.getBlockState(burrowPos);isBurrowed = state.getBlock().getBlastResistance() >= 1200 || state.getBlock() == Blocks.COBWEB; // IMAGINE USING 3 METHODS FOR THIS *COUGH COUHG B+
    }
    @EventHandler
    public void on2DRender(Render2DEvent event) {
        if(burrowPos != null && isBurrowed) {
            Vector3d pos = new Vector3d(burrowPos.getX() + 0.5, burrowPos.getY() + 0.5, burrowPos.getZ() + 0.5);
            if (NametagUtils.to2D(pos, scale.get())) {
                NametagUtils.begin(pos);
                TextRenderer.get().begin(1.0, false, true);
                TextRenderer.get().render(text.toString(), TextRenderer.get().getWidth(text.toString()) / 2.0, 0.0, textColor.get(), true);
                TextRenderer.get().end();
                NametagUtils.end();
            }
        }
    }

    @EventHandler
    public void onRender(Render3DEvent event){
        if (!isBurrowed) return;

        event.renderer.box(burrowPos, sideColor.get(), lineColor.get(), shapeMode.get(), 0);

    }
}
