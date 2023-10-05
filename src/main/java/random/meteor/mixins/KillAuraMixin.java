package random.meteor.mixins;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.combat.KillAura;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import random.meteor.systems.modules.utils.ShapesUtils;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = KillAura.class,remap = false)

public class KillAuraMixin extends Module {

    private SettingGroup sgRender;
    /*settings*/
    private Setting<Boolean> render;
    private Setting<Integer> upSpeed;
    private Setting<Integer> downSpeed;
    private Setting<Integer> size;
    Setting<ShapesUtils.Shapes> mode;
    private Setting<SettingColor> color;

    @Shadow
    @Final
    private final List<Entity> targets = new ArrayList<>();

    double currentHeight = 0.0;
    private boolean isUp = true;


    public KillAuraMixin(Category category, String name, String description) {
        super(category, name, description);
    }


    @Inject(method = "<init>", at = @At("TAIL"))

    private void onInit(CallbackInfo info) {

        sgRender = settings.createGroup("Render");


        render = sgRender.add(new BoolSetting.Builder()
            .name("render")
            .defaultValue(true)
            .build()
        );

        mode = sgRender.add(new EnumSetting.Builder<ShapesUtils.Shapes>()
            .name("shape")
            .description("what shape to render")
            .defaultValue(ShapesUtils.Shapes.Circle)
            .visible(render::get)
            .build()
        );

        upSpeed = sgRender.add(new IntSetting.Builder()
            .name("up-speed")
            .defaultValue(1)
            .range(1, 20)
            .sliderMax(20)
            .visible(render::get)
            .build()
        );

        downSpeed = sgRender.add(new IntSetting.Builder()
            .name("down-speed")
            .defaultValue(1)
            .range(1, 20)
            .sliderMax(20)
            .visible(render::get)
            .build()
        );


        size = sgRender.add(new IntSetting.Builder()
            .name("size")
            .defaultValue(15)
            .range(1, 20)
            .sliderMax(20)
            .visible(render::get)
            .build()
        );

        color = sgRender.add(new ColorSetting.Builder()
            .name("color")
            .description("The line color of the rendering.")
            .defaultValue(new SettingColor(50, 0, 255, 18))
            .visible(render::get)
            .build()
        );

    }

    @Inject(method = "onTick", at = @At("HEAD"),remap = false)
    public void onTick(TickEvent.Pre event, CallbackInfo ci) {

        if (isUp) {
            currentHeight += upSpeed.get() * 0.1;
            if (currentHeight >= 2) {
                isUp = false;
            }
        } else {
            currentHeight -= downSpeed.get() * 0.1;
            if (currentHeight <= 0.0) {
                isUp = true;
            }
        }

        currentHeight = Math.min(2, currentHeight);
    }
    public void renderShape(Render3DEvent event) {

        List<Entity> targets = this.targets;

        if (targets.isEmpty()) return;

        for (Entity player : targets) {

            Vec3d center = player.getPos();

            int segments = switch (mode.get()) {
                case Triangle -> 3;
                case Square -> 4;
                case Hexagon -> 6;
                case Nonagon -> 9;
                case Circle -> 40;

            };


            double yOffset = 0.0;

            for (int i = 0; i < size.get() * 10; i++) {
                for (int j = 0; j < segments; j++) {
                    double a1 = Math.PI * 2* j / segments;
                    double a2 = Math.PI * 2 * (j + 1) / segments;

                    double x1 = center.x + 1 * Math.cos(a1);
                    double y1 = center.y + currentHeight + yOffset;
                    double z1 = center.z + 1 * Math.sin(a1);

                    double x2 = center.x + 1 * Math.cos(a2);
                    double y2 = center.y + currentHeight + yOffset;
                    double z2 = center.z + 1 * Math.sin(a2);

                    event.renderer.line(x1, y1, z1, x2, y2, z2, color.get());
                }

                yOffset -= 0.001;
            }
        }
    }

    @EventHandler
    public void onRender(Render3DEvent event) {

        if (!Modules.get().get(KillAura.class).isActive() || !render.get()) return;

        renderShape(event);

    }
}
