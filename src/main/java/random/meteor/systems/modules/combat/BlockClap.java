package random.meteor.systems.modules.combat;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import random.meteor.Main;
import random.meteor.systems.modules.utils.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static meteordevelopment.meteorclient.utils.player.InvUtils.findInHotbar;
import static meteordevelopment.meteorclient.utils.world.BlockUtils.canPlace;
import static random.meteor.systems.modules.utils.Utils.getBestDirection;

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
            .description("Rotates player head.")
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
    private Stage stage;
    private float prevYaw;
    private float prevPitch;
    Direction direction = getBestDirection();

    public BlockClap() {
        super(Main.COMBAT, "block-clap", "Burrow that should work on almost any server");
    }

    @Override
    public void onActivate() {
        blockPos = null;
        stage = Stage.Preparing;
    }

    @EventHandler
    public void onTick(TickEvent.Post event){
        if (!hasItems()) {
            error("No items found, disabling module...");
            toggle();
            return;
        }

        switch (stage) {
            case Preparing -> {
                prevPitch = mc.player.getPitch();
                prevYaw = mc.player.getYaw();
                blockPos = mc.player.getBlockPos();

                if (center.get()) {
                    PlayerUtils.centerPlayer();
                }

                stage = Stage.Placing;
            }
            case Placing -> {
                BlockPos playerPos = mc.player.getBlockPos();
                blockPos = playerPos.offset(getBestDirection());
                BlockUtils.place(blockPos, getInvBlock(), rotate.get(), 100, swing.get(), true);
                stage = Stage.Pearling;
            }
            case Pearling -> {
                assert mc.player != null;
                mc.player.setYaw(Utils.getYawFromDirection(direction));
                mc.player.setPitch(72);
                Utils.throwPearl(72);
                stage = Stage.Toggle;
            }
            case Toggle -> {
                assert mc.player != null;
                mc.player.setYaw(prevYaw);
                mc.player.setPitch(prevPitch);
                toggle();
            }
        }
    }

    private FindItemResult getInvBlock() {
        return findInHotbar(itemStack -> blocks.get().contains(Block.getBlockFromItem(itemStack.getItem())));
    }

    private boolean hasItems() {
        List<Item> blockItems = blocks.get().stream()
                .map(Item::fromBlock)
                .toList();

        for (Item block : blockItems) {
            if (mc.player.getInventory().contains(new ItemStack(block)) && mc.player.getInventory().contains(new ItemStack(Items.ENDER_PEARL))) {
                return true;
            }
        }

        return false;
    }

    private enum Stage {
        Preparing,
        Placing,
        Pearling,
        Toggle
    }
}
