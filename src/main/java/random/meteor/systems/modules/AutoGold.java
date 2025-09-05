package random.meteor.systems.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import random.meteor.systems.Mod;
import random.meteor.utils.Utils;

import static meteordevelopment.meteorclient.utils.player.InvUtils.findInHotbar;

public class AutoGold extends Mod {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> range = sgGeneral.add(new IntSetting.Builder()
        .name("range")
        .description("range to check for piglins")
        .defaultValue(56)
        .min(4)
        .sliderMax(60)
        .build());

    private final Setting<Boolean> onlyNether = sgGeneral.add(new BoolSetting.Builder()
        .name("only-nether")
        .description("for ohio")
        .defaultValue(false)
        .build());
    private final Setting<Integer> switchDelay = sgGeneral.add(new IntSetting.Builder()
        .name("switch-delay")
        .description("delay")
        .defaultValue(1)
        .range(1, 5)
        .sliderMax(5)
        .build()
    );

    public int tick = 0;

    public AutoGold() {
        super("auto-gold", "Switches to gold armor or items when near piglins.");
    }


    @EventHandler
    private void onTick(TickEvent.Pre event) {
        tick++;
        for (Entity entity : mc.world.getEntities()) {
            if (entity.getType() == EntityType.PIGLIN && entity.getPos().isInRange(mc.player.getPos(), range.get())) {
                if (!true&& tick >= switchDelay.get()) {
                    tick = 0;
                    if (!findInHotbar(Items.GOLD_INGOT, Items.GOLD_BLOCK).isHotbar() && netherCheck()) {
                        switchToGold();
                    } else {
                        switchToGold();
                    }
                }
            }
        }
    }

    private void switchToGold() {
        int goldSlot = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() == Items.GOLD_INGOT || stack.getItem() == Items.GOLD_BLOCK) {
                goldSlot = i;
                break;
            }
        }
        if (goldSlot != -1) {
          //  mc.player.getInventory().getSelectedSlot() = goldSlot;
        }
    }

    private boolean netherCheck() {
        return !onlyNether.get() || mc.world.getDimension().respawnAnchorWorks();
    }
}
