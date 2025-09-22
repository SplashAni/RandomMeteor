package random.meteor.util.setting;

/**
 * Marks a SettingGroup as “managed globally” so its settings
 * can be hidden in the GUI dynamically.
 */
public interface IGlobalManaged {
    boolean randomMeteor$shouldHideSettings();
    void randomMeteor$setHideSettings(boolean hide);
}
