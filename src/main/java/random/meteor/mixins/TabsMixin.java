package random.meteor.mixins;

import meteordevelopment.meteorclient.gui.tabs.Tab;
import meteordevelopment.meteorclient.gui.tabs.Tabs;
import meteordevelopment.meteorclient.gui.tabs.builtin.ProfilesTab;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import random.meteor.global.GlobalTab;

import java.util.List;

@Mixin(value = Tabs.class, remap = false)
public abstract class TabsMixin {
    @Shadow
    @Final
    private static List<Tab> tabs;

    @Inject(method = "add", at = @At(value = "HEAD"))
    private static void add(Tab tab, CallbackInfo ci) {
        if (tab instanceof ProfilesTab) tabs.add(new GlobalTab()); // LAST TAB AND IT WORKS (=
    }
}
