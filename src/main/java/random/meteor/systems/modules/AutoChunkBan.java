package random.meteor.systems.modules;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import random.meteor.Main;
import random.meteor.utils.Utils;

import java.util.Arrays;

public class AutoChunkBan extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgRender = settings.createGroup("Render");


    private final Setting<Boolean> ignoreSelf = sgGeneral.add(new BoolSetting.Builder() // for testing bro
            .name("ignore-self").description("doesn't ban urself LOL").defaultValue(true).build());
    private final Setting<Boolean> ignoreFriends = sgGeneral.add(new BoolSetting.Builder().name("ignore-friends").description("doesn't ban u friends ").defaultValue(true).build());
    private final Setting<Integer> range = sgGeneral.add(new IntSetting.Builder().name("range").description("range to place").defaultValue(6).range(1, 7).sliderMax(7).build());

    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder().name("rotate").description("wheather to rotate on place").defaultValue(false).build());
    private final Setting<Boolean> swing = sgGeneral.add(new BoolSetting.Builder().name("swing").description("swings you arm on place").defaultValue(false).build());
    private final Setting<Boolean> autoMine = sgGeneral.add(new BoolSetting.Builder().name("auto-mine").description("mines the shulker after being sorted").defaultValue(true).build());
    private final Setting<Boolean> bestool = sgGeneral.add(new BoolSetting.Builder().name("best-tool").description("finds best tool to mine shulker").defaultValue(true).visible(autoMine::get).build());

    private final Setting<Boolean> render = sgRender.add(new BoolSetting.Builder().name("render").description("renderinggg").defaultValue(true).build());
    private final Setting<ShapeMode> shapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>().name("shape-mode").description("How the shapes are rendered.").defaultValue(ShapeMode.Both).visible(render::get).build());

    private final Setting<SettingColor> sideColor = sgRender.add(new ColorSetting.Builder().name("side-color").description("The side color of the rendering.").defaultValue(new SettingColor(125, 69, 245, 75)).visible(render::get).build());

    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder().name("line-color").description("The line color of the rendering.").defaultValue(new SettingColor(100, 34, 53, 255)).visible(render::get).build());

    FindItemResult shulker;
    private Stage stage;
    PlayerEntity target;
    BlockPos placePos;
    int slot;

    public AutoChunkBan() {
        super(Main.RM, "auto-chunk-ban", "");

    }

    @Override
    public void onActivate() {

        shulker = InvUtils.findInHotbar(itemStack -> Arrays.stream(Utils.SHULKER_BLOCKS).toList().contains(Block.getBlockFromItem(itemStack.getItem())));

        if (!shulker.isHotbar()) {
            error("No shulkers found...");
            toggle();
            return;
        }


        target = null;
        placePos = null;

        stage = Stage.Preparing;
        super.onActivate();
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {

        placePos = getPos();

        if (placePos == null) {
            error("No valid pos found, toggling...");
            toggle();
            return;
        }


        switch (stage) {
            case Preparing -> {
                assert mc.player != null;

                assert mc.world != null;
                for (PlayerEntity player : mc.world.getPlayers()) {
                    if (ignoreSelf.get() && player == mc.player) continue;
                    if (Friends.get().isFriend(player) && ignoreFriends.get()) continue;
                    if (player.distanceTo(mc.player) <= range.get()) {
                        target = player;
                        stage = Stage.Placing;
                        break;
                    } else if (target == null) {
                        error("No targets found toggling...");
                        stage = Stage.Toggling;
                        break;
                    }
                }
            }
            case Placing -> {

                BlockUtils.place(placePos, shulker, rotate.get(), 100, swing.get(), true);
                if (autoMine.get()) {
                    slot = mc.player.getInventory().selectedSlot;
                    stage = Stage.Mining;
                } else {
                    stage = Stage.Toggling;
                }
            }
            case Mining -> {

                FindItemResult picaxe = InvUtils.findInHotbar(itemStack -> itemStack.getItem() == Items.DIAMOND_PICKAXE || itemStack.getItem() == Items.NETHERITE_PICKAXE);

                if (bestool.get() && picaxe.isHotbar()) {
                    InvUtils.swap(picaxe.slot(), false);
                }

                BlockUtils.breakBlock(placePos, swing.get());
                assert mc.world != null;
                BlockState state = mc.world.getBlockState(placePos);

                if (state.getBlock() == Blocks.AIR) {
                    assert mc.player != null;
                    mc.player.getInventory().selectedSlot = slot;
                    info("Broke shulker, toggling...");
                    stage = Stage.Toggling;
                }
            }
            case Toggling -> toggle();
        }
    }


    public BlockPos getPos() {
        for (Direction d : Direction.values()) {
            if (d == Direction.UP || d == Direction.DOWN) continue;
            if (BlockUtils.canPlace(target.getBlockPos().offset(d))) return target.getBlockPos().offset(d);
            break;
        }
        return null;
    }

    private enum Stage {
        Preparing, Placing, Mining, Toggling
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        if (placePos == null) return;

        event.renderer.box(placePos, sideColor.get(), lineColor.get(), shapeMode.get(), 0);
    }
}