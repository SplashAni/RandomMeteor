package random.meteor.util.player;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class RotationGoal {
    RotationType rotationType;
    int yaw, pitch, prevYaw, prevPitch;

    public RotationGoal(RotationType rotationType, int yaw, int pitch) {
        this.rotationType = rotationType;
        this.yaw = yaw;
        this.pitch = pitch;
        this.prevYaw = (int) mc.player.getYaw();
        this.prevPitch = (int) mc.player.getPitch();
    }

    public RotationType rotationType() {
        return rotationType;
    }

    public int yaw() {
        return yaw;
    }

    public int pitch() {
        return pitch;
    }


}
