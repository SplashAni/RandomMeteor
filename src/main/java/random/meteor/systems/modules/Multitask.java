package random.meteor.systems.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.hit.HitResult;
import random.meteor.systems.Mod;

public class Multitask extends Mod {
    public Multitask() {
        super("multi-task", "Break blocks while eating");
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.crosshairTarget.getType() == HitResult.Type.ENTITY && mc.options.attackKey.wasPressed()) {
            mc.interactionManager.attackEntity(mc.player, mc.targetedEntity);
        }
    }
}
