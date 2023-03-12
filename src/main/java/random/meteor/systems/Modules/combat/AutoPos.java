package random.meteor.systems.Modules.combat;

import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import random.meteor.Main;

public class AutoPos extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public enum positions{
        Standing,
        Crouching,
        Swimming,
        Falling
    }


    public final Setting<positions> pos = sgGeneral.add(new EnumSetting.Builder<positions>()
            .name("positions")
            .defaultValue(positions.Standing)
            .build()
    );
    private final Setting<Integer> fallHelight = sgGeneral.add(new IntSetting.Builder()
            .name("fall-height")
            .sliderRange(4,12)
            .defaultValue(6)
            .build
    ());

    public AutoPos() {

        super(Main.COMBAT,"auto-pos","sets you player position to stuff");
    }

    @EventHandler
    public void onTick(){
        PlayerEntity player = mc.player;

        switch (pos.get()) {
            case Standing:
                mc.player.setPose(EntityPose.STANDING);
                break;
            case Swimming:
                // for nerds setting the pose will not actually make the player go into swimming position
                // it will it rather for the player to move as if it is in water 🤓
                player.setPosition(player.getX() + 0.5, player.getY() + 0.2, player.getZ() + 0.5);
                player.setSwimming(true);
                break;
            case Crouching:
                player.setSneaking(true);
                break;
            case Falling:
                ((ClientPlayerEntity) player).networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(player.getX(), player.getY() + fallHelight.get(), player.getZ(), true));
        }
    }

    @Override
    public void onDeactivate() {
        mc.player.setPose(EntityPose.STANDING);
        super.onDeactivate();
    }
}
