package random.meteor.systems.modules.RM;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ButtonBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import random.meteor.Main;
import random.meteor.systems.modules.utils.Utils;

import java.util.ArrayList;

import static meteordevelopment.meteorclient.utils.entity.TargetUtils.getPlayerTarget;
import static meteordevelopment.meteorclient.utils.entity.TargetUtils.isBadTarget;
import static meteordevelopment.meteorclient.utils.player.Rotations.getYaw;
import static meteordevelopment.meteorclient.utils.world.BlockUtils.canPlace;

public class PistonPush extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgRender = settings.createGroup("Render");

    private final Setting<Integer> range = sgGeneral.add(new IntSetting.Builder()
        .name("target-range")
        .description("Range to find target.")
        .defaultValue(12)
        .range(1, 12)
        .sliderMax(12)
        .build()
    );
    public final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .defaultValue(Mode.Redstone)
        .build()
    );
    private final Setting<Boolean> debug = sgGeneral.add(new BoolSetting.Builder()
        .name("debug")
        .defaultValue(true)
        .build()
    );
    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
        .name("rotate")
        .defaultValue(false)
        .build()
    );
    private final Setting<Integer> pistonDelay = sgGeneral.add(new IntSetting.Builder()
        .name("piston-delay")
        .defaultValue(1)
        .min(1)
        .sliderRange(1, 50)
        .build()
    );
    private final Setting<Integer> pushDelay = sgGeneral.add(new IntSetting.Builder()
        .name("push-delay")
        .defaultValue(1)
        .min(1)
        .sliderRange(1, 50)
        .build()
    );
    private final Setting<Boolean> swing = sgGeneral.add(new BoolSetting.Builder()
        .name("swing")
        .defaultValue(true)
        .build()
    );
    private final Setting<Boolean> holeFill = sgGeneral.add(new BoolSetting.Builder()
        .name("holefill")
        .defaultValue(true)
        .build()
    );
    private final Setting<Integer> holefillDelay = sgGeneral.add(new IntSetting.Builder()
        .name("hole-fill-delay")
        .defaultValue(1)
        .min(1)
        .sliderRange(1, 50)
        .build()
    );
    //k
    private final Setting<Boolean> clearAfter = sgGeneral.add(new BoolSetting.Builder()
        .name("clear-after")
        .defaultValue(false)
        .build()
    );
    private final Setting<Integer> clearDelay = sgGeneral.add(new IntSetting.Builder()
        .name("clear-delay")
        .defaultValue(1)
        .min(1)
        .sliderRange(1, 50)
        .build()
    );

    // RENDER

    private final Setting<Boolean> render = sgRender.add(new BoolSetting.Builder()
        .name("render")
        .defaultValue(false)
        .build());

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
    public PistonPush() {
        super(Main.RM,"piston-push","button mode");
    }
    BlockPos pistonPos, pushPos,holefillPos;
    PlayerEntity target;
    Stage stage;
    int rotationTick,pistonTick, pushTick,clearTick,holefillTick;
    FindItemResult piston,push;
    @Override
    public void onActivate() {
        holefillPos = null;
        pistonPos =  null;
        pushPos = null;
        target = null;
        piston = null;
        push = null;


        pushTick = 0;
        clearTick = 0;
        pistonTick = 0;
        rotationTick = 0;
        holefillTick = 0;

        stage = Stage.Preparing;
        super.onActivate();
    }
    @EventHandler
    public void onTick(TickEvent.Post event) {
        target = getPlayerTarget(range.get(), SortPriority.LowestDistance);
        if (isBadTarget(target, range.get())) {
            if (debug.get()) error("No target found, toggling...");
            toggle();
            return;
        }

        piston = InvUtils.find(Items.PISTON, Items.STICKY_PISTON);
        switch (mode.get()){
            case Redstone -> push = InvUtils.findInHotbar(Items.REDSTONE_BLOCK);
            case Button -> push = InvUtils.findInHotbar(itemStack -> Block.getBlockFromItem(itemStack.getItem()) instanceof ButtonBlock);
        }

        if (!piston.isHotbar() || !push.isHotbar()) {
            if (debug.get()) error("Required items found, toggling...");
            toggle();
            return;
        }

        switch (stage) {
            case Preparing -> {
                BlockPos pos = target.getBlockPos();
                holefillPos = pos;
                pistonPos = pistonPos(pos);

                switch(mode.get()) {
                    case Button -> pushPos = buttonPos(pos);
                    case Redstone -> pushPos = redstonePos(pistonPos);
                }
                if (pistonPos == null) {
                    if (debug.get()) error("No possible positions found, toggling...");
                    toggle();
                    return;
                }
                stage = Stage.Piston;
            }
            case Piston -> {
                mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(pistonYaw(pistonPos),0,true));
                /*
                * first silent piston push ever? , thanks cally (:
                * */
                pistonTick++;
                if (pistonTick >= pistonDelay.get() +1) {
                    place();
                    stage = Stage.Push;
                }
            }
            case Push -> {
                pushTick++;
                if (pushTick >= pushDelay.get()) {
                    if (pushPos == null) {
                        if (debug.get()) error("No push positions found,toggling...");
                        toggle();
                        return;
                    }
                    BlockUtils.place(pushPos, push, rotate.get(), 50, swing.get(), true);

                    if (clearAfter.get()) {
                        stage = Stage.Clear;
                    } else {
                        toggle();
                    }
                }
            }
            case Clear -> {
                clearTick++;
                if (clearTick >= clearDelay.get()) {
                    FindItemResult pic = InvUtils.find(Items.DIAMOND_PICKAXE, Items.NETHERITE_PICKAXE);
                    if (pic.isHotbar()) {
                        InvUtils.swap(pic.slot(), true);
                    }
                    BlockUtils.breakBlock(pistonPos, swing.get());
                    if (Utils.state(pistonPos) == Blocks.AIR){
                        if(holeFill.get()){
                            stage = Stage.HoleFill;
                        }else {
                            toggle();
                        }
                    }
                    if (pic.isHotbar()) {
                        InvUtils.swapBack();
                    }
                }
            }
            case HoleFill -> {
                FindItemResult obby = InvUtils.findInHotbar(Items.OBSIDIAN);
                if(!obby.isHotbar()) {
                    toggle();
                }
                else {
                    holefillTick++;
                    if (holefillTick >= holefillDelay.get()) {
                        BlockUtils.place(holefillPos, obby, rotate.get(), 50, swing.get(), false);
                        holefillTick = 0;
                        toggle();
                    }
                }
            }
        }
    }
    private void place() {
        InvUtils.swap(piston.slot(), true);
        Vec3d hitPos = Vec3d.ofCenter(pistonPos);
        BlockHitResult bhr = new BlockHitResult(hitPos, Direction.UP, pistonPos, false);
        if(swing.get()) mc.player.swingHand(Hand.MAIN_HAND);
        mc.getNetworkHandler().sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND,bhr,1));
        InvUtils.swapBack();
    }



    private BlockPos pistonPos(BlockPos pos) {
        BlockPos p = pos.up(1);
        if(canPlace(p.add(-1,0,0))) return p.add(-1,0,0); // 90
        else if (canPlace(p.add(0,0,1))) return  p.add(0,0,1); // 360
        else if (canPlace(p.add(1,0,0))) return  p.add(1,0,0); // -90
        else if (canPlace(p.add(0,0,-1))) return  p.add(0,0,-1); //180
        return null;
    }
    private float pistonYaw(BlockPos pos) {
        BlockPos p = pos.up(1);
        if(canPlace(p.add(-1,0,0))) return 90;
        else if (canPlace(p.add(0,0,1))) return 360;
        else if (canPlace(p.add(1,0,0))) return  -90;
        else if (canPlace(p.add(0,0,-1))) return  180;
        return 0;
    }
    private BlockPos buttonPos(BlockPos pos) {
        BlockPos p = pos.up(1);
        if(canPlace(p.add(-1,0,0))) return p.add(-2,0,0); // 90
        else if (canPlace(p.add(0,0,1))) return  p.add(0,0,2); // 360
        else if (canPlace(p.add(1,0,0))) return  p.add(2,0,0); // -90
        else if (canPlace(p.add(0,0,-1))) return  p.add(0,0,-2); //180
        return null;
    }
    private BlockPos redstonePos(BlockPos pos){
        ArrayList<BlockPos> poses = new ArrayList<>();
        for(Direction dir : Direction.values()) {
            /*
            * insane epxlaning by splashabi
            * streams all directions , basically - 1 and 1
            * once that is done it checks if can place if it can place it adds to arraylis
            * if aray lis is empty it null otherwise it returns the blockpowos (:
            * */
            if(BlockUtils.canPlace(pos.offset(dir))) poses.add(pos.offset(dir));
        }
        if(poses.isEmpty()) return null;

        return poses.get(0);
    }


    @EventHandler
    private void onRender3D(Render3DEvent event) {
        if (!render.get()) return;
        if(holefillPos != null) event.renderer.box(pushPos, sideColor.get(), lineColor.get(), shapeMode.get(), 0);
        if(pushPos != null) event.renderer.box(pushPos, sideColor.get(), lineColor.get(), shapeMode.get(), 0);
        if(pistonPos != null) event.renderer.box(pistonPos, sideColor.get(), lineColor.get(), shapeMode.get(), 0);
    }


    private enum Stage{
        Preparing,
        Piston,
        Push,
        HoleFill,

        Clear
    }
    public enum Mode{
        Button,
        Redstone;
    }
}
