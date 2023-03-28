package random.meteor.systems.modules.misc;

import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.EntityPose;
import random.meteor.Main;

public class AutoSwim extends Module {
    public AutoSwim() {
        super(Main.MISC,"auto-swim","All credits due to pegasus");
    }
    @EventHandler
    public void onTick(){
        if (mc.player.isTouchingWater()){
            mc.player.setPose(EntityPose.SWIMMING);
        }
    }
}
