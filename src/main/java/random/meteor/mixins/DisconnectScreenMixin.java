package random.meteor.mixins;

import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import random.meteor.events.KickEvent;

@Mixin(DisconnectedScreen.class)
public class DisconnectScreenMixin {
    @Inject(method = "init"
        ,at = @At("HEAD")
    )

    public void onDisconnect(CallbackInfo ci){
        MeteorClient.EVENT_BUS.post(KickEvent.get());
    }
}
