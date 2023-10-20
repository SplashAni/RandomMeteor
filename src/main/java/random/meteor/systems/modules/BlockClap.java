package random.meteor.systems.modules;

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
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import random.meteor.Main;

import java.util.List;
import java.util.Objects;

import static meteordevelopment.meteorclient.utils.player.InvUtils.findInHotbar;

public class BlockClap extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgRender = settings.createGroup("Render");

    private final Setting<List<Block>> blocks = sgGeneral.add(new BlockListSetting.Builder()
            .name("blocks")
            .description("What blocks to use for surround.")
            .defaultValue(Blocks.OBSIDIAN, Blocks.CRYING_OBSIDIAN, Blocks.NETHERITE_BLOCK)
            .build()
    );
    private final Setting<Boolean> center = sgGeneral.add(new BoolSetting.Builder()
            .name("center")
            .description("Centers you on the block you are standing on before placing.")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
            .name("rotate")
            .description("Rotates on block placement")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> swing = sgGeneral.add(new BoolSetting.Builder()
            .name("swing")
            .description("Swings your hand when the block is placed.")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> render = sgRender.add(new BoolSetting.Builder()
            .name("render")
            .description("Renders the block placed to clap into.")
            .defaultValue(true)
            .build()
    );
    private final Setting<ShapeMode> shapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>()
            .name("shape-mode")
            .description("How the shapes are rendered.")
            .defaultValue(ShapeMode.Both)
            .build()
    );

    private final Setting<SettingColor> sideColor = sgRender.add(new ColorSetting.Builder()
            .name("side-color")
            .description("The side color for positions to be placed.")
            .defaultValue(new SettingColor(0, 0, 0, 0))
            .build()
    );

    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder()
            .name("line-color")
            .description("The line color for positions to be placed.")
            .defaultValue(new SettingColor(69, 69, 69))
            .build()
    );

    private BlockPos blockPos;

    public BlockClap() {
        super(Main.RM, "block-clap", "Burrow that should work on almost any server");
    }

    @Override
    public void onActivate() {
        blockPos = null;
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        FindItemResult block = findInHotbar(itemStack -> blocks.get().contains(Block.getBlockFromItem(itemStack.getItem())));
        FindItemResult pearl = InvUtils.findInHotbar(Items.ENDER_PEARL);

        if (!block.isHotbar() || !pearl.isHotbar()) {
            error("No items found, disabling module...");
            toggle();
            return;
        }


        BlockPos pos = null;
        Direction direction = null;

        for (Direction d : Direction.values()) {
            if (BlockUtils.canPlace(mc.player.getBlockPos().offset(d))) {
                pos = mc.player.getBlockPos().offset(d);
                direction = d;
            }
        }

        if (pos == null) {
            error("No positions found");
            toggle();
            return;
        }


        BlockPos calcedPos = pos;
        Direction calcedDirection = direction;

        if (BlockUtils.place(calcedPos, block, rotate.get(), 50, swing.get(), true)) {
            Rotations.rotate(getDirection(calcedDirection), 73, () -> {

                if (center.get()) PlayerUtils.centerPlayer();

                InvUtils.swap(pearl.slot(), true);

                Objects.requireNonNull(mc.interactionManager).interactItem(mc.player, pearl.getHand());

                InvUtils.swapBack();

                PlayerUtils.centerPlayer();

            });
        }


        toggle();

    }

    public int getDirection(Direction direction) { /* MOST ACCURATE BY SPLASHGOD.CC CUZ IM PRO*/
        return switch (direction) {
            case NORTH -> 180;
            case SOUTH -> 0;
            case WEST -> 90;
            case EAST -> 270;
            default -> throw new IllegalStateException("Unexpected value: " + direction);
        };
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        if (blockPos == null || !render.get()) return;
        event.renderer.box(blockPos, sideColor.get(), lineColor.get(), shapeMode.get(), 0);
    }
}
