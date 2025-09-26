/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package random.meteor.global;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.screens.settings.base.CollectionListSettingScreen;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.settings.Setting;
import random.meteor.Main;
import random.meteor.manager.ModuleManager;
import random.meteor.util.system.Mod;

import java.util.List;

public class ModListSettingScreen extends CollectionListSettingScreen<Mod> {
    public ModListSettingScreen(GuiTheme theme, Setting<List<Mod>> setting) {
        super(theme, "Select Mods", setting, setting.get(), Main.MANAGERS.getManager(ModuleManager.class).getModules());
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
