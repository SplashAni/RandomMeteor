package random.meteor.systems.modules.combat;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import random.meteor.Main;
import random.meteor.systems.modules.utils.CombatUtils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AutoChunkBan extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgRender = settings.createGroup("Render");

    private final Setting<List<Block>> blocks = sgGeneral.add(new BlockListSetting.Builder()
            .name("blocks")
            .description("incase i missed smtj")
            .defaultValue(CombatUtils.SHULKER_BLOCKS)
            .build()
    );

    private final Setting<Boolean> ignoreSelf = sgGeneral.add(new BoolSetting.Builder() // for testing bro
            .name("ignore-self")
            .description("doesn't ban urself LOL")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> ignoreFriends = sgGeneral.add(new BoolSetting.Builder()
            .name("ignore-friends")
            .description("doesn't ban u friends ")
            .defaultValue(true)
            .build()
    );
    private final Setting<Integer> range = sgGeneral.add(new IntSetting.Builder()
            .name("range")
            .description("range to place")
            .defaultValue(6)
            .range(1,7)
            .sliderMax(7)
            .build()
    );

    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
            .name("rotate")
            .description("wheather to rotate on place")
            .defaultValue(false)
            .build()
    );
    private final Setting<Boolean> swing = sgGeneral.add(new BoolSetting.Builder()
            .name("swing")
            .description("swings you arm on place")
            .defaultValue(false)
            .build()
    );
    private final Setting<Boolean> autoMine = sgGeneral.add(new BoolSetting.Builder()
            .name("auto-mine")
            .description("mines the shulker after being sorted")
            .defaultValue(true)
            .build()
    );
    private final Setting<Integer> mineSpeed = sgGeneral.add(new IntSetting.Builder()
            .name("mine-speed")
            .description("delay before sorting")
            .defaultValue(3)
            .range(1, 10)
            .sliderMax(10)
            .visible(autoMine::get)
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


    private BlockPos blockPos;

    public AutoChunkBan() {
        super(Main.COMBAT, "auto-chunk-ban", "");

    }

    @Override
    public void onActivate() {
        start();
        super.onActivate();
    }

    private FindItemResult shulkerResult() {
        return InvUtils.findInHotbar(itemStack -> blocks.get().contains(Block.getBlockFromItem(itemStack.getItem())));
    }

    private void start() {
        if (!shulkerResult().found()) error("No shulkers found...");
        this.toggle();
        for (PlayerEntity target : mc.world.getPlayers()) {
            if (ignoreSelf.get() && target == mc.player) continue;
            if (Friends.get().isFriend(target) && ignoreFriends.get()) continue;

            double distance = target.distanceTo(mc.player);
            if (distance <= range.get()) {

                BlockPos targetBlockPos = target.getBlockPos();
                blockPos = targetBlockPos.north();
                BlockUtils.place(blockPos, shulkerResult(), rotate.get(), 100, swing.get(), true);
                autoMine(blockPos);
            }
        }
    }

    private void autoMine(BlockPos pos) {
        mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, Direction.NORTH));

        new Timer().schedule(new TimerTask() { // fuck integers dealys
            @Override
            public void run() {
                mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, Direction.NORTH));
                info("Successfully mined shulker now run...");
            }
        }, mineSpeed.get() * 1000);
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        if(render.get()) {
            event.renderer.box(blockPos, sideColor.get(), lineColor.get(), shapeMode.get(), 0);
        }
    }
}