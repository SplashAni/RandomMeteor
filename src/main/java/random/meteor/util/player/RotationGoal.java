package random.meteor.util.player;

public class RotationGoal {
    RotationType rotationType;
    int yaw;
    int pitch;

    public RotationGoal(RotationType rotationType, int yaw, int pitch) {
        this.rotationType = rotationType;
        this.yaw = yaw;
        this.pitch = pitch;
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
