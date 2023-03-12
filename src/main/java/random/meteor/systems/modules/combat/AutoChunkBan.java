package random.meteor.systems.modules.combat;

import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import random.meteor.Main;
import random.meteor.systems.modules.utils.CombatUtils;

import java.util.List;

public class AutoChunkBan extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

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
            .defaultValue(15)
            .range(5, 30)
            .sliderMax(30)
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
            .defaultValue(false)
            .build()
    );
    private final Setting<Integer> startDelay = sgGeneral.add(new IntSetting.Builder()
            .name("start-delay")
            .description("delay before sorting")
            .defaultValue(10)
            .range(1,10)
            .sliderMax(20)
            .visible(autoMine::get)
            .build()
    );
    private final Setting<Boolean> mineDebug = sgGeneral.add(new BoolSetting.Builder()
            .name("mine-debug")
            .description("send timer to chat")
            .defaultValue(false)
            .visible(autoMine::get)
            .build()
    );
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
                BlockPos blockPos = targetBlockPos.north();
                BlockUtils.place(blockPos, shulkerResult(), rotate.get(), 100, swing.get(), true);
                autoMine(blockPos);
            }
        }
    }
    private void autoMine(BlockPos pos) {
        Direction direction = mc.player.getHorizontalFacing().getOpposite();

        mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, direction));
        for (int i = 1; i <= startDelay.get() + 1000; i++) {
            if (mineDebug.get() && i % 100 == 0) {
                info("Starting in: " + i);
            }
            if (i == startDelay.get() + 1000) {
                mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, direction));
            }
        }
    }
}