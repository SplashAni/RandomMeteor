package random.meteor.systems.Modules.misc;

import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.text.Text;
import random.meteor.Main;

import java.util.List;
import java.util.Random;

public class AutoReply extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<List<String>> respond = sgGeneral.add(new  StringListSetting.Builder()
            .name("reply-message")
            .description("")
            .defaultValue("hi")
            .build()
    );

    public AutoReply() {
        super(Main.MISC,"auto-reply","funy");
    }


    @EventHandler
    private void onMessageReceive(ReceiveMessageEvent event) {
        if(respond.get().isEmpty()) return;
        if(mc.world == null) return;
        String username = mc.player.getName().toString();
        if(event.getMessage().contains(Text.of(username))){
            ChatUtils.sendPlayerMsg(respond.get().get(new Random().nextInt(respond.get().size())));
        }
    }
}
