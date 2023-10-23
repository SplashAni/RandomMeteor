package random.meteor.mixins;

import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import random.meteor.systems.modules.Multitask;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Redirect(method = "handleBlockBreaking",at = @At(value = "INVOKE",target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"))
    public boolean isUsing(ClientPlayerEntity instance) {
        if (Modules.get().get(Multitask.class).isActive()) return false;
        return instance.isUsingItem();
    }

    @Redirect(method = "doItemUse",at = @At(value = "INVOKE",target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;isBreakingBlock()Z"))
    public boolean isBreaking(ClientPlayerInteractionManager instance) {
        if (Modules.get().get(Multitask.class).isActive()) return false;
        return instance.isBreakingBlock();
    }

}
