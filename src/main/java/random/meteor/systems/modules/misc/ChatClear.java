package random.meteor.systems.modules.misc;

import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import random.meteor.Main;

public class ChatClear extends Module {
    public enum When implements IVisible {
        Always,
        MessageReceive;

        @Override
        public boolean isVisible() {
            return false;
        }
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();


    public final Setting<When> when = sgGeneral.add(new EnumSetting.Builder<When>()
            .name("when-to-clear")
            .defaultValue(When.MessageReceive)
            .build()
    );
    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
            .name("delay")
            .description("delay")
            .defaultValue(1)
            .min(1)
            .sliderMax(5)
            .visible(When.Always)
            .build()
    );

    public ChatClear() {
        super(Main.MISC, "chat-clear", "may actually use this");
    }
    @EventHandler
    public void onTick(){
        if(when.get().equals(When.Always)) {
            for (int i = 1; i <= delay.get(); i++){
                mc.inGameHud.getChatHud().clear(false);
            }
        }
    }

    @EventHandler
    private void onMessageReceive(ReceiveMessageEvent event) {
        if(when.get().equals(When.MessageReceive)){
            mc.inGameHud.getChatHud().clear(false);
        }
    }
}