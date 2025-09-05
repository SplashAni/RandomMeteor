package random.meteor.systems.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.orbit.EventHandler;
import random.meteor.util.Category;
import random.meteor.util.Mod;

public class FeetTrap extends Mod {
    public FeetTrap() {
        super("feet-trap", Category.PVP);
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        System.out.println("ticky");
    }

}
