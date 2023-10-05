package random.meteor.mixins;

import com.mojang.blaze3d.systems.RenderSystem;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import random.meteor.systems.modules.RM.ItemRenderer;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {

    @Inject(method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V"
            ,at = @At("HEAD"))
    public void renderItem(float tickDelta, MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, ClientPlayerEntity player, int light, CallbackInfo ci) {
        ItemRenderer ir = Modules.get().get(ItemRenderer.class);
        OutlineVertexConsumerProvider outlineVertexConsumerProvider = new OutlineVertexConsumerProvider(vertexConsumers);

        if (ir.isActive()) {
            Color color = ir.handColor.get();

            if(ir.customGlint.get()){
                RenderSystem.setShaderGlintAlpha(ir.glintAlpha.get());
            }

            if(ir.itemColor.get()) {
                RenderSystem.setShaderColor(color.r / 255f, color.g / 255f, color.b / 255f, color.a / 255f);
            }

        }

    }
    @Inject(method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V"
            ,at = @At("TAIL")
    )
    public void kys(float tickDelta, MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, ClientPlayerEntity player, int light, CallbackInfo ci) {
        RenderSystem.setShaderColor(1,1,1,1);
    }
}
