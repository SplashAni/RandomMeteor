package random.meteor.systems.modules;

import meteordevelopment.orbit.EventHandler;
import random.meteor.events.ChatRenderEvent;
import random.meteor.systems.Mod;

public class ChatEmojis extends Mod {
    public ChatEmojis() {
        super("chat-emojis");
    }

    @EventHandler
    public void onChatRender(ChatRenderEvent event) {
        event.context.fill(5, 5, 50, 50, -1);
    }
}
