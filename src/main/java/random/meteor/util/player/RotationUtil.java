package random.meteor.util.player;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.world.TickEvent;
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
    public RotationGoal currentRotationGoal, prevRotation;
    private float rotationTicks = 0f;
    private int holdTick = 0, prevYaw, prevPitch;

    // todo : smooth it out xd
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
            if (currentRotationGoal.rotationType == RotationType.Server || currentRotationGoal.rotationType == RotationType.Silent) {
                updateServerLook(currentRotationGoal.yaw, currentRotationGoal.pitch);
            }
        }

        if (currentRotationGoal == null) return;

        rotationTicks += 1;
        float rotationTime = 1;

        if (rotationTicks >= rotationTime) {
            prevRotation = currentRotationGoal;

            holdTick = 100; // this gets decremented WAYY faster in the event for some reason? soooooo this is technically 1 tick xdDddd

            rotations.removeFirst();
            rotationTicks = 0;

            if (rotations.isEmpty()) {
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
        } else if (prevRotation != null && holdTick > 0) {
            event.getState().bodyYaw = prevRotation.yaw;
            event.getState().pitch = prevRotation.pitch;

            holdTick--;

            if (holdTick <= 0) {
                if (prevRotation.rotationType == RotationType.Server || prevRotation.rotationType == RotationType.Silent) {
                    updateServerLook((int) mc.player.getYaw(), (int) mc.player.getPitch()); // prevent gae desync xd
                }
                prevRotation = null;
            }
        }
    }

    @EventHandler
    public void onRotationSendPacketEvent(RotationSendPacketEvent event) {

        if (currentRotationGoal != null) {
            switch (currentRotationGoal.rotationType()) {
                case Client -> updateClientLook(currentRotationGoal.yaw(), currentRotationGoal.pitch());
                case Server, Silent -> {
                    event.cancel();
                    event.redirect(currentRotationGoal.yaw(), currentRotationGoal.pitch());
                }
            }
            return;
        }
    }
}
