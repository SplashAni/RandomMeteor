package random.meteor.systems.modules;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.EntityTypeListSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.particle.ParticleTypes;
import random.meteor.Main;
import random.meteor.systems.Mod;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class DeathEffect extends Mod {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Set<EntityType<?>>> entities = sgGeneral.add(new EntityTypeListSetting.Builder()
            .name("entities")
            .onlyAttackable()
            .defaultValue(EntityType.PLAYER)
            .build()
    );

    private final Setting<Integer> renderTime = sgGeneral.add(new IntSetting.Builder()
            .name("render-time")
            .description("time to render")
            .defaultValue(1)
            .range(1, 5)
            .sliderMax(5)
            .build()
    );

    public DeathEffect() {
        super("death-effect", "Renders an effect on entities that recently died");
    }

    private final Map<Entity, Long> toRender = new HashMap<>();

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        long currentTime = System.currentTimeMillis();

        toRender.entrySet().removeIf(entry -> currentTime - entry.getValue() > renderTime.get() * 1000);

        for (Entity e : Objects.requireNonNull(mc.world).getEntities()) {
            if (!e.isAlive() && entities.get().contains(e.getType())) {
                toRender.put(e, currentTime);
            }
        }
    }

    @EventHandler
    public void onRender(Render3DEvent event) {
        long timer = System.currentTimeMillis();

        Iterator<Map.Entry<Entity, Long>> iterator = toRender.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Entity, Long> entry = iterator.next();
            if (timer - entry.getValue() > renderTime.get() * 1000) {
                iterator.remove();
            } else {
                Objects.requireNonNull(mc.world).addParticle(ParticleTypes.EXPLOSION, entry.getKey().getX(), entry.getKey().getY(), entry.getKey().getZ(), 0, 0, 0);
            }
        }
    }
}
