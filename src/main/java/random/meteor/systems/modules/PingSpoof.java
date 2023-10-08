package random.meteor.systems.modules;

import meteordevelopment.meteorclient.events.entity.player.InteractBlockEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.network.packet.c2s.play.KeepAliveC2SPacket;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class PingSpoof extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> ping = sgGeneral.add(new IntSetting.Builder()
        .name("ping")
        .defaultValue(500)
        .sliderMin(100)
        .sliderMax(10000)
        .build()
    );
    private final Setting<Boolean> delayActions = sgGeneral.add(new BoolSetting.Builder()
        .name("delay-actions")
        .defaultValue(true)
        .build()
    );
    private final Setting<Boolean> blocks = sgGeneral.add(new BoolSetting.Builder()
        .name("blocks")
        .defaultValue(true)
        .visible(delayActions::get)
        .build()
    );

    private final List<KeepAliveC2SPacket> keepAlivePackets = new ArrayList<>();
    private final List<task> actions = new ArrayList<>();
    private boolean jitterSpoof = false;

    public PingSpoof() {
        super(Categories.Misc, "ping-spoof", "");
    }

    @Override
    public void onActivate() {
        keepAlivePackets.clear();
    }
    int ticks;
    @EventHandler
    public void onTick(TickEvent.Pre event) {
        jitterSpoof = !jitterSpoof;


        if(blocks.get()) {
            ticks++;
            if (PlayerUtils.getPing()  >= ticks) {
                for (task action : actions) {
                    FindItemResult yes = InvUtils.find(action.block.asItem());
                    BlockUtils.place(action.pos, yes, false, 50, true);
                    actions.remove(action);
                }
                ticks = 0;
            }
        }

    }
    @EventHandler
    public void onInteract(InteractBlockEvent event){
        if(!blocks.get()) return;

        actions.add(new task(event.result.getBlockPos(), (Block) mc.player.getHandItems()));
        event.cancel();
    }

    @Override
    public void onDeactivate() {
        long currentTime = System.currentTimeMillis();
        Iterator<KeepAliveC2SPacket> iterator = keepAlivePackets.iterator();

        while (iterator.hasNext()) {
            KeepAliveC2SPacket packet = iterator.next();
            if (System.currentTimeMillis() + ping.get() <= currentTime) {
                mc.getNetworkHandler().sendPacket(packet);
                iterator.remove();
            }
        }
    }
    @EventHandler
    private void onSendPacket(PacketEvent.Send event) {
        if (event.packet instanceof KeepAliveC2SPacket packet) {
            if (keepAlivePackets.contains(packet)) {
                keepAlivePackets.remove(packet);
            } else {
                keepAlivePackets.add(packet);
                event.cancel();
            }
        }
    }

    @EventHandler
    private void onReceivePacket(PacketEvent.Receive event) {
        if (jitterSpoof) {
            long time = System.currentTimeMillis();
            Iterator<KeepAliveC2SPacket> iterator = keepAlivePackets.iterator();

            while (iterator.hasNext()) {
                KeepAliveC2SPacket packet = iterator.next();
                if (System.currentTimeMillis() + ping.get() <= time) {
                    mc.getNetworkHandler().sendPacket(packet);
                    iterator.remove();
                    break;
                }
            }
        } else {
            int jitter = ping.get() + (int) (Math.random() * 100) - 50;
            long time = System.currentTimeMillis();

            Iterator<KeepAliveC2SPacket> iterator = keepAlivePackets.iterator();

            while (iterator.hasNext()) {
                KeepAliveC2SPacket packet = iterator.next();
                if (System.currentTimeMillis() + jitter <= time) {
                    Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(packet);
                    iterator.remove();
                    break;
                }
            }
        }
    }
    public record task(BlockPos pos , Block block){}
}
