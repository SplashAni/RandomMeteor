package random.meteor.systems.modules.RM;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.Target;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import random.meteor.Main;

public class Blocker extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgRender = settings.createGroup("Render");
    private final Setting<Double> range = sgGeneral.add(new DoubleSetting.Builder()
            .name("range")
            .description("The maximum range the entity can be to attack it.")
            .defaultValue(4.5)
            .min(0)
            .sliderMax(6)
            .build()
    );
    private final Setting<Double> delay = sgGeneral.add(new DoubleSetting.Builder()
            .name("delay")
            .defaultValue(12)
            .min(1)
            .sliderMax(60)
            .build()
    );

    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
            .name("rotate")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> swing = sgGeneral.add(new BoolSetting.Builder()
            .name("swing")
            .defaultValue(false)
            .build()
    );


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

    public Blocker() {
        super(Main.RM,"anti-faceplace","im the most pro alive so pro");
    }
    BlockPos pos;
    int ticks;
    @Override
    public void onActivate() {
        super.onActivate();
    }
    @EventHandler
    public void onTick(TickEvent.Pre event){
        for(Entity entity : mc.world.getEntities()){
            if(entity.getType() == EntityType.END_CRYSTAL){
                pos = entity.getBlockPos();
                if(entity.isInRange(mc.player,range.get())){
                    ticks++;
                    if(ticks >= delay.get()) {
                        FindItemResult block = InvUtils.findInHotbar(Items.OBSIDIAN);
                        if (rotate.get())
                            Rotations.rotate(Rotations.getYaw(entity), Rotations.getPitch(entity, Target.Body));
                        mc.interactionManager.attackEntity(mc.player, entity);
                        BlockUtils.place(pos, block, rotate.get(), 50, swing.get(), true);
                        if (swing.get()) mc.player.swingHand(Hand.MAIN_HAND);
                        }
                    }
                }
            }
        }

    @EventHandler
    private void onRender(Render3DEvent event) {
        if (pos == null) return;

        event.renderer.box(pos, sideColor.get(), lineColor.get(), shapeMode.get(), 0);
    }
}
