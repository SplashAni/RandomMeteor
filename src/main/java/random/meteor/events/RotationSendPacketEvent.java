package random.meteor.events;

import meteordevelopment.meteorclient.events.Cancellable;

public class RotationSendPacketEvent extends Cancellable {
    int redirectYaw, redirectPitch;

    public RotationSendPacketEvent() {
    }

    public void redirect(int yaw, int pitch) {
        this.redirectYaw = yaw;
        this.redirectPitch = pitch;
    }

    public int getRedirectYaw() {
        return redirectYaw;
    }

    public int getRedirectPitch() {
        return redirectPitch;
    }
}
