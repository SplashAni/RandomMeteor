package random.meteor.Modules.combat;

import baritone.api.BaritoneAPI;
import baritone.api.pathing.goals.GoalBlock;
import meteordevelopment.meteorclient.events.entity.EntityAddedEvent;
import meteordevelopment.meteorclient.events.entity.EntityRemovedEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.combat.CrystalAura;
import meteordevelopment.meteorclient.systems.modules.combat.Surround;
import meteordevelopment.meteorclient.systems.modules.player.AutoGap;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.player.PlayerEntity;
import random.meteor.Main;
import random.meteor.Modules.misc.AutoPussy;

import static random.meteor.Utils.CombatUtils.getPlayer;

public class AutoPvp extends Module {
    private final SettingGroup sgGeneral = settings.createGroup("General");
    private final SettingGroup sgToggle = settings.createGroup("Toggle");


    private final Setting<Boolean> checkHealth = sgGeneral.add(new BoolSetting.Builder()
            .name("check-opponent-health")
            .description("Decides to pvp or not")
            .defaultValue(false)
            .build()
    );
    private final Setting<Boolean> autoRun = sgGeneral.add(new BoolSetting.Builder()
            .name("auto-run")
            .description("Toggles auto pussy and runs")
            .defaultValue(false)
            .build()
    );
    private final Setting<Boolean> subtractTargetPos = sgGeneral.add(new BoolSetting.Builder()
            .name("subtract-target-pos")
            .description("amount of blocks to stay away from the target")
            .defaultValue(false)
            .build()
    );
    private final Setting<Integer> subtractX = sgGeneral.add(new IntSetting.Builder()
            .name("subtract-X")
            .description("subtracts a value from the X of the targest pos")
            .defaultValue(0)
            .range(0, 10)
            .sliderMax(10)
            .visible(subtractTargetPos::get)
            .build()
    );

    private final Setting<Integer> subtractY = sgGeneral.add(new IntSetting.Builder()
            .name("subtract-Y")
            .description("subtracts a value from the Y of the targest pos")
            .defaultValue(0)
            .range(0, 10)
            .sliderMax(10)
            .visible(subtractTargetPos::get)
            .build()

    );    private final Setting<Integer> getSubtractZ = sgGeneral.add(new IntSetting.Builder()
            .name("subtract-Z")
            .description("subtracts a value from the Z of the targest pos")
            .defaultValue(0)
            .range(0, 10)
            .sliderMax(10)
            .visible(subtractTargetPos::get)
            .build()
    );
    private final Setting<Boolean> autoSurround = sgToggle.add(new BoolSetting.Builder()
            .name("toggle-surround")
            .description("toggles surround when reaching the targets pos")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> autoCrystal = sgToggle.add(new BoolSetting.Builder()
            .name("auto-crystal")
            .description("toggles auto crystal when reaching the targets pos")
            .defaultValue(false)
            .build()
    );
    private final Setting<Boolean> autoGap = sgToggle.add(new BoolSetting.Builder()
            .name("auto-gap")
            .description("toggles auto gap when reaching the targets pos")
            .defaultValue(false)
            .build()
    );

    public AutoPvp() {
        super(Main.COMBAT, "auto-pvp", "Automatically pvps, so skillful");

    }

    @EventHandler
    private void onEntityAdded(EntityAddedEvent event) {
        PlayerEntity target = (PlayerEntity) event.entity;

        double x =target.getX(); double y = target.getY(); double z = target.getZ();

        AutoPussy run = Modules.get().get(AutoPussy.class);

        if (target.getHealth() >= getPlayer().getHealth() && checkHealth.get()) {
            if(autoRun.get()) {
                run.toggle();
            }

        } else if (target.getHealth() <= getPlayer().getHealth()) {
            BaritoneAPI.getProvider().getPrimaryBaritone();
            int newX = (int) x - subtractX.get(); int newY = (int) y - subtractY.get(); int newZ = (int) z - getSubtractZ.get();
            GoalBlock targetPos = new GoalBlock( newX,newY,newZ);
            BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoal(targetPos);

            if(getPlayer().getX() == newX && getPlayer().getZ() == getPlayer().getZ()){
                toggleStuff();
            }
        }
    }
    @EventHandler
    private void onEntityRemoved(EntityRemovedEvent event) {
        if(autoRun.get()){
            ChatUtils.sendPlayerMsg("#stop");
        }
    }

        private void toggleStuff(){
        CrystalAura ca = Modules.get().get(CrystalAura.class);
        AutoGap gap = Modules.get().get(AutoGap.class);
        Surround sur = Modules.get().get(Surround.class);

        if(!ca.isActive() && autoCrystal.get()) ca.toggle();
        if(!gap.isActive() && autoGap.get()) gap.toggle();;
        if(!sur.isActive() && autoSurround.get()) sur.toggle();
    }
}
