package random.meteor.mixins;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import random.meteor.utils.ChatFieldDuck;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin implements ChatFieldDuck {
    @Shadow
    protected TextFieldWidget chatField;

    @Override
    public void randomMeteor$setText(String text) {
        chatField.setText(text);
    }
}
