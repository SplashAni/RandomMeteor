package random.meteor.mixins;

import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import random.meteor.events.ChatRenderEvent;

import java.util.Objects;

@Mixin(ChatInputSuggestor.class)
public class ChatInputSuggestorMixin {

    @Shadow
    @Final
    TextFieldWidget textField;

    @Inject(method = "render", at = @At("HEAD"))
    public void render(DrawContext context, int mouseX, int mouseY, CallbackInfo ci) {
        int caretX = -1;
        int caretY = -1;

        if (textField != null) {
            caretX = getCaretOffsetX(textField);
            caretY = getCaretOffsetY(textField);
        }

        MeteorClient.EVENT_BUS.post(ChatRenderEvent.get(context, Objects.requireNonNull(textField).getText(), mouseX, mouseY, caretX, caretY));
    }

    @Unique
    public int getCaretOffsetX(TextFieldWidget textField) {
        int cursorIndex = textField.getCursor();
        return textField.getCharacterX(cursorIndex);
    }

    @Unique
    public int getCaretOffsetY(TextFieldWidget textField) {
        return textField.getY() + (textField.drawsBackground() ? (textField.getHeight() - 8) / 2 : 0);
    }
}
