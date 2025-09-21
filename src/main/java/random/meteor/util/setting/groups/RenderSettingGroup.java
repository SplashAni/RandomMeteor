package random.meteor.util.setting.groups;

import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import random.meteor.util.render.GradientMode;
import random.meteor.util.render.RenderMode;
import random.meteor.util.render.RenderType;
import random.meteor.util.setting.DefaultSettingGroup;
import random.meteor.util.system.Mod;

public class RenderSettingGroup extends DefaultSettingGroup { // todo : make global color managmenets and so on
    public Setting<Integer> renderTime;
    public Setting<Boolean> shrink;
    public Setting<Boolean> slide;
    public Setting<Boolean> fade;
    public Setting<RenderType> renderType;
    public Setting<RenderMode> renderMode;
    public Setting<GradientMode> gradientMode;

    public Setting<SettingColor> sideColor;
    public Setting<SettingColor> lineColor;

    public RenderSettingGroup(Mod mod) {
        super(mod);
        setName("Render Settings");

        renderTime = getSettingGroup().add(new IntSetting.Builder()
            .name("render-time")
            .defaultValue(4)
            .min(1 )
            .sliderMax(10)
            .build()
        );

        sideColor = getSettingGroup().add(new ColorSetting.Builder()
            .name("side-color")
            .description("The side color of the rendering.")
            .defaultValue(new SettingColor(0, 0, 255, 75))
            .build()
        );

        lineColor = getSettingGroup().add(new ColorSetting.Builder()
            .name("line-color")
            .description("The line color of the rendering.")
            .defaultValue(new SettingColor(0, 0, 255, 255))
            .build()
        );

        renderType = getSettingGroup().add(new EnumSetting.Builder<RenderType>()
            .name("render-shape")
            .defaultValue(RenderType.Normal)
            .build()
        );

        renderMode = getSettingGroup().add(new EnumSetting.Builder<RenderMode>()
            .name("render-mode")
            .defaultValue(RenderMode.Both)
            .build()
        );
        gradientMode = getSettingGroup().add(new EnumSetting.Builder<GradientMode>()
            .name("gradient-mode")
            .defaultValue(GradientMode.Full)
            .build()
        );

        shrink = getSettingGroup().add(new BoolSetting.Builder()
            .name("shrink")
            .defaultValue(true)
            .visible(() -> renderTime.get() > 1)
            .build()
        );

        slide = getSettingGroup().add(new BoolSetting.Builder()
            .name("slide")
            .defaultValue(true)
            .visible(() -> renderTime.get() > 1)
            .build()
        );
        fade = getSettingGroup().add(new BoolSetting.Builder()
            .name("fade")
            .defaultValue(true)
            .visible(() -> renderTime.get() > 1)
            .build()
        );

    }
}
