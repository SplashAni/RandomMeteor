package random.meteor.systems.modules;

import meteordevelopment.meteorclient.events.entity.player.StartBreakingBlockEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import org.joml.Vector3d;
import random.meteor.systems.Mod;

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
    private final Setting<mineMode> remineMode = sgGeneral.add(new EnumSetting.Builder<mineMode>()
        .name("remine-mode")
        .visible(remine::get)
        .defaultValue(mineMode.Instant)
        .build()
    );
    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
        .name("rotate")
        .defaultValue(false)
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
    public BlockPos pos;
    public Direction direction;
    public float progress;
    public boolean update;
    boolean mining;

    public AutoMine() {
        super("auto-mine", "insane");
    }

    @EventHandler
    private void onStartBreakingBlock(StartBreakingBlockEvent event) {

        if (!BlockUtils.canBreak(event.blockPos)) return;

        boolean canMine = false;
        
        if (pos != null) {
            if (event.blockPos != pos) {
                Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, pos, direction));
            }
            canMine = true;
        }

        if(!canMine) return;;
        direction = event.direction;
        pos = event.blockPos;
        progress = 0f;
        sendAction(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK);
        mining = true;
    }


    @EventHandler
    public void sendPacket(PacketEvent.Sent sent) {

        if (sent.packet instanceof UpdateSelectedSlotC2SPacket packet
            && packet.getSelectedSlot() != mc.player.getInventory().selectedSlot
            && progress >= 1.0f) {
            update = true;
        }

    }


    @EventHandler
    public void onTick(TickEvent.Pre event) {

        if (update) {
            mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(
                mc.player.getInventory().selectedSlot));
            update = false;
        }
        if (shouldPause()) return;

        if (pos != null && !mining) {
            if (mc.world.getBlockState(pos).isReplaceable()) return;
            switch (remineMode.get()) {
                case Instant -> {
                    RenderUtils.renderTickingBlock(
                        pos, sideColor.get(),
                        lineColor.get(), shapeMode.get(),
                        0, 10, true,
                        true
                    );
                    sendAction(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK);
                    update = true;
                }
                case Normal -> {
                    progress = 0.0f;
                    mining = true;
                }
            }
        }


        if (pos == null || direction == null) return;

        if (progress >= 1.0f) {
            sendAction(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK);

            if (mc.world.isAir(pos)) {
                mining = false;
            } else {
                FindItemResult tool = InvUtils.findFastestTool(mc.world.getBlockState(pos));

                mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(tool.slot()));
            }


        } else {
            mc.player.swingHand(Hand.MAIN_HAND);
            FindItemResult tool = InvUtils.findFastestTool(mc.world.getBlockState(pos));

            progress += (float) BlockUtils.getBreakDelta(tool.slot() != -1 ? tool.slot() : Objects.requireNonNull(mc.player).getInventory().selectedSlot, mc.world.getBlockState(pos));

        }
    }


    public boolean shouldPause() {
        return PlayerUtils.shouldPause(pauseMine.get(), pauseEat.get(), pauseDrink.get());
    }

    public void sendAction(PlayerActionC2SPacket.Action action) {
        mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(action, pos, direction));
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
