package random.meteor.systems.modules;

import meteordevelopment.meteorclient.events.entity.player.StartBreakingBlockEvent;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import org.joml.Vector3d;
import random.meteor.systems.Mod;
import random.meteor.utils.Utils;

import java.util.Objects;


public class AutoMine extends Mod {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgCrystal = settings.createGroup("Crystal");
    private final SettingGroup sgPause = settings.createGroup("Pause");
    private final SettingGroup sgRender = settings.createGroup("Render");

    private final Setting<switchMode> swapMode = sgGeneral.add(new EnumSetting.Builder<switchMode>()
        .name("swap-mode")
        .defaultValue(switchMode.Silent)
        .build()
    );

    private final Setting<Boolean> remine = sgGeneral.add(new BoolSetting.Builder()
        .name("auto-remine")
        .defaultValue(true)
        .build()
    );
    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
        .name("rotate")
        .defaultValue(false)
        .build()
    );


    private final Setting<mineMode> remineMode = sgGeneral.add(new EnumSetting.Builder<mineMode>()
        .name("remine-mode")
        .visible(remine::get)
        .defaultValue(mineMode.Instant)
        .build()
    );


    /*crystal*/
    private final Setting<Boolean> placeCrystal = sgCrystal.add(new BoolSetting.Builder()
        .name("place-crystal")
        .defaultValue(true)
        .build()
    );
    private final Setting<Boolean> breakCrystal = sgCrystal.add(new BoolSetting.Builder()
        .name("break-crystal")
        .defaultValue(true)
        .build()
    );
    private final Setting<Integer> maxSelfDmg = sgCrystal.add(new IntSetting.Builder()
        .name("max-self-dmg")
        .description("")
        .defaultValue(5)
        .range(1, 36)
        .sliderMax(36)
        .visible(breakCrystal::get)
        .build()
    );

    /*PAUSE*/
    private final Setting<Boolean> pauseMine = sgPause.add(new BoolSetting.Builder()
        .name("pause-on-mine")
        .description("")
        .defaultValue(true)
        .build()
    );
    private final Setting<Boolean> pauseEat = sgPause.add(new BoolSetting.Builder()
        .name("pause-on-eat")
        .description("")
        .defaultValue(true)
        .build()
    );
    private final Setting<Boolean> pauseDrink = sgPause.add(new BoolSetting.Builder()
        .name("pause-on-drink")
        .description("")
        .defaultValue(true)
        .build()
    );


    /*render*/
    private final Setting<Boolean> renderProgress = sgGeneral.add(new BoolSetting.Builder()
        .name("progress")
        .defaultValue(true)
        .build()
    );
    private final Setting<Boolean> render = sgRender.add(new BoolSetting.Builder()
        .name("render")
        .defaultValue(true)
        .build()
    );

    private final Setting<ShapeMode> shapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>()
        .name("shape-mode")
        .description("How the shapes are rendered.")
        .defaultValue(ShapeMode.Both)
        .visible(render::get)
        .build()
    );

    private final Setting<SettingColor> sideColor = sgRender.add(new ColorSetting.Builder()
        .name("side-color")
        .description("The side color of the rendering.")
        .defaultValue(new SettingColor(225, 0, 0, 75))
        .visible(render::get)
        .build()
    );

    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder()
        .name("line-color")
        .description("The line color of the rendering.")
        .defaultValue(new SettingColor(225, 0, 0, 255))
        .visible(render::get)
        .build()
    );

    public AutoMine() {
        super( "auto-mine", "insane");
    }

    public BlockPos pos;
    public float progress;
    boolean didMine, update;

    @EventHandler
    private void onStartBreakingBlock(StartBreakingBlockEvent event) {

        if (!BlockUtils.canBreak(event.blockPos)) return;
        if (pos != null) {
            Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, Direction.UP));
        }

        pos = event.blockPos;
        update = false;
        progress = 0f;
    }


    @EventHandler
    public void onTick(TickEvent.Pre event) {


        if (didMine) {
            Utils.updateHotbar();
            progress = 0f;
            pos = null;
            didMine = false;
        }

        if (pos == null) return;

        assert mc.world != null;
        FindItemResult tool = InvUtils.findFastestTool(mc.world.getBlockState(pos));

        progress += BlockUtils.getBreakDelta(tool.slot() != -1 ? tool.slot() : Objects.requireNonNull(mc.player).getInventory().selectedSlot, mc.world.getBlockState(pos));

        if (swapMode.get().equals(switchMode.Normal)) {
            InvUtils.swap(tool.slot(), true);
        }

        if (rotate.get()) Rotations.rotate(Rotations.getYaw(pos), Rotations.getPitch(pos));

        if (shouldPause() || mc.player.getMovementSpeed() > 0) return;

        Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, Direction.UP));
        Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, Direction.UP));

        if (progress >= 1) {
            update = true;
            mc.particleManager.addBlockBreakParticles(pos, mc.world.getBlockState(pos));
            if (tool.slot() != -1) {

                assert mc.player != null;

                switch (swapMode.get()) {
                    case Silent -> mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(tool.slot()));
                    case SwapSilent -> Utils.move(mc.player.getInventory().selectedSlot, tool.slot());
                }

            }

            if (swapMode.get().equals(switchMode.SwapSilent) && tool.slot() != -1) {
                assert mc.player != null;
                Utils.move(mc.player.getInventory().selectedSlot, tool.slot());
            }

            if (Utils.state(pos) != Blocks.AIR) {
                return;
            }

            didMine = true;

        }
    }


    public boolean shouldPause() {
        return PlayerUtils.shouldPause(pauseMine.get(), pauseEat.get(), pauseDrink.get());
    }

    @EventHandler
    private void onRender2D(Render2DEvent event) {

        if (progress >= 1 || pos == null) return;


        Vector3d vector = new Vector3d();
        vector.set(pos.toCenterPos().x, pos.toCenterPos().y, pos.toCenterPos().z);

        if (NametagUtils.to2D(vector, 1)) {
            NametagUtils.begin(vector);
            TextRenderer.get().begin(1.2, false, true);

            String text = String.format("%.0f%%", progress * 100);
            double w = TextRenderer.get().getWidth(text) / 2;
            TextRenderer.get().render(text, -w, 0, Color.WHITE, true);

            TextRenderer.get().end();
            NametagUtils.end();
        }
    }

    @EventHandler
    public void onRender(Render3DEvent event) {
        if (pos == null) return;
        if (!render.get() || !renderProgress.get()) return;
        assert mc.world != null;
        VoxelShape shape = mc.world.getBlockState(pos).getOutlineShape(mc.world, pos);
        if (shape == null || shape.isEmpty()) return;

        Box orig = shape.getBoundingBox();


        double factor = progress > 1 ? 1 : progress * 2;
        renderBlock(event, orig, pos, factor);
    }

    private void renderBlock(Render3DEvent event, Box orig, BlockPos pos, double shrinkFactor) {
        Box box = orig.shrink(
            orig.getLengthX() * shrinkFactor,
            orig.getLengthY() * shrinkFactor,
            orig.getLengthZ() * shrinkFactor
        );

        double xShrink = (orig.getLengthX() * shrinkFactor) / 2;
        double yShrink = (orig.getLengthY() * shrinkFactor) / 2;
        double zShrink = (orig.getLengthZ() * shrinkFactor) / 2;

        double x1 = pos.getX() + box.minX + xShrink;
        double y1 = pos.getY() + box.minY + yShrink;
        double z1 = pos.getZ() + box.minZ + zShrink;
        double x2 = pos.getX() + box.maxX + xShrink;
        double y2 = pos.getY() + box.maxY + yShrink;
        double z2 = pos.getZ() + box.maxZ + zShrink;

        event.renderer.box(x1, y1, z1, x2, y2, z2, sideColor.get(), lineColor.get(), shapeMode.get(), 0);
    }

    public enum mineMode {
        Instant,
        Normal
    }

    public enum switchMode {
        None,
        Normal,
        Silent,
        SwapSilent
    }
}
