package random.meteor.systems.modules;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import random.meteor.Main;
import random.meteor.utils.Utils;

import static random.meteor.utils.Utils.getCrystal;

public class PistonAura extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

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
            .build()
    );

    private final Setting<Boolean> swing = sgGeneral.add(new BoolSetting.Builder()
            .name("swing-hand")
            .description("Whether to render your hand swinging.")
            .defaultValue(false)
            .build()
    );

    public PistonAura() {
        super(Main.RM, "piston-aura", "iced out");
    }

    CalculatedPos calculated;
    
    public Stage stage;

    @EventHandler
    public void onTick(TickEvent.Pre event) {


        FindItemResult piston = InvUtils.findInHotbar(Items.PISTON, Items.STICKY_PISTON);
        FindItemResult redstoneBlock = InvUtils.findInHotbar(Items.REDSTONE_BLOCK);
        FindItemResult crystal = InvUtils.findInHotbar(Items.END_CRYSTAL);

        if (!piston.isHotbar() || !redstoneBlock.isHotbar() || !crystal.isHotbar()) return;

        doCalcuate();

        if (stage == null) return;

        switch (stage) {

            case Piston -> {
                BlockUtils.place(calculated.pistonPos, piston, true, 50);
                stage = Stage.Crystal;
            }
            case Crystal -> {
                InvUtils.swap(crystal.slot(), true);
                mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(Vec3d.of(calculated.basePos), Direction.UP, calculated.basePos, true));
                InvUtils.swapBack();
                stage = Stage.Redstone;
            }
            case Redstone -> {
                BlockUtils.place(calculated.redstonePos, redstoneBlock, true, 50);
                if (getCrystal(calculated.basePos) != null) {
                    mc.interactionManager.attackEntity(mc.player, getCrystal(calculated.basePos));
                }
            }
        }

    }


    public void doCalcuate() {
        for (Direction direction : Direction.values()) {
            if (direction == Direction.UP || direction == Direction.DOWN) continue;

            BlockPos offset = mc.player.getBlockPos().offset(direction);

            if (Utils.state(offset) != Blocks.OBSIDIAN) {
                continue;
            }


            BlockPos pistonPos = offset.offset(direction).up();

            if (!BlockUtils.canPlace(pistonPos)) continue;

            BlockPos redstonePos = null;

            for (Direction d : Direction.values()) {

                if (!(d == Direction.DOWN || d == direction)) {
                    continue;
                }

                if (BlockUtils.canPlace(pistonPos.offset(d))) {
                    redstonePos = pistonPos.offset(d);
                    break;
                }
            }

            if (redstonePos == null) continue;

            calculated = new CalculatedPos(pistonPos, offset, redstonePos);

            stage = Stage.Piston;
            break;
        }
    }


    public enum Stage {
        Piston,
        Crystal,
        Redstone
    }

    public record CalculatedPos(BlockPos pistonPos, BlockPos basePos, BlockPos redstonePos) {
        public boolean isValid() {
            return pistonPos != null && basePos != null && redstonePos != null;
        }

    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        if (calculated == null) return;
        if (!calculated.isValid()) return;
        event.renderer.box(calculated.basePos, Color.BLUE.a(50), Color.BLUE.toSetting(), ShapeMode.Sides, 0);
        event.renderer.box(calculated.pistonPos, Color.YELLOW.a(50), Color.BLUE.toSetting(), ShapeMode.Sides, 0);
        event.renderer.box(calculated.redstonePos, Color.RED.a(50), Color.BLUE.toSetting(), ShapeMode.Sides, 0);

    }
}