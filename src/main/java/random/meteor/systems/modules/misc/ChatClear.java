package random.meteor.systems.modules.misc;

import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.text.Text;
import random.meteor.Main;

public class ChatClear extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Integer> clearAttempts = sgGeneral.add(new IntSetting.Builder()
            .name("clear-attempts")
            .description("amounts of times to clear chat")
            .defaultValue(35)
            .range(10,100)
            .sliderMax(100)
            .build()
    );
    public ChatClear() {
        super(Main.MISC, "chat-clear", "constantly clears chat");
    }

    @EventHandler
    private void onMessageReceive(ReceiveMessageEvent event) {
        for(int i = 1; i <= clearAttempts.get(); i ++)
        mc.player.sendMessage(Text.literal(" "));
    }
}