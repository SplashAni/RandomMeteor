package random.meteor.systems.modules;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import random.meteor.systems.Mod;
import random.meteor.utils.PistonUtils;

import java.util.Map;
import java.util.Objects;

public class PistonAura extends Mod {
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
    AuraPosition auraPosition;
    PistonUtils pistonUtils;


    public PistonAura() {
        super("piston-aura", "pisaton moment");
    }

    @Override
    public void onActivate() {
        pistonUtils = new PistonUtils();
        super.onActivate();
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        calc();

    }

    public void calc() {
        PlayerEntity target = mc.player;

        BlockPos crystalPos, redstonePos, pistonPos;

        Map<Direction, BlockPos> placeableMap = pistonUtils.getValidPosition(Objects.requireNonNull(target));

        if (placeableMap != null && !placeableMap.isEmpty()) {
            Map.Entry<Direction, BlockPos> entry = placeableMap.entrySet().stream().findFirst().orElse(null);

            crystalPos = entry.getValue();
            Direction direction = entry.getKey();
            pistonPos = crystalPos.offset(direction).up();
            redstonePos = pistonPos.offset(direction);

            this.auraPosition = new AuraPosition(
                crystalPos, pistonPos, redstonePos
            );
        } else {
            this.auraPosition = null;
        }

    }


    @EventHandler
    private void onRender(Render3DEvent event) {
        if (auraPosition == null) return;

        event.renderer.box(auraPosition.crystalPos, new Color(77, 9, 255, 131), lineColor.get(), shapeMode.get(), 0);
        event.renderer.box(auraPosition.pistonBlock, new Color(210, 255, 9, 131), lineColor.get(), shapeMode.get(), 0);

        event.renderer.box(auraPosition.redstonePos, new Color(255, 9, 9, 131), lineColor.get(), shapeMode.get(), 0);

    }

    public record AuraPosition(BlockPos crystalPos, BlockPos pistonBlock, BlockPos redstonePos) {

    }

}
