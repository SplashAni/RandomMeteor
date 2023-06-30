package random.meteor.systems.modules.RM;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import random.meteor.Main;
import random.meteor.systems.modules.utils.Utils;

public class PistonBurrow extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgRender = settings.createGroup("Render");

    private final Setting<Boolean> center = sgGeneral.add(new BoolSetting.Builder()
        .name("center")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
        .name("rotate")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> trap = sgGeneral.add(new BoolSetting.Builder()
        .name("trap")
         .description("Highly recommended")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> swing = sgGeneral.add(new BoolSetting.Builder()
        .name("swing")
        .defaultValue(false)
        .build()
    );

    //DELAYS

    private final Setting<Integer> pistonDelay = sgGeneral.add(new IntSetting.Builder()
        .name("piston-delay")
        .defaultValue(1)
        .min(1)
        .sliderRange(1, 50)
        .build()
    );
    private final Setting<Integer> redstoneDelay = sgGeneral.add(new IntSetting.Builder()
        .name("redstone-delay")
        .defaultValue(1)
        .min(1)
        .sliderRange(1, 50)
        .build()
    );
    private final Setting<Integer> burrowDelay = sgGeneral.add(new IntSetting.Builder()
        .name("burrow-delay")
        .defaultValue(1)
        .min(1)
        .sliderRange(1, 50)
        .build()
    );

    private final Setting<Integer> trapDelay = sgGeneral.add(new IntSetting.Builder()
        .name("trap-delay")
        .defaultValue(1)
        .min(1)
        .sliderRange(1, 50)
        .visible(trap::get)
        .build()
    );

    // extra shit
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


    public PistonBurrow() {
        super(Main.RM,"piston-burrow","aah yes sex");
    }

    BlockPos trapTopPos,trapBackPos,pistonPos,redstonePos,burrowPos;
    int redstoneTick,pistonTick,burrowTick,trapTick,clearTick;
    Stage stage;
    @Override
    public void onActivate() {
        trapBackPos = null;
        trapTopPos = null;
        pistonPos = null;
        redstonePos = null;
        burrowPos = null;

        redstoneTick = 0;
        pistonTick = 0;
        burrowTick = 0;
        trapTick = 0;
        clearTick = 0;

        stage = Stage.Preparing;
        super.onActivate();
    }
    @EventHandler
    public void onTick(TickEvent.Pre event){
        if(mc.player == null || mc.world == null) return;
        if(center.get()) PlayerUtils.centerPlayer();

        switch (stage){
            case Preparing -> {
                BlockPos pos = mc.player.getBlockPos();

                trapTopPos = pos.up(2);
                trapBackPos = pos.add(1,1,0).down(1);
                burrowPos = pos.add(-1,0,0);
                pistonPos = pos.add(-2,0,0);
                redstonePos = pos.add(-3,0,0);

                if(trap.get()){
                    stage = Stage.Trap;
                }
                else {
                    stage = Stage.Burrow;
                }
            }
            case Trap -> {
                trapTick++;
                if(trapTick >= trapDelay.get()) {
                    FindItemResult block = InvUtils.findInHotbar(itemStack -> itemStack.getItem() instanceof BlockItem);
                    if (!block.isHotbar()) return;
                    if (BlockUtils.canPlace(trapTopPos))
                        BlockUtils.place(trapTopPos, block, rotate.get(), 50, swing.get(), true);
                    if (BlockUtils.canPlace(trapBackPos))
                        BlockUtils.place(trapBackPos, block, rotate.get(), 50, swing.get(), true);
                    trapTick = 0;
                    stage = Stage.Burrow;
                }
            }
            case Burrow -> {
                burrowTick++;
                if (burrowTick >= burrowDelay.get()) {
                    FindItemResult netheriteBlock = InvUtils.findInHotbar(Items.NETHERITE_BLOCK);
                    if (!netheriteBlock.isHotbar()) return;
                    if (BlockUtils.canPlace(burrowPos))
                        BlockUtils.place(burrowPos, netheriteBlock, rotate.get(), 50, swing.get(), true);
                    burrowTick = 0;
                    stage = Stage.Piston;
                }
            }
            case Piston -> {
                pistonTick++;
                if (pistonTick >= pistonDelay.get()) {
                    FindItemResult piston = InvUtils.findInHotbar(Items.PISTON, Items.STICKY_PISTON);
                    if (!piston.isHotbar()) return;
                    float yawBack = mc.player.getYaw();
                    Rotations.rotate(90,0);
                    if (mc.player.getYaw() == 90f) {
                        if (BlockUtils.canPlace(pistonPos))
                            BlockUtils.place(pistonPos, piston, false, 50, swing.get(), true);
                    }
                    mc.player.setYaw(yawBack);
                    pistonTick = 0;
                    stage = Stage.Redstone;
                }
            }
            case Redstone -> {
                redstoneTick++;
                if (redstoneTick >= redstoneDelay.get()) {
                    FindItemResult redstone = InvUtils.findInHotbar(Items.REDSTONE_BLOCK);
                    if (!redstone.isHotbar()) return;
                    if (BlockUtils.canPlace(redstonePos))
                        BlockUtils.place(redstonePos, redstone, rotate.get(), 50, swing.get(), true);
                    redstoneTick = 0;
                    if(clearAfter.get()){
                        stage = Stage.Clear;
                    }
                    else {
                        toggle();
                    }
                }
            }
            case Clear -> {
                clearTick++;
                if(clearTick >= clearDelay.get()){
                    // WHY IS THIS SO MASSIVE :wEARYA;
                    FindItemResult picaxe = InvUtils.findInHotbar(Items.NETHERITE_PICKAXE,Items.DIAMOND_PICKAXE);
                    if(!picaxe.isHotbar()) return;
                    InvUtils.swap(picaxe.slot(),true);
                    if(Utils.state(redstonePos) == Blocks.REDSTONE_BLOCK) BlockUtils.breakBlock(redstonePos,swing.get());
                    if(Utils.state(redstonePos) == Blocks.AIR) BlockUtils.breakBlock(pistonPos,swing.get());
                    if(Utils.state(pistonPos) == Blocks.AIR) if(!trap.get()) toggle();
                    else {
                        BlockUtils.breakBlock(trapTopPos,swing.get());
                        if(Utils.state(trapTopPos) == Blocks.AIR) BlockUtils.breakBlock(trapBackPos,swing.get());
                        if(Utils.state(trapBackPos) == Blocks.AIR) toggle();
                    }
                }
            }
        }
    }
    @EventHandler
    private void onRender3D(Render3DEvent event) {
        if (!render.get()) return;
        if(burrowPos != null) event.renderer.box(burrowPos, sideColor.get(), lineColor.get(), shapeMode.get(), 0);
        if(pistonPos != null) event.renderer.box(pistonPos, sideColor.get(), lineColor.get(), shapeMode.get(), 0);
        if(redstonePos != null) event.renderer.box(redstonePos, sideColor.get(), lineColor.get(), shapeMode.get(), 0);
    }



    private enum Stage{
        Preparing,
        Trap,
        Piston,
        Burrow,
        Redstone,
        Clear
    }

}
