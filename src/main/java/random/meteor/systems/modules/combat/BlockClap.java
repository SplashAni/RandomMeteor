package random.meteor.systems.modules.combat;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
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
import net.minecraft.util.math.Direction;
import random.meteor.Main;
import random.meteor.systems.modules.utils.CombatUtils;

import java.util.List;

import static meteordevelopment.meteorclient.utils.player.InvUtils.findInHotbar;

public class BlockClap extends Module {
    private static BlockPos blockPos;
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
    private final Setting<Boolean> jump = sgGeneral.add(new BoolSetting.Builder()
            .name("jump")
            .description("Jumps then places block to trap player underneath.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> toggleVelocity = sgGeneral.add(new BoolSetting.Builder()
            .name("toggle-velocity")
            .description("Toggles velocity to keep the player clapped.")
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
            .description("Swings you hand when the block is placed.")
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
            .defaultValue(new SettingColor(0, 0, 0,0))
            .build()
    );

    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder()
            .name("line-color")
            .description("The line color for positions to be placed.")
            .defaultValue(new SettingColor(69, 69, 69))
            .build()
    );


    public BlockClap() {
        super(Main.COMBAT, "block-clap", "Burrow that should work on almost server");
    }


    @Override
    public void onActivate() {

        if (!hasItems()) {
            error("No pearls found, toggling...");
            toggle();
            return;
        }

        if (center.get()) {
            PlayerUtils.centerPlayer();
        }
        if (jump.get() && mc.player.isOnGround()) {
            mc.player.jump();

        }
        clapAttempt();
        super.onActivate();
    }

    private void clapAttempt() {

        assert mc.player != null;
        BlockPos playerPos = mc.player.getBlockPos();

        blockPos = playerPos.north();
        BlockUtils.place(blockPos, getInvBlock(), rotate.get(), 100, swing.get(), true);
        throwPearl();

        this.toggle();
    }
    private void throwPearl(){ // todo: make rotate back to original pos soon tm™

        mc.player.setYaw(-182);
        mc.player.setPitch(72);
        CombatUtils.throwPearl(72);
    }
    private FindItemResult getInvBlock() {
        return findInHotbar(itemStack -> blocks.get().contains(Block.getBlockFromItem(itemStack.getItem())));
    }
    private boolean hasItems() {
        List<Item> blockItems = blocks.get().stream()
                .map(Item::fromBlock).toList();
        for (Item block : blockItems) {
            assert mc.player != null;
            if (mc.player.getInventory().contains(new ItemStack(block)) && mc.player.getInventory().contains(new ItemStack(Items.ENDER_PEARL))) {
                return false;
            }
        }
        return true;
    }

    private int getYaw(Direction direction) {
        if (direction == null) {
            assert mc.player != null;
            return (int) mc.player.getYaw();
        }
        return switch (direction) {
            case NORTH -> 180;
            case SOUTH -> 0;
            case WEST -> 90;
            case EAST -> -90;
            default -> throw new IllegalStateException("Unexpected value: " + direction);
        };
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        if (render.get()) {
            event.renderer.box(blockPos, sideColor.get(), lineColor.get(), shapeMode.get(),0);
        }
    }
}
