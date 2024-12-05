package random.meteor.systems.modules;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import random.meteor.systems.Mod;

import java.util.Objects;

public class BowCart extends Mod {
    private final SettingGroup sgRender = settings.createGroup("Render");

    // Render

    private final Setting<ShapeMode> shapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>()
        .name("shape-mode")
        .description("How the shapes are rendered.")
        .defaultValue(ShapeMode.Both)
        .build()
    );

    private final Setting<SettingColor> sideColor = sgRender.add(new ColorSetting.Builder()
        .name("side-color")
        .description("The side color of the rendering.")
        .defaultValue(new SettingColor(225, 0, 0, 75))
        .build()
    );

    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder()
        .name("line-color")
        .description("The line color of the rendering.")
        .defaultValue(new SettingColor(225, 0, 0, 255))
        .build()
    );
    ProjectileEntity currentProjectile;
    BlockPos predicted;
    Stage stage;

    public BowCart() {
        super("bow-cart", "cart bow");
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        FindItemResult rail = InvUtils.findInHotbar(Items.RAIL);
        FindItemResult tntCart = InvUtils.findInHotbar(Items.TNT_MINECART);

        if (predicted != null && BlockUtils.canPlace(predicted.up(), true)) {
            BlockPos pos = predicted.up();

            BlockUtils.place(
                pos, rail, 0, true
            );

            InvUtils.swap(tntCart.slot(), false);

            Objects.requireNonNull(mc.interactionManager)
                .interactBlock(mc.player, Hand.MAIN_HAND,
                    new BlockHitResult(new Vec3d(pos.getX() + 0.5,
                        pos.getY() + 0.5, pos.getZ() + 0.5),
                        Direction.UP, pos, true));


            InvUtils.swapBack();

        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @EventHandler
    public void onRender(Render3DEvent event) {

        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof ProjectileEntity) {
                predicted = getTrajectoryEnd(entity, event.tickDelta);
                if (predicted != null && entity.getVelocity() != Vec3d.ZERO) {
                    event.renderer.box(predicted, sideColor.get(), lineColor.get(), shapeMode.get(), 0);
                }
            }
        }

    }

    private BlockPos getTrajectoryEnd(Entity entity, float tickDelta) {
        Vec3d start = new Vec3d(
            MathHelper.lerp(tickDelta, entity.lastRenderX, entity.getX()),
            MathHelper.lerp(tickDelta, entity.lastRenderY, entity.getY()),
            MathHelper.lerp(tickDelta, entity.lastRenderZ, entity.getZ())
        );

        float drag = 0.92f;
        float worldGravity = 0.03f;
        double speed = entity.getVelocity().length();

        Vec3d velocity = entity.getVelocity().normalize().multiply(speed);

        int ticks = 250;
        BlockPos lastHitPos = null;


        for (int i = 0; i < ticks; i++) {
            velocity = velocity.multiply(drag);
            start = start.add(velocity.x, velocity.y - worldGravity, velocity.z);

            HitResult hitResult = Objects.requireNonNull(mc.world).raycast(
                new RaycastContext(start,
                    start.add(velocity), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE,
                    entity));

            if (hitResult.getType() != HitResult.Type.MISS) {
                lastHitPos = new BlockPos(BlockPos.ofFloored(hitResult.getPos()));
            }
        }

        return lastHitPos;
    }

    public enum Stage {
        Waiting,
        Rail,
        Cart
    }

}
