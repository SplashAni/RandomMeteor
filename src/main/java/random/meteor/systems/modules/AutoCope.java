import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import random.meteor.Main;

import java.util.Collections;
import java.util.List;

public class AutoCope extends Module {
    private final SettingGroup sgMessages = settings.createGroup("Messages");

    private final Setting<Integer> delay = sgMessages.add(new IntSetting.Builder()
            .name("delay")
            .description("The delay between specified messages in seconds.")
            .defaultValue(2)
            .min(0)
            .sliderMax(20)
            .build()
    );
    private final Setting<List<String>> copeMessages = sgMessages.add(new StringListSetting.Builder()
            .name("messages")
            .defaultValue("I lagged", "I have skill issue", "Im trash")
            .build()
    );

    private long ticks = 0;

    public AutoCope() {
        super(Main.RM, "auto-cope", "");
    }

    @EventHandler
    private void onPacketReceive(PacketEvent.Receive event) {
        if (event.packet instanceof DeathMessageS2CPacket packet) {
            Entity entity = mc.world.getEntityById(packet.getEntityId());
            if (entity == mc.player) {
                List<String> messages = copeMessages.get();
                if (!messages.isEmpty()) {
                    long timer = System.currentTimeMillis();
                    if (timer - ticks >= delay.get() * 1000L) {
                        Collections.shuffle(messages);
                        String randomMessage = messages.get(0);
                        ChatUtils.sendPlayerMsg(randomMessage);
                        ticks = timer;
                    }
                }
            }
        }
    }
}
