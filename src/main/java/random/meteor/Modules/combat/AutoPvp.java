package random.meteor.Modules.combat;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.combat.CrystalAura;
import meteordevelopment.meteorclient.systems.modules.combat.Surround;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.player.PlayerEntity;
import random.meteor.Main;

import static random.meteor.Utils.CombatUtils.getTargetPlayerWithinRange;

public class AutoPvp extends Module {
    private final SettingGroup sgGeneral = settings.createGroup("General");
    private final Setting<Boolean> toggleModules = sgGeneral.add(new BoolSetting.Builder()
            .name("pvp-toggle")
            .description("Toggles pvp modules.")
            .defaultValue(true)
            .build()
    );
    private final Setting<Integer> toggleRange = sgGeneral.add(new IntSetting.Builder()
            .name("toggle-range")
            .description("The maximum height Anchor will work at.")
            .defaultValue(10)
            .range(0, 255)
            .sliderMax(20)
            .build()
    );

    public AutoPvp() {
        super(Main.COMBAT, "auto-pvp", "Automatically pvps, so skillful");
    }

    @Override
    public void onActivate() {
        ChatUtils.sendPlayerMsg("#follow players");
        super.onActivate();
    }

    @Override
    public void onDeactivate() {
        ChatUtils.sendPlayerMsg("#stop");
        super.onDeactivate();
    }

    @EventHandler
    private void onTick() {
        rangeToggle();
        return;
    }

    @Override
    public WWidget getWidget(GuiTheme theme) {
        WTable table = theme.table();
        buttonMenu(theme, table);

        return table;
    }

    private void buttonMenu(GuiTheme theme, WTable table) {

        WButton pause = table.add(theme.button("Pause")).expandCellX().right().widget();
        pause.action = () -> {
            ChatUtils.sendPlayerMsg("#pause");
        };

        WButton unPause = table.add(theme.button("Unpause")).right().widget();
        unPause.action = () -> {
            ChatUtils.sendPlayerMsg("#continue");
        };
    }


    public void rangeToggle() {
        PlayerEntity player = mc.player;
        PlayerEntity target = getTargetPlayerWithinRange(Integer.parseInt(toggleRange.toString()));
        if (target != null && toggleModules.get() && Modules.get() != null) {
            double distance = player.getPos().distanceTo(target.getPos());
            if (distance <= 3.0) {
                CrystalAura crystalAura = Modules.get().get(CrystalAura.class);
                Surround surround = Modules.get().get(Surround.class);
                if (crystalAura != null && !crystalAura.isActive()) {
                    crystalAura.toggle();
                }
                if (surround != null && !surround.isActive()) {
                    surround.toggle();
                }
            }
        }
    }
}

