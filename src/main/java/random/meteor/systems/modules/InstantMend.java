package random.meteor.systems.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import random.meteor.Main;
import random.meteor.utils.Utils;

public class InstantMend extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> speed = sgGeneral.add(new IntSetting.Builder() /*90 is like 20bottles per sec
    which is very fast on 300ms , can go higher but u get kciekd :thujbmsupp: , todo try this with surround
    */
        .name("exp-packets")
        .defaultValue(90)
        .min(1)
        .sliderRange(1, 200)
        .build()
    );
    public InstantMend() {
        super(Main.RM,"instant-mend","fastest exploit bro");
    }
    @EventHandler
    public void onTick(TickEvent.Pre event) {
        FindItemResult exp = InvUtils.find(Items.EXPERIENCE_BOTTLE);
        for (int i = 0; i <= speed.get(); i++) {
            Utils.move(exp.slot(),mc.player.getInventory().selectedSlot);
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            Utils.move(exp.slot(),mc.player.getInventory().selectedSlot);
        }
    }
}
