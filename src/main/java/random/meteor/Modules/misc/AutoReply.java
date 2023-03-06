package random.meteor.Modules.misc;

import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.text.Text;
import random.meteor.Main;

public class AutoReply extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<String> respond = sgGeneral.add(new StringSetting.Builder() // todo: turn this into an array :pray:
            .name("reply-message")
            .description("")
            .defaultValue("hi")
            .build()
    );

    public AutoReply() {
        super(Main.MISC,"auto-reply","funy");
    }


    @EventHandler
    private void onMessageRecieve(ReceiveMessageEvent event) {
        if(respond.get().isBlank() && respond.get().isBlank()) return;
        if(mc.world == null && mc.world == null) return;
        String username = mc.player.getName().toString();
        if(event.getMessage().contains(Text.of(username))){
            ChatUtils.sendPlayerMsg(respond.toString());
        }
    }
}
