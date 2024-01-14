package random.meteor.systems.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import random.meteor.Main;

public class MinecartSpeed extends Module {
    public MinecartSpeed() {
        super(Main.RM, "minecart-speed", "relly ez");
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (!mc.player.hasVehicle())
            return;

        Entity vehicle = mc.player.getVehicle();
        if (!(vehicle instanceof AbstractMinecartEntity)) return;
        Vec3d velocity = vehicle.getVelocity();

        double motionX = velocity.x;
        double motionY = 0;
        double motionZ = velocity.z;

        // up/down
        if (mc.options.jumpKey.isPressed())
            motionY = 1;
        else if (mc.options.sprintKey.isPressed())
            motionY = velocity.y;

        if (mc.options.forwardKey.isPressed()) {
            double speed = 0.5;
            float yawRad = vehicle.getYaw() * MathHelper.RADIANS_PER_DEGREE;

            motionX = MathHelper.sin(-yawRad) * speed;
            motionZ = MathHelper.cos(yawRad) * speed;
        }

        vehicle.setVelocity(motionX, motionY, motionZ);

    }

}
