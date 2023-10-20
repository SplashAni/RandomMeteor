package random.meteor.systems.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import random.meteor.Main;

public class Speed extends Module {
    public Speed() {
        super(Main.RM,"speedee","ok");
    }
    @EventHandler
    public void onTick(TickEvent.Pre event){

    }
}
