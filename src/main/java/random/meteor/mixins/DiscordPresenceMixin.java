package random.meteor.mixins;

import meteordevelopment.meteorclient.systems.modules.misc.DiscordPresence;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = DiscordPresence.class, remap = false)
public abstract class DiscordPresenceMixin {


}
