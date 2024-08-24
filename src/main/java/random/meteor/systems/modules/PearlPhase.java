package random.meteor.systems.modules;

import meteordevelopment.meteorclient.settings.*;
import random.meteor.systems.Mod;
import random.meteor.utils.Utils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;

public class PearlPhase extends Mod {
        private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> center = sgGeneral.add(new BoolSetting.Builder()
            .name("center")
            .description("Centers player, recommend for bedrock pvp.)")
            .defaultValue(true)
            .build()
    );
    public final Setting<Mode> pearlPos = sgGeneral.add(new EnumSetting.Builder<Mode>()
            .name("pearl-pos")
            .defaultValue(Mode.Default)
            .build()
    );
    private final Setting<Integer> customPitch = sgGeneral.add(new IntSetting.Builder()
            .name("custom-pitch")
            .description("")
            .defaultValue(45)
            .range(1,90)
            .sliderMax(90)
            .visible(Mode.Custom)
            .build()
    );

    public final Setting<Boolean> notify = sgGeneral.add(new BoolSetting.Builder()
            .name("notify")
            .description("Sends chat message if pearl is thrown")
            .defaultValue(true)
            .build()
    );

    public PearlPhase() {
        super( "pearl-phase", "Attempts to phase with pearls");
    }

    @Override
    public void onActivate() {
        if (center.get()) {
            PlayerUtils.centerPlayer();

                switch (pearlPos.get()) {
                    case Default -> Utils.throwPearl(72);
                    case Custom -> Utils.throwPearl(customPitch.get());
                    case Top -> Utils.throwPearl(-90);
                    case Bottom -> Utils.throwPearl(90);
                }
            if (notify.get()) {
                info("Threw pearl, toggling...");
            }

            this.toggle();
        }
        super.onActivate();
    }

    public enum Mode implements IVisible {
        Custom,
        Default,
        Top,
        Bottom,
        ;

        @Override
        public boolean isVisible() {
            return false;
        }
    }
}
