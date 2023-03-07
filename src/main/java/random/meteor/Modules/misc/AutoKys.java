package random.meteor.Modules.misc;

import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.*;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import random.meteor.Main;

public class AutoKys extends Module {
    private final SettingGroup messageTypes = settings.getDefaultGroup();
    private final SettingGroup globalMethods = settings.getDefaultGroup();

    private final Setting<Boolean> kill = messageTypes.add(new BoolSetting.Builder()
            .name("/kill")
            .description("uses /kill to attempt to kill, works on certain servers")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> suicide = messageTypes.add(new BoolSetting.Builder()
            .name("/suicide")
            .description("uses /suicide to attempt to kill, works on certain servers")
            .defaultValue(false)
            .build()
    );
    private final Setting<Boolean> autoLava = globalMethods.add(new BoolSetting.Builder()
            .name("auto-lava")
            .description("places a lava bucket to attempt to kill you")
            .defaultValue(false)
            .build()
    );

    public AutoKys() {
        super(Main.MISC, "auto-kys", "tries to kill urself lol");
    }

    @Override
    public void onActivate() {
        sendMessage();
        autoLava();
        super.onActivate();
    }


    private void sendMessage() {
        if (kill.get() && !suicide.get()) {
            ChatUtils.sendPlayerMsg("/kill");
            toggle();
        }
        if (suicide.get() && !kill.get()) {
            ChatUtils.sendPlayerMsg("/suicide");
            toggle();
        }
    }

    private void autoLava() {
        Item lavaBucket = Items.LAVA_BUCKET;
        FindItemResult result = InvUtils.find(lavaBucket);

        PlayerEntity player = mc.player;

        BlockPos blockPos = new BlockPos(player.getX(), player.getY() - 1, player.getZ());

        if (autoLava.get()) {
            if (result.found()) {
                Rotations.rotate(0, 90);
                BlockUtils.place(blockPos, result, 90);
            } else {
                error("Lava bucket not found, toggling...");
            }
        }
    }
}
