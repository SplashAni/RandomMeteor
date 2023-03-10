package random.meteor.Modules.combat;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import random.meteor.Main;

import static meteordevelopment.meteorclient.MeteorClient.mc;
import static random.meteor.Utils.CombatUtils.isRange;

public class AutoNuke extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    FindItemResult tnt = InvUtils.findInHotbar(Items.TNT);
    FindItemResult items = InvUtils.findInHotbar(Items.TNT);
    FindItemResult flint = InvUtils.findInHotbar(Items.FLINT_AND_STEEL);

    private final Setting<Integer> range = sgGeneral.add(new IntSetting.Builder()
            .name("range")
            .description("Health to be at before running..")
            .defaultValue(5)
            .range(1, 12)
            .sliderMax(12)
            .build()
    );
    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
            .name("rotate")
            .description("Rotates player head.")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> swing = sgGeneral.add(new BoolSetting.Builder()
            .name("swing")
            .description("Swings you hand when the block is placed.")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> checkEntities = sgGeneral.add(new BoolSetting.Builder()
            .name("check-entities")
            .description("wheather to check entities")
            .defaultValue(true)
            .build()
    );
    public AutoNuke() {
        super(Main.COMBAT, "auto-nuke", "places tnt ontop of players heads LOl");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (!tnt.found() && !flint.found()) {
            error("Required items not found toggling...");
            toggle();
        } else {

        }
    }

    private void begin() {
        double x = mc.player.getPos().x;  double y = mc.player.getPos().y + mc.player.getEyeHeight(mc.player.getPose()); double z = mc.player.getPos().z;
        for (PlayerEntity target : mc.world.getPlayers()) {

            double headX = target.getPos().x; double headY = target.getPos().y + target.getEyeHeight(target.getPose()); double headZ = target.getPos().z;

            BlockPos targetsHeadPos = new BlockPos(target.getPos().x, target.getPos().y + target.getEyeHeight(target.getPose()), target.getPos().z);

            if (isRange(x,y,z,headX, headY, headZ, range.get())) {

                BlockUtils.place(targetsHeadPos,tnt, rotate.get(), 100, swing.get(), checkEntities.get());
                if(flint.found()){
                    blowTnt(targetsHeadPos);
                }
                else {
                    error("No flint found toggling...");
                    toggle();
                }
            } else {
                error("Target is out of range toggling...");
                toggle();
            }
        }
    }
    private void blowTnt(BlockPos pos) {
        InvUtils.swap(flint.slot(), true);
        mc.interactionManager.interactBlock(mc.player, Hand.OFF_HAND, new BlockHitResult(new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), Direction.UP, pos, true));
        InvUtils.swapBack();
    }
}