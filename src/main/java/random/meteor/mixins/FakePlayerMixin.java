package random.meteor.mixins;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.player.FakePlayer;
import meteordevelopment.meteorclient.utils.entity.fakeplayer.FakePlayerEntity;
import meteordevelopment.meteorclient.utils.entity.fakeplayer.FakePlayerManager;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = FakePlayer.class,remap = false)
public class FakePlayerMixin extends Module {
    @Shadow
    @Final
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    @Unique
    private Setting<Boolean> damage;
    @Unique
    private Setting<Boolean> totemPops;

    public FakePlayerMixin(Category category, String name, String description) {
        super(category, name, description);
    }

    @Inject(method = "<init>",at = @At("TAIL"))
    private void init(CallbackInfo ci){

        damage = sgGeneral.add(new BoolSetting.Builder()
                .name("damage")
                .defaultValue(true)
                .build()
        );

        totemPops = sgGeneral.add(new BoolSetting.Builder()
                .name("totem-pops")
                .defaultValue(true)
                .build()
        );
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        List<FakePlayerEntity> e = FakePlayerManager.getFakePlayers();


        if (e.isEmpty()) return;

        for (PlayerEntity fp : e) {
            if (mc.targetedEntity == e && mc.options.attackKey.wasPressed()) {
                mc.particleManager.addEmitter(fp, ParticleTypes.CRIT);
            }
        }
    }
}
