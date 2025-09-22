package random.meteor.util.setting.groups;

import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import random.meteor.util.setting.DefaultSettingGroup;
import random.meteor.util.setting.EventType;
import random.meteor.util.setting.modes.SwingMode;
import random.meteor.util.system.Mod;

/*
 *
 * swing
 * range
 * target
 *
 * */
public class SwingSettingGroup extends DefaultSettingGroup {
    public Setting<SwingMode> handMode;
    public Setting<EventType> eventType;

    public SwingSettingGroup(Mod mod) {
        super(mod,"Swing");

        handMode = getSettingGroup().add(new EnumSetting.Builder<SwingMode>()
            .name("hand-mode")
            .description("How your hand should swing.")
            .defaultValue(SwingMode.Interacted)
            .build()
        );

        eventType = getSettingGroup().add(new EnumSetting.Builder<EventType>()
            .name("swing-event")
            .description("Weather to swing before or after interaction.")
            .defaultValue(EventType.POST)
            .build()
        );
    }





}
