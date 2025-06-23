package random.meteor.events;

import net.minecraft.client.gui.DrawContext;

public class ChatRenderEvent {
    public static ChatRenderEvent INSTANCE = new ChatRenderEvent();

    public DrawContext context;
    public int mouseX, mouseY;

    public static ChatRenderEvent get(DrawContext context, int mouseX, int mouseY) {
        ChatRenderEvent event = INSTANCE;
        event.context = context;
        event.mouseX = mouseX;
        event.mouseY = mouseY;
        return event;
    }
}
