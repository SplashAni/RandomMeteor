package random.meteor.Modules.combat;

import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.movement.Velocity;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import random.meteor.Main;
import random.meteor.Utils.CombatUtils;

import java.util.List;

public class BlockClap extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
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
    private final Setting<Boolean> trap = sgGeneral.add(new BoolSetting.Builder()
            .name("trap")
            .description("Places obsidian on top of the player.")
            .defaultValue(true)
            .build()
    );
    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
            .name("delay")
            .description("How many tics to wait.")
            .defaultValue(1)
            .min(1)
            .max(10)
            .build()
    );
    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
            .name("rotate")
            .description("Rotates player head.")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> toggleVelocity = sgGeneral.add(new BoolSetting.Builder()
            .name("toggle-velocity")
            .description("Toggles velocity to keep the player clapped.")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> swing = sgGeneral.add(new BoolSetting.Builder()
            .name("swing")
            .description("Render your hand swinging when placing surround blocks.")
            .defaultValue(true)
            .build()
    );

    public BlockClap() {
        super(Main.COMBAT, "Block-Clap", "Burrow that should work on almost server");
    }


    @Override
    public void onActivate() {
        toggleVelocity();
        if (center.get()) {
            PlayerUtils.centerPlayer();
        }
        clapAttempt();
        super.onActivate();
    }

    private void toggleVelocity() {
        Velocity v = new Velocity();
        if (toggleVelocity.get() && v.isActive()) {
            v.toggle();
            info("Toggled velocity...");
        }
    }

    private void clapAttempt() {
        BlockPos playerPos = mc.player.getBlockPos();
        BlockPos blockPos = playerPos.north();
        BlockUtils.place(blockPos, getInvBlock(), rotate.get(), 100, swing.get(), true);
        throwPearl();
        this.toggle();
    }
    private void throwPearl(){
        mc.player.setYaw(-182);
        mc.player.setPitch(72);
        CombatUtils.throwPearl(72);
    }
    private FindItemResult getInvBlock() {
        return InvUtils.findInHotbar(itemStack -> blocks.get().contains(Block.getBlockFromItem(itemStack.getItem())));
    }
}