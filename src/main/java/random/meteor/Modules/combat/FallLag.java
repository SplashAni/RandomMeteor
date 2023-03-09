package random.meteor.Modules.combat;

import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import random.meteor.Main;

import static random.meteor.Utils.MiscUtils.getFallDistance;
import static random.meteor.Utils.MiscUtils.isFalling;

public class FallLag extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Boolean> rubberband = sgGeneral.add(new BoolSetting.Builder()
            .name("rubberband")
            .description("Rubber bands you to prevent damage")
            .defaultValue(true)
            .build()
    );
    public FallLag() {
        super(Main.MISC,"better-fall","saving lives tbh");
    }

    @EventHandler
    private void onTick(){

    }
    private void rubberBand(){
        if (isFalling() && getFallDistance() <= Math.ceil(mc.player.getVelocity().y * -1.0) && (int) Math.ceil((getFallDistance() - 3.0) * 0.8) > 0) {
            Vec3d velocity = mc.player.getVelocity();
            Vec3d playerPos = mc.player.getPos();

            Vec3d targetPos = playerPos.add(velocity.multiply(1.5, 1.5, 1.5));
            RaycastContext raycastContext = new RaycastContext(playerPos, targetPos, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player);
            BlockHitResult result = mc.world.raycast(raycastContext);

            if (result.getType() != HitResult.Type.MISS) {
                BlockPos hitPos = result.getBlockPos();
                Vec3d safePos = new Vec3d(hitPos.getX() + 0.5, hitPos.getY() + 1.0, hitPos.getZ() + 0.5);
                mc.player.setPosition(safePos.x, safePos.y, safePos.z);
            }
        }
    }
}
