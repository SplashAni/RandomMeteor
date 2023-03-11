package random.meteor.Modules.combat;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.util.math.BlockPos;
import random.meteor.Main;
import random.meteor.Utils.CombatUtils;

import java.util.List;

import static random.meteor.Utils.CombatUtils.openShulker;

public class AutoChunkBan extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<List<Block>> blocks = sgGeneral.add(new BlockListSetting.Builder()
            .name("blocks")
            .description("What blocks to use for surround.")
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
            .defaultValue(false)
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

            }
        }
    }
}