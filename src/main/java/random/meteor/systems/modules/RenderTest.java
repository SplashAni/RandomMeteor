package random.meteor.systems.modules;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.BlockPos;
import random.meteor.util.render.RenderBlock;
import random.meteor.util.render.RenderUtil;
import random.meteor.util.setting.groups.RenderSettingGroup;
import random.meteor.util.system.Category;
import random.meteor.util.system.Mod;

public class RenderTest extends Mod {
    RenderSettingGroup renderSettingGroup;
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> height = sgGeneral.add(new DoubleSetting.Builder()
        .name("height")
        .description("How tall the gradient should be.")
        .defaultValue(0.7)
        .min(0)
        .sliderMax(1)
        .build()
    );

    public RenderTest() {
        super("render-test", Category.PVP);
       // renderSettingGroup = register(RenderSettingGroup.class);
    }

    @Override
    public void onPreTick(TickEvent.Pre event) {
        //RenderUtil.addBlock(new RenderBlock(mc.player.getBlockPos(),renderSettingGroup));
        super.onPreTick(event);
    }

    @EventHandler
    private void onRender(Render3DEvent event) {

    }
}
