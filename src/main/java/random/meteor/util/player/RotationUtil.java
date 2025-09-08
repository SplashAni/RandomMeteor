package random.meteor.util.player;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import random.meteor.events.PlayerRenderStateEvent;
import random.meteor.events.RotationSendPacketEvent;
import random.meteor.manager.Manager;

import java.util.LinkedList;
import java.util.Objects;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class RotationUtil extends Manager {
    private static final LinkedList<RotationGoal> rotations = new LinkedList<>();
    public RotationGoal currentRotationGoal;
    private float rotationTicks = 0f;


    private static void updateServerLook(int yaw, int pitch) {
        Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(
            new PlayerMoveC2SPacket.LookAndOnGround(
                yaw,
                pitch,
                Objects.requireNonNull(mc.player).isOnGround(),
                mc.player.horizontalCollision
            )
        );
    }

    private static void updateClientLook(int yaw, int pitch) {
        if (mc.player != null) {
            mc.player.setYaw(yaw);
            mc.player.setPitch(pitch);
        }
    }

    public static void rotate(RotationType rotationType, int yaw, int pitch) {
        rotations.add(new RotationGoal(rotationType, yaw, pitch));
    }

    @Override
    public void onInitialize() {
        MeteorClient.EVENT_BUS.subscribe(this);
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (currentRotationGoal == null && !rotations.isEmpty()) {
            currentRotationGoal = rotations.getFirst();
        }

        if (currentRotationGoal == null) return;

        rotationTicks += 1;
        float rotationTime = 1;

        if (rotationTicks >= rotationTime) {
            rotations.removeFirst();
            rotationTicks = 0;

            if (rotations.isEmpty()) {

                switch (currentRotationGoal.rotationType()) {
                    case Client -> {
                        updateClientLook(currentRotationGoal.yaw(), currentRotationGoal.pitch);
                    }
                    case Server, Silent -> {

                    }
                }

                Rotations.rotating = false;
                currentRotationGoal = null;

            } else {
                currentRotationGoal = rotations.getFirst();
            }
        }
    }

    @EventHandler
    public void state(PlayerRenderStateEvent event) {
        if (currentRotationGoal != null && currentRotationGoal.rotationType == RotationType.Server) {
            event.getState().bodyYaw = currentRotationGoal.yaw;
            event.getState().pitch = currentRotationGoal.pitch;
        }
    }


    @EventHandler
    public void onRotationSendPacketEvent(RotationSendPacketEvent event) {
        if (rotations.isEmpty()) return;
        if (currentRotationGoal == null) currentRotationGoal = rotations.getFirst();

        switch (currentRotationGoal.rotationType()) {
            case Client -> updateClientLook(currentRotationGoal.yaw(), currentRotationGoal.pitch());
            case Server, Silent -> {
                event.cancel();
                event.redirect(currentRotationGoal.yaw(), currentRotationGoal.pitch());
            }
        }
    }
}
