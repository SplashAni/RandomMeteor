package random.meteor.global;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.tabs.Tab;
import meteordevelopment.meteorclient.gui.tabs.WindowTabScreen;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.Settings;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.navigation.NavigationDirection;
import org.jetbrains.annotations.Nullable;
import random.meteor.Main;
import random.meteor.util.setting.GlobalSettingGroup;

import java.util.Optional;

public class GlobalScreen extends WindowTabScreen {
    public Settings settings = new Settings();
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public GlobalScreen(GuiTheme theme, Tab tab) {
        super(theme, tab);
        sgGeneral.add(new EnumSetting.Builder<ShapeMode>()
            .name("testing")
            .description("smash")
            .defaultValue(ShapeMode.Both)
            .build()
        );
    }

    @Override
    public void initWidgets() {

        add(theme.label("Below is the controls for every module that uses global settings.")).expandCellX();

        add(theme.settings(settings)).expandX();
        add(theme.horizontalSeparator()).expandX();

    }


    @Override
    public Optional<Element> hoveredElement(double mouseX, double mouseY) {
        return super.hoveredElement(mouseX, mouseY);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean isFocused() {
        return super.isFocused();
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
    }

    public Settings getSettings() {
        return settings;
    }

    @Override
    public @Nullable GuiNavigationPath getFocusedPath() {
        return super.getFocusedPath();
    }

    @Override
    public ScreenRect getBorder(NavigationDirection direction) {
        return super.getBorder(direction);
    }

    @Override
    public @Nullable GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
        return super.getNavigationPath(navigation);
    }

    @Override
    public int getNavigationOrder() {
        return super.getNavigationOrder();
    }
}
