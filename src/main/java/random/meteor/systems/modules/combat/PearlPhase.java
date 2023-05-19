package random.meteor.systems.modules.combat;

import meteordevelopment.meteorclient.settings.*;
import random.meteor.Main;
import random.meteor.systems.modules.utils.CombatUtils;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;

public class PearlPhase extends Module {
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
    private final Setting<Integer> attempts = sgGeneral.add(new IntSetting.Builder()
            .name("attempts")
            .description("amount of times to throw the pearls")
            .defaultValue(1)
            .range(1, 5)
            .sliderMax(5)
            .build()
    );

    public PearlPhase() {
        super(Main.COMBAT, "pearl-phase", "Attempts to phase with pearls");
    }

    @Override
    public void onActivate() {
        if (center.get()) {
            PlayerUtils.centerPlayer();

            for(int i = 1; i <= attempts.get();i++ ) {
                switch (pearlPos.get()) {
                    case Default -> {
                        CombatUtils.throwPearl(72);
                    }
                    case Custom -> {
                        CombatUtils.throwPearl(customPitch.get());
                    }
                    case Top -> {
                        CombatUtils.throwPearl(-90);
                    }
                    case Bottom -> {
                        CombatUtils.throwPearl(90);
                    }
                }
            }
            if (notify.get()) {
                info("Attempted to phase");
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
