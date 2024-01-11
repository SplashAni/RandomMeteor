package random.meteor.systems.modules;

import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import random.meteor.Main;
import random.meteor.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class Anti32k extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder().name("delay").description("How many ticks between block placements.").defaultValue(5).build());
    private final Setting<Integer> range = sgGeneral.add(new IntSetting.Builder().name("range").defaultValue(6).max(6).min(1).build());
    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder().name("rotate").description("Automatically rotates you towards the city block.").defaultValue(true).build());
    private final Setting<Boolean> silentClose = sgGeneral.add(new BoolSetting.Builder().name("silent-close").defaultValue(true).build());

    public Anti32k() {
        super(Main.RM, "anti-32k", "thanks macik lol");
    }

    List<BlockPos> poses = new ArrayList<>();
    BlockPos target;
    int ticks;

    @EventHandler
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.packet instanceof BlockUpdateS2CPacket p) {
            if (p.getState().getBlock() instanceof ShulkerBoxBlock) poses.add(p.getPos());
        }
    }

    @EventHandler
    public void tick(TickEvent.Pre event) {
        if (poses.isEmpty()) {
            return;
        }

        if (poses.removeIf(this::isInvalid) || poses.isEmpty()) {
            return;
        }

        if (ticks > 0) {
            ticks--;
            return;
        }

        target = poses.get(0);

        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(
                target.toCenterPos(), Direction.UP, target, true
        ));

        poses.remove(poses.get(0));

        ticks = delay.get();
    }

    @EventHandler
    public void openScreen(OpenScreenEvent event) {
        if (!silentClose.get()) return;
        if (event.screen instanceof ShulkerBoxScreen && target != null) {
            mc.setScreen(null);
            target = null;
        }
    }

    @EventHandler
    public boolean isInvalid(BlockPos pos) {
        if (!Utils.isRange(mc.player, pos, range.get())) return true;
        return false;
    }
}
