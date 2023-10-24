package random.meteor.systems.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.PlayerListEntry;
import random.meteor.Main;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class MessageBomb extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
            .name("delay")
            .description("Delay in ticks between message bombs.")
            .defaultValue(1200) // 60 secds cuz 20 tps
            .min(1)
            .sliderMax(5000)
            .build()
    );
    private final Setting<String> text = sgGeneral.add(new StringSetting.Builder()
            .name("msg")
            .defaultValue("splashgod.cc == pro")
            .build());


    public MessageBomb() {
        super(Main.RM, "message-bomb", "xdMorgan");
    }

    private boolean didSpam = false;
    private int ticks = 0;
    private int counter = 0;

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (didSpam) {
            ticks++;
            if (ticks >= delay.get()) {
                if (counter < 5) {
                    spamMessages();
                    counter++;
                } else {
                    counter = 0;
                    ticks = 0;
                }
            }
        } else {
            didSpam = true;
            spamMessages();
            counter++;
        }
    }

    private void spamMessages() {
        for (int i = 0; i < 5; i++) {
            String randomPlayer = getRandom();
            if (randomPlayer != null) {
                Objects.requireNonNull(mc.getNetworkHandler()).sendCommand("/w " + randomPlayer.concat(" "+text.get()));
            }
        }
    }

    private String getRandom() {
        List<PlayerListEntry> l = (List<PlayerListEntry>) Objects.requireNonNull(mc.getNetworkHandler()).getPlayerList();

        if (l.isEmpty()) {
            return null;
        }

        Random r = new Random();
        int i = r.nextInt(l.size());
        PlayerListEntry rp = l.get(i);

        return rp.getProfile().getName();
    }
}
