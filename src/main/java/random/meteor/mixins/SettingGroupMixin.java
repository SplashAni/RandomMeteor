package random.meteor.mixins;

import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mixin(value = SettingGroup.class, remap = false)
public class SettingGroupMixin {

    // This could be toggled somewhere in your mod logic
    private boolean hideSettings = true;

    public void setHideSettings(boolean hide) {
        this.hideSettings = hide;
    }

    @Inject(method = "iterator", at = @At("HEAD"), cancellable = true)
    private void hideRealSettings(CallbackInfoReturnable<Iterator<Setting<?>>> cir) {
        if (hideSettings) {
            List<Setting<?>> fake = new ArrayList<>();
            fake.add((new ColorSetting.Builder()
                    .name("you are GAEEEE")
                    .description("The side color of the rendering.")
                    .defaultValue(new SettingColor(225, 0, 0, 75))
                    .build()
                )
            ); // simple placeholder setting
            cir.setReturnValue(fake.iterator());
        }
    }


}
