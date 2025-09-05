package random.meteor.util.setting.groups;

import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import random.meteor.util.setting.DefaultSettingGroup;
import random.meteor.util.system.Mod;

public class PlaceSettingGroup extends DefaultSettingGroup {


    Setting<Boolean> outsideBorder;
    Setting<WorldHeight> worldHeight;
    Setting<Boolean> airplace;
    Setting<Boolean> createBase;
    Setting<Double> supportRange;

    public PlaceSettingGroup(Mod mod) {
        super(mod);

        airplace = getSettingGroup().add(new BoolSetting.Builder()
            .name("airplace")
            .defaultValue(true)
            .build()
        );
        createBase = getSettingGroup().add(new BoolSetting.Builder()
            .name("create-support")
            .description("Places blocks near other blocks to support the goal block")
            .defaultValue(false)
            .build()
        );
        supportRange = getSettingGroup().add(new DoubleSetting.Builder()
            .name("support-range")
            .description("Range in which to interact.")
            .defaultValue(4.5)
            .min(0)
            .sliderMax(6)
            .build()
        );

        outsideBorder = getSettingGroup().add(new BoolSetting.Builder()
            .name("outside-world-border")
            .defaultValue(false)
            .build()
        );
        worldHeight = getSettingGroup().add(new EnumSetting.Builder<WorldHeight>()
            .name("world-height")
            .description("1.12.2 or modern version placement height")
            .defaultValue(WorldHeight.New)
            .build()
        );
    }

    public enum WorldHeight {
        Old,
        New,
    }
}
