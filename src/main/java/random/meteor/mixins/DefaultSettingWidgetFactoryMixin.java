package random.meteor.mixins;

import meteordevelopment.meteorclient.gui.DefaultSettingsWidgetFactory;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.utils.SettingsWidgetFactory;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.settings.Setting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import random.meteor.global.ModListSetting;
import random.meteor.global.ModListSettingScreen;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Mixin(DefaultSettingsWidgetFactory.class)
public abstract class DefaultSettingWidgetFactoryMixin extends SettingsWidgetFactory {
    public DefaultSettingWidgetFactoryMixin(GuiTheme theme) {
        super(theme);
    }

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void init(GuiTheme theme, CallbackInfo ci) {
        factories.put(ModListSetting.class, (table, setting) -> modlistW(table, (ModListSetting) setting));
    }

    @Shadow
    protected abstract void selectW(WContainer c, Setting<?> setting, Runnable action);

    @Unique
    private void modlistW(WTable table, ModListSetting setting) {
        selectW(table, setting, () -> mc.setScreen(new ModListSettingScreen(theme, setting)));
    }
}
