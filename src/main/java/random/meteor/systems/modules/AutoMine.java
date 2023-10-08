package random.meteor.systems.modules;

import meteordevelopment.meteorclient.events.entity.player.StartBreakingBlockEvent;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3d;
import random.meteor.Main;
import random.meteor.utils.Utils;

import java.util.Objects;

public class AutoMine extends Module {
    public enum mineMode {
        Instant,
        Normal
    }
    public enum switchMode{
        None,
        Normal,
        Silent,
        SwapSilent
    }
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
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
    public AutoMine() {
        super(Main.RM, "auto-mine", "insane");
    }

    public BlockPos pos,prev;

    public float progress = 0.0f;

    @EventHandler
    private void onStartBreakingBlock(StartBreakingBlockEvent event) {
        if (!BlockUtils.canBreak(event.blockPos)) return;
        pos = event.blockPos;
        progress = 0.0f;
        event.cancel();
    }

    boolean didMine = false;

    @EventHandler
    public void onTick(TickEvent.Pre event) {

        if (prev != null && remine.get())
            if (Utils.state(prev) != Blocks.AIR) switch (remineMode.get()) {
                case Normal -> {
                    pos = prev;
                    prev = null;
                }
                case Instant ->
                        mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, prev, Direction.UP));
            }

        if(didMine) {
            assert mc.world != null;
            if(mc.world.getBlockState(pos).isAir()){
                Entity e = Utils.getCrystal(pos);
                if(e != null){
                    assert mc.player != null;
                    mc.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(e, mc.player.isSneaking()));
                }
            }


            Utils.updateHotbar();
            progress = 0f;
            prev = pos;
            pos = null;
            didMine = false;
        }


        if (pos  == null) return;

        assert mc.world != null;
        FindItemResult tool = InvUtils.findFastestTool(mc.world.getBlockState(pos));

        progress += BlockUtils.getBreakDelta(tool.slot() != -1 ? tool.slot() : Objects.requireNonNull(mc.player).getInventory().selectedSlot, mc.world.getBlockState(pos));

        if(swapMode.get().equals(switchMode.Normal)){
            InvUtils.swap(tool.slot(),true);
        }

        Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, Direction.UP));
        mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, Direction.UP));

        if (progress >= 1) {

            if (tool.slot() != -1) {

                doCrystal();

                assert mc.player != null;

                switch (swapMode.get()){
                    case Silent -> mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(tool.slot()));
                    case SwapSilent -> Utils.move(mc.player.getInventory().selectedSlot,tool.slot());
                }

            }

            if(swapMode.get().equals(switchMode.SwapSilent) && tool.slot() != -1) {
                assert mc.player != null;
                Utils.move(mc.player.getInventory().selectedSlot,tool.slot());
            }

            didMine = true;
        }
    }
    public void doCrystal(){

        FindItemResult crystal = InvUtils.findInHotbar(Items.END_CRYSTAL);

        if((Utils.state(pos) == Blocks.OBSIDIAN) && crystal.isHotbar()) {

            if (Utils.getCrystal(pos) == null) {

                InvUtils.swap(crystal.slot(), true);
                mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(new Vec3d(pos.toCenterPos().toVector3f()), Direction.UP, pos, true));
                InvUtils.swapBack();

            }


        }
    }
    @EventHandler
    private void onRender2D(Render2DEvent event) {
        if (pos == null || progress == Float.POSITIVE_INFINITY || progress > 100)  return;
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
}