package random.meteor.util.setting.groups;

import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import random.meteor.util.setting.DefaultSettingGroup;
import random.meteor.util.setting.EventType;
import random.meteor.util.setting.modes.HandMode;
import random.meteor.util.system.Mod;

/*
 *
 * swing
 * range
 * target
 *
 * */
public class SwingSettingGroup extends DefaultSettingGroup {
    public Setting<HandMode> handMode;
    public Setting<EventType> eventType;

    public SwingSettingGroup(Mod mod) {
        super(mod);

        setName("Swing Settings");

        handMode = getSettingGroup().add(new EnumSetting.Builder<HandMode>()
            .name("hand-mode")
            .description("How your hand should swing.")
            .defaultValue(HandMode.Interacted)
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
