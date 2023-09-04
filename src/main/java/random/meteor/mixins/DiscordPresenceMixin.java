package random.meteor.mixins;

import meteordevelopment.discordipc.DiscordIPC;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.DiscordPresence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import random.meteor.systems.modules.RM.CustomRpc;

@Mixin(value = DiscordPresence.class, remap = false)
public abstract class DiscordPresenceMixin {

    @Redirect(method = "onActivate",
        at = @At(value =  "INVOKE",
            target = "Lmeteordevelopment/discordipc/DiscordIPC;start(JLjava/lang/Runnable;)Z"),
    remap = false)

    public boolean hi(long appId, Runnable onReady){
        return DiscordIPC.start( Modules.get().get(CustomRpc.class).getId(),null);
    }
}
