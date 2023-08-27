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
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import random.meteor.Main;
import random.meteor.systems.modules.utils.PistonInfo;
import random.meteor.systems.modules.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static meteordevelopment.meteorclient.utils.entity.TargetUtils.getPlayerTarget;
import static meteordevelopment.meteorclient.utils.entity.TargetUtils.isBadTarget;
import static random.meteor.systems.modules.utils.Utils.canContinue;
import static random.meteor.systems.modules.utils.Utils.increment;

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
    public final Setting<RotationMode> rotationMode = sgGeneral.add(new EnumSetting.Builder<RotationMode>()
        .name("rotation-mode")
        .defaultValue(RotationMode.None)
        .build()
    );
    public final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("item-mode")
        .defaultValue(Mode.RedstoneBlock)
        .build()
    );
    private final Setting<Boolean> debug = sgGeneral.add(new BoolSetting.Builder()
        .name("debug")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> oldPistons = sgGeneral.add(new BoolSetting.Builder()
        .name("old-pistons")
        .description("Uses pistons that are already placed")
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
        .visible(holeFill::get)
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
        .visible(clearAfter::get)
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
    BlockPos pistonPos,holefillPos;
    PlayerEntity target;
    Stage stage;
    int rotationTick,pistonTick, pushTick,clearTick,holefillTick;
    FindItemResult piston,push;
    private PistonInfo pistonInfo;

    @Override
    public void onActivate() {
        holefillPos = null;
        pistonPos = null;
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
            case Button -> {
                push = InvUtils.findInHotbar(itemStack ->  Block.getBlockFromItem(itemStack.getItem()) instanceof ButtonBlock);
            }
            case RedstoneBlock -> {
                push = InvUtils.findInHotbar(Items.REDSTONE_BLOCK);
            }
        }

        if (!piston.isHotbar() || !push.isHotbar()) {
            if (debug.get()) error("Required items found, toggling...");
            toggle();
            return;
        }

        switch (stage) {
            case Preparing -> {
                holefillPos = target.getBlockPos();
                pistonInfo = pistonPos();

                if (pistonInfo == null) {
                    if (debug.get()) error("No possible positions found, toggling...");
                    toggle();
                } else {
                    stage = Stage.Piston;
                }
            }
            case Piston -> {

                PistonInfo result = pistonPos();

                if(result != null) {
                    pistonPos = pistonInfo.pos();

                    switch(rotationMode.get()){
                        case None -> mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(pistonYaw(pistonInfo.direction()),0,true));
                        case PistonYaw -> Rotations.rotate(pistonYaw(pistonInfo.direction()),0);
                        case PistonBlock -> Rotations.rotate(Rotations.getYaw(pistonPos),Rotations.getPitch(pistonPos));
                    }

                    pistonTick++;
                    if (canContinue(pistonTick, pistonDelay.get() + 2)) {
                        BlockUtils.place(pistonInfo.pos(), piston, false, 50, true, false);
                        pistonTick = 0;
                        if (debug.get()) info("Placed piston facing " + pistonInfo.direction());

                        stage = Stage.Push;

                    }

                }else {
                    if (debug.get()) error("No possible positions found, toggling...");
                    toggle();
                }
            }
            case Push -> {
                pushTick++;
                if (pushTick >= pushDelay.get()) {

                    BlockPos targetPos = null;

                    switch (mode.get()){
                        case RedstoneBlock -> targetPos = redstonePos(pistonPos);
                        case Button -> targetPos = incrementPos(pistonPos,pistonInfo.direction());
                    }



                    if (targetPos== null) {
                        if (debug.get()) error("No push positions found,toggling...");
                        toggle();
                        return;
                    }

                    BlockUtils.place(targetPos, push, rotate.get(), 50, swing.get(), true);

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



    public PistonInfo pistonPos() {
        BlockPos p = target.getBlockPos().up();

        List<PistonInfo> results = new ArrayList<>();

        for (Direction d : Direction.values()) {
            if (d == Direction.DOWN || d == Direction.UP) continue;

            if (isValid(p.offset(d), Blocks.PISTON)) {
                results.add(new PistonInfo(p.offset(d), d));
            }
        }

        if (results.isEmpty() || results.size() < 2) {
            return null;
        }

        return results.get(1);
    }


    public int pistonYaw(Direction d){

        switch (d){
            case NORTH -> {
                return 180;
            }
            case EAST -> {
                return -90;
            }
            case SOUTH -> {
                return 0;
            }
            case WEST -> {
                return 90;
            }
        }

        return 0;
    }
    public BlockPos incrementPos(BlockPos pos, Direction d) {
        switch (d) {
            case NORTH, SOUTH -> {
                return new BlockPos(pos.getX(), pos.getY(), pos.getZ() + increment(pos.getZ()));
            }
            case WEST, EAST -> {
                return new BlockPos(pos.getX() + increment(pos.getX()), pos.getY(), pos.getZ());
            }
        }
        return pos;
    }

    public boolean isValid(BlockPos pos, Block block) {
        return BlockUtils.canPlace(pos) || (oldPistons.get() && Utils.state(pos).equals(block));
    }


    private BlockPos redstonePos(BlockPos pos){
        if(pos == null) return null;
        ArrayList<BlockPos> poses = new ArrayList<>();
        for(Direction dir : Direction.values()) {
            if(BlockUtils.canPlace(pos.offset(dir))) poses.add(pos.offset(dir));
        }
        if(poses.isEmpty()) return null;

        return poses.get(0);
    }

    @EventHandler
    private void onRender3D(Render3DEvent event) {
        if (!render.get()) return;
        if(pistonPos != null) event.renderer.box(pistonPos, sideColor.get(), lineColor.get(), shapeMode.get(), 0);
        if(redstonePos(pistonPos)!= null) event.renderer.box(redstonePos(pistonPos), sideColor.get(), lineColor.get(), shapeMode.get(), 0);
    }


    private enum Stage{
        Preparing,
        Piston,
        Push,
        HoleFill,

        Clear
    }
    public enum RotationMode {
        PistonYaw,
        PistonBlock,
        None
    }
    public enum Mode {
        Button,
        RedstoneBlock
    }
}
