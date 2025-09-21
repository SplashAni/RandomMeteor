package random.meteor.global;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.tabs.Tab;
import meteordevelopment.meteorclient.gui.tabs.TabScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.Direction;

public class GlobalTab extends Tab {
    public GlobalTab() {
        super("Global");
    }

    public TabScreen createScreen(GuiTheme theme) {
        return new GlobalScreen(theme, this);
    }

    @Override
    public boolean isScreen(Screen screen) {
        return screen instanceof GlobalScreen;
    }

}
