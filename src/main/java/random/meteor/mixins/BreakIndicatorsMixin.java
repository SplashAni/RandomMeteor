package random.meteor.mixins;

import meteordevelopment.meteorclient.mixin.ClientPlayerInteractionManagerAccessor;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.BreakIndicators;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import random.meteor.systems.modules.AutoMine;
import random.meteor.utils.enums.RenderMode;

@Mixin(value = BreakIndicators.class, remap = false)
public abstract class BreakIndicatorsMixin {


    @Redirect(
            method = "renderNormal",
            at = @At(
                    value = "INVOKE",
                    target = "Lmeteordevelopment/meteorclient/mixin/ClientPlayerInteractionManagerAccessor;getCurrentBreakingBlockPos()Lnet/minecraft/util/math/BlockPos;"
            )
    )
    private BlockPos kys(ClientPlayerInteractionManagerAccessor instance) {
        AutoMine autoMine = Modules.get().get(AutoMine.class);

        if (!autoMine.isActive() || autoMine.pos == null || autoMine.progress > 1 || autoMine.renderMode.get() != RenderMode.BreakIndicators)
            return instance.getCurrentBreakingBlockPos();
        return autoMine.pos;
    }

    @Redirect(method = "renderNormal",
            at = @At(
                    value = "INVOKE",
                    target = "Lmeteordevelopment/meteorclient/mixin/ClientPlayerInteractionManagerAccessor;getBreakingProgress()F"
            )
    )
    public float ok(ClientPlayerInteractionManagerAccessor instance) {
        AutoMine autoMine = Modules.get().get(AutoMine.class);

        if (!autoMine.isActive() || autoMine.pos == null || autoMine.progress > 1 || autoMine.renderMode.get() != RenderMode.BreakIndicators)
            return instance.getBreakingProgress();

        return autoMine.progress;
    }
}

