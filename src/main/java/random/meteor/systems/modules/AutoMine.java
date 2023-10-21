package random.meteor.systems.modules;

import meteordevelopment.meteorclient.events.entity.player.StartBreakingBlockEvent;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.*;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3d;
import random.meteor.Main;
import random.meteor.utils.Utils;
import random.meteor.utils.enums.RenderMode;

import java.util.Objects;

public class AutoMine extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
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
    private final Setting<Boolean> placeCrystal = sgGeneral.add(new BoolSetting.Builder()
            .name("place-crystal")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> breakCrystal = sgGeneral.add(new BoolSetting.Builder()
            .name("break-crystal")
            .defaultValue(true)
            .build()
    );
    private final Setting<Integer> maxSelfDmg = sgGeneral.add(new IntSetting.Builder()
            .name("max-self-dmg")
            .description("")
            .defaultValue(5)
            .range(1,36)
            .sliderMax(36)
            .visible(breakCrystal::get)
            .build()
    );
    private final Setting<mineMode> remineMode = sgGeneral.add(new EnumSetting.Builder<mineMode>()
            .name("remine-mode")
            .visible(remine::get)
            .defaultValue(mineMode.Instant)
            .build()
    );
    private final Setting<Boolean> renderProgress = sgGeneral.add(new BoolSetting.Builder()
            .name("progress")
            .defaultValue(true)
            .build()
    );
    public final Setting<RenderMode> renderMode = sgRender.add(new EnumSetting.Builder<RenderMode>()
            .name("render-mode")
            .defaultValue(RenderMode.BreakIndicators)
            .build()
    );

    private final Setting<ShapeMode> shapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>()
            .name("shape-mode")
            .description("How the shapes are rendered.")
            .defaultValue(ShapeMode.Both)
            .visible(() -> renderMode.get() == RenderMode.Custom)
            .build()
    );

    private final Setting<SettingColor> sideColor = sgRender.add(new ColorSetting.Builder()
            .name("side-color")
            .description("The side color of the rendering.")
            .defaultValue(new SettingColor(225, 0, 0, 75))
            .visible(() -> renderMode.get() == RenderMode.Custom)
            .build()
    );

    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder()
            .name("line-color")
            .description("The line color of the rendering.")
            .defaultValue(new SettingColor(225, 0, 0, 255))
            .visible(() -> renderMode.get() == RenderMode.Custom)
            .build()
    );

    public AutoMine() {
        super(Main.RM, "auto-mine", "insane");
    }

    public BlockPos pos, prev;

    public float progress = 0.0f;

    @EventHandler
    private void onStartBreakingBlock(StartBreakingBlockEvent event) {
        if (!BlockUtils.canBreak(event.blockPos)) return;
        pos = event.blockPos;
        progress = 0.0f;
    }

    boolean didMine = false;

    boolean canClear;
    @EventHandler
    public void onTick(TickEvent.Pre event) {

        if(canClear){
            for (Entity e : Objects.requireNonNull(mc.world).getEntities()) {
                if (e instanceof EndCrystalEntity) {

                    double selfDmg = DamageUtils.crystalDamage(mc.player, e.getPos(), false, e.getBlockPos(),true);

                    if(selfDmg > maxSelfDmg.get()) continue;

                    if (PlayerUtils.isWithin(e, 5)) mc.interactionManager.attackEntity(mc.player, e);
                }
            }
        }

        if (prev != null && remine.get())
            if (Utils.state(prev) != Blocks.AIR) switch (remineMode.get()) {
                case Normal -> {
                    pos = prev;
                    prev = null;
                }
                case Instant -> {
                    FindItemResult tool = InvUtils.findFastestTool(Objects.requireNonNull(mc.world).getBlockState(prev));

                    if (tool.slot() != -1) {
                        InvUtils.swap(tool.slot(), true);
                    }

                    if (placeCrystal.get()) doCrystal(prev);

                    if (rotate.get()) Rotations.rotate(Rotations.getYaw(prev), Rotations.getPitch(prev));

                    Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, prev, Direction.UP));

                    canClear = true;

                    InvUtils.swapBack();

                    canClear = false;
                }
            }

        if (didMine) {
            Utils.updateHotbar();
            progress = 0f;
            prev = pos;
            pos = null;
            didMine = false;
            canClear = false;
        }


        if (pos == null) return;

        assert mc.world != null;
        FindItemResult tool = InvUtils.findFastestTool(mc.world.getBlockState(pos));

        progress += BlockUtils.getBreakDelta(tool.slot() != -1 ? tool.slot() : Objects.requireNonNull(mc.player).getInventory().selectedSlot, mc.world.getBlockState(pos));

        if (swapMode.get().equals(switchMode.Normal)) {
            InvUtils.swap(tool.slot(), true);
        }

        if (rotate.get()) Rotations.rotate(Rotations.getYaw(pos), Rotations.getPitch(pos));

        Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, Direction.UP));
        mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, Direction.UP));

        if (progress >= 1) {

            if (tool.slot() != -1) {

                if (placeCrystal.get()) doCrystal(pos);

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
            canClear = true;


            didMine = true;
        }
    }

    public void doCrystal(BlockPos pos) {

        FindItemResult crystal = InvUtils.findInHotbar(Items.END_CRYSTAL);

        if ((Utils.state(pos) == Blocks.OBSIDIAN) && crystal.isHotbar()) {

            if (Utils.getCrystal(pos) == null) {

                InvUtils.swap(crystal.slot(), true);
                Objects.requireNonNull(mc.interactionManager).interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(new Vec3d(pos.toCenterPos().toVector3f()), Direction.UP, pos, true));
                InvUtils.swapBack();

            }


        }
    }

    @EventHandler
    private void onRender2D(Render2DEvent event) {

        if (progress * 100 > 100 || !renderProgress.get()) return;

        if (pos == null || progress == Float.POSITIVE_INFINITY) return;
        Vector3d v = new Vector3d();
        v.set(pos.toCenterPos().x, pos.toCenterPos().y, pos.toCenterPos().z);

        if (NametagUtils.to2D(v, 1)) {
            NametagUtils.begin(v);
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
        if (renderMode.get() != RenderMode.Custom) return;
        event.renderer.box(pos, sideColor.get(), lineColor.get(), shapeMode.get(), 0);

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