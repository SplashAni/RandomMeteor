package random.meteor.systems.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.orbit.EventHandler;
import random.meteor.util.system.Category;
import random.meteor.util.system.Mod;

public class RotationTests extends Mod {
    public RotationTests() {
        super("rotation-test",Category.PVP);
    }
    @EventHandler
    public void onTick(TickEvent.Pre event){
        Rotations.setCamRotation(50,50);
        Rotations.rotating = true;
    }
}
