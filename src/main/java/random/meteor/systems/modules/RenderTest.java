package random.meteor.systems.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import random.meteor.util.render.RenderBlock;
import random.meteor.util.render.RenderUtil;
import random.meteor.util.setting.groups.RenderSettingGroup;
import random.meteor.util.system.Category;
import random.meteor.util.system.Mod;

public class RenderTest extends Mod {
    RenderSettingGroup renderSettingGroup;


    public RenderTest() {
        super("render-test", Category.PVP);
        renderSettingGroup = register(RenderSettingGroup.class);
    }

    @Override
    public void onPreTick(TickEvent.Pre event) {
        RenderUtil.addBlock(new RenderBlock(mc.player.getBlockPos().up(3), renderSettingGroup));

        super.onPreTick(event);
    }
}
