package random.meteor.util.setting.groups;

import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import random.meteor.util.player.PlayerUtil;
import random.meteor.util.setting.GlobalSettingGroup;
import random.meteor.util.setting.modes.CenterMode;
import random.meteor.util.setting.modes.CenterTiming;
import random.meteor.util.system.Mod;

public class CenterSettingGroup extends GlobalSettingGroup {

    public Setting<CenterTiming> centerTiming;
    public Setting<CenterMode> centerMode;
    public Setting<Boolean> onOnGround;
    boolean centerUntil;

    public CenterSettingGroup(Mod mod) {
        super(mod, "Center");

        centerTiming = getSettingGroup().add(new EnumSetting.Builder<CenterTiming>()
            .name("center-event")
            .description("Weather to swing before or after interaction.")
            .defaultValue(CenterTiming.OnActivate)
            .build()
        );
        centerMode = getSettingGroup().add(new EnumSetting.Builder<CenterMode>()
            .name("center-mode")
            .description("Weather to swing before or after interaction.")
            .defaultValue(CenterMode.Smooth) // oh so smooth 🤤🤤
            .build()
        );
        onOnGround = getSettingGroup().add(new BoolSetting.Builder()
            .name("only-on-ground")
            .description("Centers on ground only")
            .defaultValue(true)
            .build()
        );
    }

    @Override
    public void onActivate() {
        centerUntil = centerTiming.get() == CenterTiming.OnActivate;

        super.onActivate();
    }

    @Override
    public void onPreTick() {
        boolean shouldCenter = centerTiming.get() == CenterTiming.Always
            || centerUntil;

        if (shouldCenter && PlayerUtil.centerEvent(this)) {
            mod.debug(centerUntil ? "Finished centering on on activation" : "Centered player", mod.debugActions.get());
            if (centerUntil) centerUntil = false;
        }
        super.onPreTick();
    }
}
