package random.meteor.systems.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.text.Text;
import random.meteor.Main;
import random.meteor.events.KickEvent;

import java.util.List;
import java.util.Random;

public class FakeKick extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> minTotem = sgGeneral.add(new IntSetting.Builder()
        .name("min-totems")
        .defaultValue(2)
        .min(1)
        .sliderRange(1, 15)
        .build()
    );
    private final Setting<Integer> minHealth = sgGeneral.add(new IntSetting.Builder()
        .name("min-health")
        .defaultValue(5)
        .min(1)
        .sliderRange(1, 36)
        .build()
    );
    private final Setting<Boolean> toggleKick = sgGeneral.add(new BoolSetting.Builder()
        .name("toggle-kick")
        .defaultValue(true)
        .build()
    );
    private final Setting<List<String>> msges = sgGeneral.add(new StringListSetting.Builder()
        .name("kick-messages")
        .defaultValue("Illegal client modifications",
            "Unlikely fast clicking",
            "Timed out"
        )
        .build()
    );

    public FakeKick() {
        super(Main.RM, "fake-kick", "trollings2b2t");
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        FindItemResult totem = InvUtils.find(Items.TOTEM_OF_UNDYING);
        if (totem.count() < minTotem.get() || PlayerUtils.getTotalHealth() <= minHealth.get()) {
            assert mc.player != null;

            Random random = new Random();
            String msg = msges.get().get(random.nextInt(msges.get().size()));

            mc.player.networkHandler.onDisconnect(new DisconnectS2CPacket(Text.literal(msg)));
            toggle();
        }
    }
    @EventHandler
    public void onKick(KickEvent event){
        if(!toggleKick.get()) return;
        toggle();
    }
}
