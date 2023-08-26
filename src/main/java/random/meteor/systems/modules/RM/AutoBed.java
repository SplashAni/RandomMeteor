package random.meteor.systems.modules.RM;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import random.meteor.Main;

public class AutoBed extends Module {
    public AutoBed() {
        super(Main.RM, "auto-bed", "god tier");
    }

    @Override
    public void onActivate() {
        super.onActivate();
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {

    }

}

