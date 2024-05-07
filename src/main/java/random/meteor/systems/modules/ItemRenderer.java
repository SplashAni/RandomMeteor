package random.meteor.systems.modules;

import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import random.meteor.Main;
import random.meteor.systems.Mod;

public class ItemRenderer extends Mod {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<Boolean> itemColor = sgGeneral.add(new BoolSetting.Builder()
            .name("item-color")
            .defaultValue(false)
            .build()
    );

    public final Setting<SettingColor> handColor = sgGeneral.add(new ColorSetting.Builder()
            .name("hand-color")
            .description("The line color for unsafe blocks.")
            .defaultValue(new SettingColor(58,232,255,31))
            .visible(itemColor::get)
            .build()
    );

    public final Setting<Boolean> customGlint = sgGeneral.add(new BoolSetting.Builder()
            .name("custom-glint")
            .defaultValue(false)
            .build()
    );

    public final Setting<Integer> glintAlpha = sgGeneral.add(new IntSetting.Builder()
            .name("glint-alpha")
            .defaultValue(3)
            .min(0)
            .sliderMax(8)
            .visible(customGlint::get)
            .build()
    );

    public final Setting<Boolean> allGlint = sgGeneral.add(new BoolSetting.Builder()
            .name("all-glint")
            .description("makes every item gfet a glient")
            .defaultValue(true)
            .visible(customGlint::get)
            .build()
    );

    public ItemRenderer() {
        super("item-renderer","Custom hand renders");
    }
}
