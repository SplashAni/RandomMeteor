package random.meteor.mixyy;

import net.minecraft.client.main.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Main.class)
public class AwtFixer {
    @Inject(method = "main", at = @At("HEAD"), remap = false)
    private static void doBootstrap(CallbackInfo ci) {
        System.setProperty("java.awt.headless", "false"); //thanks pegasus : https://github.com/lapoliciarobomiquesofrances/meteor-community-addon/blob/main/src/main/resources/community-addon.mixins.json
    }
}