package random.meteor.systems.modules.RM;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import random.meteor.Main;

public class NoInterp extends Module {
    public NoInterp() {
        super(Main.RM, "no-interp", "insane");
    }


    @EventHandler
    public void onTick(TickEvent.Pre event) {
    }


}
