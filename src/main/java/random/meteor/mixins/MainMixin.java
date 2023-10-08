package random.meteor.mixins;

import net.minecraft.client.main.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = Main.class, remap = false)
public class MainMixin {
    @Redirect(method = "<clinit>",
        at = @At(value = "INVOKE",
        target = "Ljava/lang/System;setProperty(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;")
    )
    private static String redirect(String key, String value) {
        return  System.setProperty("java.awt.headless", "false");
    }
}
