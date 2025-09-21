package random.meteor.systems.modules;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.orbit.EventHandler;
import random.meteor.util.render.RenderMode;
import random.meteor.util.render.RenderType;
import random.meteor.util.render.RenderUtil;
import random.meteor.util.system.Category;
import random.meteor.util.system.Mod;

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
    public BlockposTest() {
        super("test-pos", Category.PVP);
    }

    @EventHandler
    public void render(Render3DEvent renderer3D) {
//        RenderUtil.addBlock(renderer3D,mc.player.getBlockPos().add(x.get(),0,z.get()), RenderType.Normal, RenderMode.Both, Color.RED, Color.PINK);


    }
}
