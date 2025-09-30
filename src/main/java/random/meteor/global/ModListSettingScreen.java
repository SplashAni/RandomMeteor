

package random.meteor.global;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.screens.settings.base.CollectionListSettingScreen;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import random.meteor.util.system.Mod;

import java.util.List;

public class ModListSettingScreen extends CollectionListSettingScreen<Mod> {
    public ModListSettingScreen(GuiTheme theme, ModListSetting<?> setting, List<Mod> mods) {
        super(
            theme,
            "Select Mods",
            setting,
            setting.get(),
            mods
        );
    }


    @Override
    protected WWidget getValueWidget(Mod value) {
        return theme.label(value.title);
    }

    @Override
    protected String[] getValueNames(Mod value) {
        String[] names = new String[value.aliases.length + 1];
        System.arraycopy(value.aliases, 0, names, 1, value.aliases.length);
        names[0] = value.title;
        return names;
    }
}
