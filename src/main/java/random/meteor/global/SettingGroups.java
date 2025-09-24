package random.meteor.global;

import random.meteor.util.setting.groups.*;

import java.util.List;

public interface SettingGroups {

    List<?> getGlobalSettingGroupList();

    @SuppressWarnings("unchecked")
    default <T> T get(Class<T> groupClass) {
        return (T) getGlobalSettingGroupList().stream()
            .filter(g -> g.getClass().equals(groupClass))
            .findFirst()
            .orElse(null);
    }

    default CenterSettingGroup getCenterSettings() {
        return get(CenterSettingGroup.class);
    }

    default DelaySettingGroup getDelaySettings() {
        return get(DelaySettingGroup.class);
    }

    default PlaceSettingGroup getPlaceSettings() {
        return get(PlaceSettingGroup.class);
    }

    default RangeSettingGroup getRangeSettings() {
        return get(RangeSettingGroup.class);
    }

    default RenderSettingGroup getRenderSettings() {
        return get(RenderSettingGroup.class);
    }

    default SwapSettingGroup getSwapSettings() {
        return get(SwapSettingGroup.class);
    }

    default SwingSettingGroup getSwingSettings() {
        return get(SwingSettingGroup.class);
    }
}
