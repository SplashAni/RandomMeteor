package random.meteor.systems.modules.RM.PistonAura;

import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import net.minecraft.item.Items;
import random.meteor.Main;
import random.meteor.systems.modules.utils.PistonInfo;

public class PistonAura extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Integer> range = sgGeneral.add(new IntSetting.Builder()
        .name("target-range")
        .description("Range to find target.")
        .defaultValue(12)
        .range(1, 12)
        .sliderMax(12)
        .build()
    );
    private final Setting<Boolean> debug = sgGeneral.add(new BoolSetting.Builder()
        .name("debug")
        .defaultValue(true)
        .build()
    );

    public PistonAura() {
        super(Main.RM, "piston-aura", "");
    }



    @Override
    public void onActivate() {
        PistonPosition pistonPosition = new PistonPosition(mc.player.getBlockPos(),true);

        PistonPosition.Position p = pistonPosition.calculated();

        if(p == null){
            toggle();
            return;
        }
        PistonInfo pi = p.pi();


        if (p.createBase() && p.base() != null) {
            FindItemResult ob = InvUtils.findInHotbar(Items.OBSIDIAN);
            BlockUtils.place(p.base(), ob, true, 100, true);
            ChatUtils.sendPlayerMsg(p.base().toShortString());

            if(pi == null) return;
            FindItemResult o= InvUtils.findInHotbar(Items.PISTON);


            BlockUtils.place(p.pi().pos().up(),o, true, 100, true);
            ChatUtils.sendPlayerMsg(p.base().up().toShortString() + " piston");
        }


        assert mc.player != null;

        toggle();
        super.onActivate();
    }

}
