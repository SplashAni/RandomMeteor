package random.meteor.systems.modules;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import random.meteor.Main;
import random.meteor.utils.Utils;

import static meteordevelopment.meteorclient.utils.entity.TargetUtils.getPlayerTarget;
import static meteordevelopment.meteorclient.utils.entity.TargetUtils.isBadTarget;

public class TntAura extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgPause = settings.createGroup("Pause");
    private final SettingGroup sgRender = settings.createGroup("Render");

    // GENERNEL

    private final Setting<Integer> range = sgGeneral.add(new IntSetting.Builder()
            .name("target-range")
            .description("Range to find target.")
            .defaultValue(12)
            .range(1,12)
            .sliderMax(12)
            .build()
    );

    private final Setting<Integer> placeDelay = sgGeneral.add(new IntSetting.Builder()
            .name("place-delay")
            .description("Delay to place tnt")
            .defaultValue(15)
            .range(1,35)
            .sliderMax(35)
            .build()
    );
    private final Setting<Boolean> doublePlace = sgGeneral.add(new BoolSetting.Builder()
            .name("double-place")
            .description("")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> swop = sgGeneral.add(new BoolSetting.Builder()
            .name("switch")
            .description("")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
            .name("rotate")
            .description("")
            .defaultValue(true)
            .build()
    );
    // PAUSE

    private final Setting<Boolean> antiself = sgPause.add(new BoolSetting.Builder()
            .name("anti-self")
            .description("")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> pauseMine = sgPause.add(new BoolSetting.Builder()
            .name("pause-on-mine")
            .description("")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> pauseEat = sgPause.add(new BoolSetting.Builder()
            .name("pause-on-eat")
            .description("")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> pauseDrink = sgPause.add(new BoolSetting.Builder()
            .name("pause-on-drink")
            .description("")
            .defaultValue(true)
            .build()
    );



    // RENDER

    private final Setting<Boolean> swing = sgRender.add(new BoolSetting.Builder()
            .name("swing")
            .description("")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> render = sgRender.add(new BoolSetting.Builder()
            .name("render")
            .description("renderinggg")
            .defaultValue(true)
            .build()
    );
    private final Setting<ShapeMode> shapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>()
            .name("shape-mode")
            .description("How the shapes are rendered.")
            .defaultValue(ShapeMode.Both)
            .visible(render::get)
            .build()
    );

    private final Setting<SettingColor> sideColor = sgRender.add(new ColorSetting.Builder()
            .name("side-color")
            .description("The side color of the rendering.")
            .defaultValue(new SettingColor(125, 69, 245, 75))
            .visible(render::get)
            .build()
    );

    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder()
            .name("line-color")
            .description("The line color of the rendering.")
            .defaultValue(new SettingColor(100, 34, 53, 255))
            .visible(render::get)
            .build()
    );

    private Stage stage;
    PlayerEntity target;
    BlockPos place;
    BlockPos placeDouble;
    int slot;
    int ticks = 0;

    public TntAura() {
        super(Main.RM,"tnt-aura","Automatically places tnt ontop of targets head");
    }

    @Override
    public void onActivate() {
        target = null;
        place = null;
        placeDouble = null;

        stage = Stage.Preparing;

        super.onActivate();
    }

    @EventHandler
    public void onTick(TickEvent.Pre event){
        target = getPlayerTarget(range.get(), SortPriority.LowestDistance);

        if (isBadTarget(target, range.get())) {
            return;
        }

        if(!tntResult().found() ){
            return;
        }

        if (antiself.get() && Utils.isSelf(target)) return;

        if (PlayerUtils.shouldPause(pauseMine.get(), pauseEat.get(), pauseDrink.get())) return;

        ticks++;
        switch (stage){
            case Preparing -> {
                assert mc.player != null;
                slot = mc.player.getInventory().selectedSlot;
                place = target.getBlockPos().up(2);
                placeDouble = target.getBlockPos().up(3);
                stage = Stage.Placing;
            }
            case Placing -> {
                if(ticks >= placeDelay.get()) {
                    ticks = 0;
                    if (swop.get()) {
                        mc.player.getInventory().selectedSlot = tntResult().slot();
                    }

                    BlockUtils.place(place, tntResult(), rotate.get(), 50, swing.get(), true);
                    if (doublePlace.get()) {
                        BlockUtils.place(placeDouble, tntResult(), rotate.get(), 50, swing.get(), true);
                    }
                    stage = Stage.Igniting;
                }
            }
            case Igniting -> {
                interactIngiter(place);
                if(doublePlace.get()){
                    interactIngiter(placeDouble);
                }
                stage = Stage.Placing;
            }
            case Toggling -> {
                mc.player.getInventory().selectedSlot = slot;
                toggle();
            }
        }
    }
    public void interactIngiter(BlockPos pos) {
        InvUtils.swap(igniteResult().slot(),true);

        assert mc.interactionManager != null;
        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND,
                new BlockHitResult(new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), Direction.UP, pos, true));
        InvUtils.swapBack();
    }
    private FindItemResult tntResult() {
        return InvUtils.findInHotbar(itemStack ->
                itemStack.getItem() == Items.TNT
        );
    }
    private FindItemResult igniteResult() {
        return InvUtils.findInHotbar(itemStack ->
                itemStack.getItem() == Items.FIRE_CHARGE || itemStack.getItem() == Items.FLINT_AND_STEEL
        );
    }
    @EventHandler
    private void onRender(Render3DEvent event) {
        if(render.get()) {
            if (place == null) return;
            event.renderer.box(place, sideColor.get(), lineColor.get(), shapeMode.get(), 0);

            if (doublePlace.get() && placeDouble != null) {
                event.renderer.box(placeDouble, sideColor.get(), lineColor.get(), shapeMode.get(), 0);
            }
        }
    }

    private enum Stage{
        Preparing,
        Placing,
        Igniting,
        Toggling
    }
}
