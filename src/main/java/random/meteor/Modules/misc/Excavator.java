package random.meteor.Modules.misc;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import random.meteor.Main;

public class Excavator extends Module {
    public Excavator() {
        super(Main.MISC,"excavator","here u go v2");
    }
    @Override
    public WWidget getWidget(GuiTheme theme) {
        WTable table = theme.table();
        excavatorGui(theme, table);

        return table;
    }
    private void excavatorGui(GuiTheme theme, WTable table) {

        WButton spawn = table.add(theme.button("Set pos 1")).expandCellX().right().widget();
        spawn.action = () -> {
            ChatUtils.sendPlayerMsg("#sel 1");
            table.clear();
            excavatorGui(theme, table);
        };

        WButton clear = table.add(theme.button("Set pos 2")).right().widget();
        clear.action = () -> {
            ChatUtils.sendPlayerMsg("#sel 2");
            table.clear();
            excavatorGui(theme, table);
        };
        WButton start = table.add(theme.button("Start")).right().widget();
        start.action = () -> {
            ChatUtils.sendPlayerMsg("#sel ca");
            table.clear();
            excavatorGui(theme, table);
        };
        WButton stop = table.add(theme.button("Stop")).right().widget();
        stop.action = () -> {
            ChatUtils.sendPlayerMsg("#stop");
            table.clear();
            excavatorGui(theme, table);
        };
        WButton clearRenders = table.add(theme.button("Clear-renders")).right().widget();
        clearRenders.action = () -> {
            ChatUtils.sendPlayerMsg("#sel clear");
            table.clear();
            excavatorGui(theme, table);
        };
    }
}
