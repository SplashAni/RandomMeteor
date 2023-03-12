package random.meteor.systems.Modules.combat;

import random.meteor.Main;
import random.meteor.systems.Modules.Utils.CombatUtils;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;

public class PearlPhase extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> center = sgGeneral.add(new BoolSetting.Builder()
            .name("Center")
            .description("Centers player, recommend for bedrock pvp.)")
            .defaultValue(true)
            .build()
    );
    public final Setting<Mode> pearlPos = sgGeneral.add(new EnumSetting.Builder<Mode>()
            .name("Pearl-pos")
            .defaultValue(Mode.DEFAULT)
            .build()
    );
    public final Setting<Boolean> notify = sgGeneral.add(new BoolSetting.Builder()
            .name("Notify")
            .description("Sends chat message if pearl is thrown")
            .defaultValue(true)
            .build()
    );

    public PearlPhase() {
        super(Main.COMBAT, "pearl-phase", "Attempts to phase with pearls");
    }

    @Override
    public void onActivate() {
        if (center.get()) {
            PlayerUtils.centerPlayer();

            if (pearlPos.get() == Mode.DEFAULT) {
                CombatUtils.throwPearl(72);
            } else if (pearlPos.get() == Mode.BOTTOM) {
                CombatUtils.throwPearl(90);
            } else if (pearlPos.get() == Mode.TOP) {
                CombatUtils.throwPearl(-90);
            }

            if (notify.get()) {
                info("Attempted to phase");
            }

            this.toggle();
        }
        super.onActivate();
    }

    public enum Mode {
        DEFAULT,
        TOP,
        BOTTOM,
    }
}
