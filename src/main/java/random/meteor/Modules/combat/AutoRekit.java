package random.meteor.Modules.combat;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import random.meteor.Main;

public class AutoRekit extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<String> kit = sgGeneral.add(new StringSetting.Builder()
            .name("kit-name")
            .description("kits name")
            .defaultValue("splash")
            .build()
    );

    public AutoRekit() {

        super(Main.MISC,"Auto-kit","rekits on death");
    }

    @EventHandler
    private void onPacketReceive(PacketEvent.Receive event)  {
        if (event.packet instanceof DeathMessageS2CPacket packet) {
            Entity entity = mc.world.getEntityById(packet.getEntityId());
            if (entity == mc.player) {
                ChatUtils.sendPlayerMsg("/kit "+kit.get());
            }
        }
    }
}
