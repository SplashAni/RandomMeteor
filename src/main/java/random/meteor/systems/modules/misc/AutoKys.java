package random.meteor.systems.modules.misc;

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
    private final SettingGroup sgGlobal = settings.getDefaultGroup();

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
    private final Setting<Boolean> autoLava = sgGlobal.add(new BoolSetting.Builder()
            .name("auto-lava")
            .description("places a lava bucket to attempt to kill you")
            .defaultValue(false)
            .build()
    );
    private final Setting<Boolean> rotate = sgGlobal.add(new BoolSetting.Builder()
            .name("rotate")
            .description("Rotates player head.")
            .defaultValue(true)
            .visible(autoLava::get)
            .build()
    );
    private final Setting<Boolean> swing = sgGlobal.add(new BoolSetting.Builder()
            .name("swing")
            .description("Swings you hand when the block is placed.")
            .defaultValue(true)
            .visible(autoLava::get)
            .build()
    );

    public AutoKys() {
        super(Main.MISC, "auto-kys", "tries to kill urself lol");
    }

    @Override
    public void onActivate() {
        sendMessage();
        if (autoLava.get()) {
            placeLava();
        }
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

    private void placeLava() {
        Item lavaBucket = Items.LAVA_BUCKET;

        FindItemResult result = InvUtils.find(lavaBucket);

        PlayerEntity player = mc.player;

        BlockPos blockPos = new BlockPos(player.getX(), player.getY() - 1, player.getZ());

        if (result.found()) {
            BlockUtils.place(blockPos, result, rotate.get(), 100, swing.get(), true);
            info("Placed a lava bucket...");
            this.toggle();
        } else {
            error("No lava bucket found toggling...");
            this.toggle();
        }
    }
}