package random.meteor.mixins;

import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import random.meteor.systems.modules.RM.ItemRenderer;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Mixin(Item.class)
public class ItemMixin {

    @Redirect(method = "hasGlint",
            at = @At(value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;hasEnchantments()Z"
    ))
    private boolean kys(ItemStack instance) {

        ItemRenderer ir = Modules.get().get(ItemRenderer.class);

        if(!ir.isActive() || !ir.allGlint.get()) return instance.hasEnchantments();

        assert mc.player != null;

        return mc.player.getMainHandStack() == instance || mc.player.getOffHandStack() == instance;
    }

}
