package random.meteor.systems.modules.RM;

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
import random.meteor.systems.modules.utils.Utils;

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
    public void onTick(TickEvent.Pre event){
        FindItemResult exp = InvUtils.findInHotbar(Items.EXPERIENCE_BOTTLE);

        int oldslot = -1;
        if(exp.isHotbar()) {
            InvUtils.swap(exp.slot(),true);
        }else {
            oldslot = exp.slot();
            Utils.move(exp.slot(),mc.player.getInventory().selectedSlot);
        }


        for(int i = 0 ; i <= speed.get();i++){
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            if(oldslot != -1){
                Utils.move(mc.player.getInventory().selectedSlot,oldslot);
                oldslot = -1;
            }


        }
    }
}
