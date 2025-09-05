package random.meteor.systems.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import random.meteor.util.setting.groups.PlaceSettingGroup;
import random.meteor.util.setting.groups.RangeSettingGroup;
import random.meteor.util.setting.groups.SwapSettingGroup;
import random.meteor.util.setting.groups.SwingSettingGroup;
import random.meteor.util.system.Category;
import random.meteor.util.system.Mod;
import random.meteor.util.world.BlockUtil;

public class FeetTrap extends Mod {


    PlaceSettingGroup placeSettingGroup = new PlaceSettingGroup(this);
    RangeSettingGroup rangeSettingGroup = new RangeSettingGroup(this);
    SwapSettingGroup swapSettingGroup = new SwapSettingGroup(this);
    SwingSettingGroup swingSettingGroup = new SwingSettingGroup(this);

    public FeetTrap() {
        super("feet-trap", Category.PVP);
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {

        BlockPos offset = mc.player.getBlockPos().offset(Direction.DOWN, 1);
        BlockUtil.place(offset, placeSettingGroup, rangeSettingGroup, swapSettingGroup, swingSettingGroup);
    }

}
