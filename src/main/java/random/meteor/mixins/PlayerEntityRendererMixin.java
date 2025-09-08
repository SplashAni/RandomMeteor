package random.meteor.mixins;

import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import random.meteor.events.PlayerRenderStateEvent;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {
    @Inject(method = "updateRenderState(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;F)V", at = @At("RETURN"))
    private void updateRenderState$rotations(AbstractClientPlayerEntity abstractClientPlayerEntity, PlayerEntityRenderState playerEntityRenderState, float f, CallbackInfo ci) {
        if (abstractClientPlayerEntity == mc.player) {
            MeteorClient.EVENT_BUS.post(new PlayerRenderStateEvent(playerEntityRenderState));
        }
    }
}
