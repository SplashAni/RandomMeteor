package random.meteor.mixins;

import meteordevelopment.discordipc.DiscordIPC;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.DiscordPresence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import random.meteor.systems.modules.CustomRpc;

@Mixin(value = DiscordPresence.class, remap = false)
public abstract class DiscordPresenceMixin {


}
