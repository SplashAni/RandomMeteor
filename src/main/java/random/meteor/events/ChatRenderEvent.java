package random.meteor.events;

import net.minecraft.client.gui.DrawContext;

public class ChatRenderEvent {
    public static ChatRenderEvent INSTANCE = new ChatRenderEvent();

    public DrawContext context;
    public int mouseX, mouseY, cartX, caretY;
    public String currentText;

    public static ChatRenderEvent get(DrawContext context, String currentText, int mouseX, int mouseY, int caretX, int caretY) {
        ChatRenderEvent event = INSTANCE;
        event.context = context;
        event.currentText = currentText;
        event.mouseX = mouseX;
        event.mouseY = mouseY;
        event.cartX = caretX;
        event.caretY = caretY;
        return event;
    }

}
