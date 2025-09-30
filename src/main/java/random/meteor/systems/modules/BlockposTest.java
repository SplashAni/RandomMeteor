package random.meteor.systems.modules;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.orbit.EventHandler;
import random.meteor.global.ModListSetting;
import random.meteor.util.setting.groups.RangeSettingGroup;
import random.meteor.util.system.Category;
import random.meteor.util.system.Mod;

import java.util.List;

public class BlockposTest extends Mod {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> x = sgGeneral.add(new IntSetting.Builder()
        .name("x")
        .defaultValue(1)
        .min(-50)
        .sliderMax(7)
        .build()
    );
  private final Setting<Integer> z = sgGeneral.add(new IntSetting.Builder()
        .name("z")
        .defaultValue(1)
        .min(-50)
        .sliderMax(7)
        .build()
    );
    private final Setting<List<Mod>> bobmaclat = sgGeneral.add(
        new ModListSetting.Builder<RangeSettingGroup>()
            .name("modularsss")
            .description("xd")
            .groupClass(RangeSettingGroup.class)
            .build()
    );

    public BlockposTest() {
        super("test-p1s", Category.PVP);
        register(RangeSettingGroup.class);
    }

    @Override
    public void onActivate() {
        System.out.println(bobmaclat.get());
        super.onActivate();
    }

    @EventHandler
    public void render(Render3DEvent renderer3D) {
//        RenderUtil.addBlock(renderer3D,mc.player.getBlockPos().add(x.get(),0,z.get()), RenderType.Normal, RenderMode.Both, Color.RED, Color.PINK);


    }
}
