package random.meteor.util.player;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.orbit.EventHandler;
import random.meteor.events.RotationSendPacketEvent;
import random.meteor.manager.Manager;

import java.util.ArrayList;
import java.util.List;

public class RotationUtil extends Manager {
    private static List<RotationGoal> rotations;
    RotationGoal currentRotationGoal;

    @Override
    public void onInitialize() {
        setEvents(true);
        MeteorClient.EVENT_BUS.subscribe(this);
        rotations = new ArrayList<>();
    }

    @EventHandler
    public void onRotationSendPacketEvent(RotationSendPacketEvent event) {

        System.out.println("canceling rot event");
        event.cancel();
        event.redirect(0,0);
    }

}
