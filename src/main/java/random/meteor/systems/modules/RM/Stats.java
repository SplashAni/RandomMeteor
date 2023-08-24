package random.meteor.systems.modules.RM;

import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import random.meteor.Main;
import random.meteor.systems.modules.utils.StatsScreen;

public class Stats extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> width = sgGeneral.add(new IntSetting.Builder()
        .name("width")
        .description("widht")
        .defaultValue(-50)
        .min(-100)
        .sliderMax(100)
        .build()
    );
    private final Setting<Integer> height = sgGeneral.add(new IntSetting.Builder()
        .name("height")
        .defaultValue(110)
        .min(1)
        .sliderMax(1000)
        .build()
    );
    private final Setting<Integer> h = sgGeneral.add(new IntSetting.Builder()
        .name("h")
        .defaultValue(5)
        .min(1)
        .sliderMax(1000)
        .build()
    );
    private final Setting<Integer> q = sgGeneral.add(new IntSetting.Builder()
        .name("q")
        .defaultValue(5)
        .min(1)
        .sliderMax(1000)
        .build()
    );
    private final Setting<Integer> u = sgGeneral.add(new IntSetting.Builder()
        .name("u")
        .defaultValue(5)
        .min(1)
        .sliderMax(1000)
        .build()
    );
    private final Setting<Integer> d = sgGeneral.add(new IntSetting.Builder()
        .name("d")
        .defaultValue(5)
        .min(1)
        .sliderMax(1000)
        .build()
    );
    public Stats() {
        super(Main.RM,"stats","insane");
        runInMainMenu = true; /*bet*/
    }

    public int w(){
        return width.get();
    }
    public int h(){
        return height.get();
    }
    public int q(){
        return h.get();
    }
    public int up(){
        return u.get();
    }
    public int down(){
        return d.get();
    }
    @Override
    public void onActivate() {
        mc.setScreen(new StatsScreen());
        toggle();
        super.onActivate();
    }
}
