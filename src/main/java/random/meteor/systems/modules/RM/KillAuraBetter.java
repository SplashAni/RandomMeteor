package random.meteor.systems.modules.RM;

import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.combat.KillAura;
import meteordevelopment.meteorclient.systems.modules.combat.SelfWeb;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import random.meteor.Main;

import java.util.List;

public class KillAuraBetter extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgRotate = settings.createGroup("Rotate");

    //GENREL

    private final Setting<List<Item>> weapon = sgGeneral.add(new ItemListSetting.Builder()
            .name("weapon")
            .description("")
            .defaultValue(Items.NETHERITE_SWORD, Items.NETHERITE_AXE,Items.NETHERITE_PICKAXE)
            .build()
    );
    private final Setting<Swap> swapMehtod = sgGeneral.add(new EnumSetting.Builder<Swap>()
            .name("swap-method")
            .defaultValue(Swap.Weapon)
            .build()
    );
    // ROTATE
    private final Setting<Rotation>  rotate = sgGeneral.add(new EnumSetting.Builder<Rotation>()
            .name("weapon")
            .description("Only attacks an entity when a specified weapon is in your hand.")
            .defaultValue(Rotation.Packet)
            .build()
    );
    private final Setting<Integer> attackWait = sgGeneral.add(new IntSetting.Builder()
            .name("attack-wait")
            .description("How long to wait after rotating before attacking")
            .defaultValue(25)
            .min(25)
            .sliderRange(1, 50)
            .visible(() -> rotate.get() == Rotation.Delayed)
            .build()
    );
    private final Setting<Boolean> smoothRotate = sgGeneral.add(new BoolSetting.Builder()
            .name("smooth-rotate")
            .description("Harder to bypass")
            .defaultValue(false)
            .build()
    );
    public KillAuraBetter() {
        super(Main.RM,"kill-aura-better","");
    }

    @Override
    public void onActivate() {
        super.onActivate();
    }

    private enum Stage{
        Switch,
        Attack
    }
    public enum Rotation{
        Packet,
        OnLook,
        Rotate,
        Delayed // rotates before hitting then rotates back and hits for troling lag and shit OK
    }
    public enum Swap {
        Weapon,
        None,
    }
}
