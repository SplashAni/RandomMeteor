package random.meteor.systems.modules;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import random.meteor.Main;
import random.meteor.utils.Utils;

import static meteordevelopment.meteorclient.utils.entity.TargetUtils.getPlayerTarget;
import static meteordevelopment.meteorclient.utils.entity.TargetUtils.isBadTarget;
import static meteordevelopment.meteorclient.utils.world.BlockUtils.canPlace;

public class CrystalBomb extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgRender = settings.createGroup("Render");

    private final Setting<Double> targetRange = sgGeneral.add(new DoubleSetting.Builder()
        .name("target-range")
        .description("The radius in which players get targeted.")
        .defaultValue(5.5)
        .min(0)
        .sliderMax(7)
        .build()
    );
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
    private final Setting<Integer> crystalDelay = sgGeneral.add(new IntSetting.Builder()
        .name("crystal-delay")
        .defaultValue(1)
        .min(1)
        .sliderRange(1, 50)
        .build()
    );

    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
        .name("rotate")
        .description("Automatically rotates you towards the city block.")
        .defaultValue(true)
        .build());



        // RENDER

    private final Setting<Boolean> swingHand = sgRender.add(new BoolSetting.Builder()
        .name("swing-hand")
        .description("Whether to render your hand swinging.")
        .defaultValue(false)
        .build());

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

    PlayerEntity target;
    BlockPos obsidianPos, pistonPos, redstonePos,protectPos;
    FindItemResult obsidian, piston, redstone,crystal;
    int redstoneTick,pistonTick,crystalTick;
    boolean canInstant;
    float progress;

    Stage stage;

    public CrystalBomb() {
        super(Main.RM,"crystal-bomb","pushes crystals in head with piston,impossible to bypezz");
    }

    @Override
    public void onActivate() {
        target = null;

        obsidianPos = null;
        pistonPos = null;
        protectPos = null;
        redstonePos = null;

        obsidian = null;
        piston = null;
        redstone = null;
        crystal = null;

        canInstant = false;

        crystalTick = 0;
        redstoneTick = 0;
        pistonTick = 0;
        progress = 0f;

        stage = Stage.Preparing;

        super.onActivate();
    }
    @EventHandler
    public void onTick(TickEvent.Pre event){
        target = getPlayerTarget(targetRange.get(), SortPriority.LowestDistance);
        if (isBadTarget(target, targetRange.get())) return;

        redstone = InvUtils.findInHotbar(Items.REDSTONE_BLOCK);
        piston = InvUtils.findInHotbar(Items.PISTON,Items.STICKY_PISTON);
        obsidian = InvUtils.findInHotbar(Items.OBSIDIAN);
        crystal = InvUtils.findInHotbar(Items.END_CRYSTAL);

        switch(stage){
            case Preparing -> {
                BlockPos pos = target.getBlockPos();
                obsidianPos = pos.up(2);
                pistonPos = pos.up(4);
                redstonePos = pos.up(5);

                if(canPlace(obsidianPos)) BlockUtils.place(obsidianPos,obsidian,rotate.get(),50,true, swingHand.get());
                if(Utils.state(obsidianPos) == Blocks.OBSIDIAN) stage = Stage.Piston;

            }
            case Piston -> {
                pistonTick++;
                if(pistonTick >= pistonDelay.get()) {
                    if (canPlace(pistonPos))
                        BlockUtils.place(pistonPos, piston, rotate.get(), 50, true, swingHand.get());
                    if (!(mc.world.getBlockState(pistonPos).getBlock() == Blocks.PISTON)) return;
                    pistonTick = 0;
                    stage = Stage.Crystal;
                }
            }
            case Crystal -> {
                crystalTick++;
                if (crystalTick >= crystalDelay.get()) {
                    if (BlockUtils.canBreak(obsidianPos.up())) stage = Stage.Bypass;
                    InvUtils.swap(crystal.slot(), true);
                    mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(new Vec3d(obsidianPos.getX() + 0.5, obsidianPos.getY() + 0.5, obsidianPos.getZ() + 0.5), Direction.UP, obsidianPos, true));
                    InvUtils.swapBack();
                    crystalTick = 0;
                    stage = Stage.Redstone;
                }
            }
            case Redstone -> {
                redstoneTick++;
                if (redstoneTick >= redstoneDelay.get()) {
                    if (canPlace(redstonePos(redstonePos)))
                        BlockUtils.place(redstonePos, redstone, rotate.get(), 50, true);
                    if (!(mc.world.getBlockState(redstonePos).getBlock() == Blocks.REDSTONE_BLOCK)) return;

                    if (Utils.crystal() != null) mc.interactionManager.attackEntity(mc.player, Utils.crystal());
                    if (swingHand.get()) mc.player.swingHand(Hand.MAIN_HAND);

                    redstoneTick = 0;
                    stage = Stage.Preparing;
                }
            }
            case Bypass -> {
                if(!canInstant){
                    FindItemResult pic = InvUtils.findInHotbar(Items.NETHERITE_PICKAXE);
                    progress += BlockUtils.getBreakDelta(pic.slot(), mc.world.getBlockState(obsidianPos.up()));
                    if (progress >= 1.0f){
                        canInstant = true;
                    }  else {
                        mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, obsidianPos.up(), Direction.UP));
                    }
                    stage = Stage.Crystal;
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + stage);
        }
    }
    private enum Stage{
        Preparing,
        Piston,
        Crystal,
        Redstone,
        Bypass
    }
    private BlockPos redstonePos(BlockPos targetPos) { // only block that can be bypazzed?
        if(canPlace(redstonePos)) return redstonePos;
        else if (canPlace(redstonePos.add(1,0,0))) return redstonePos.add(1,0,0);
        else if (canPlace(redstonePos.add(-1,0,0))) return redstonePos.add(-1,0,0);
        else if (canPlace(redstonePos.add(0,0,1))) return redstonePos.add(0,0,1);
        else if (canPlace(redstonePos.add(0,0,-1))) return redstonePos.add(0,0,-1);
        return redstonePos;
    }

        @EventHandler
    private void onRender3D(Render3DEvent event) {
        if (!render.get()) return;
        if(obsidianPos != null) event.renderer.box(obsidianPos, sideColor.get(), lineColor.get(), shapeMode.get(), 0);
        if(pistonPos != null) event.renderer.box(pistonPos, sideColor.get(), lineColor.get(), shapeMode.get(), 0);
        if(redstonePos != null) event.renderer.box(redstonePos, sideColor.get(), lineColor.get(), shapeMode.get(), 0);
    }
}
