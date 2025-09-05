package random.meteor.systems.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.orbit.EventHandler;
import random.meteor.util.setting.groups.DelaySettingGroup;
import random.meteor.util.setting.groups.PlaceSettingGroup;
import random.meteor.util.setting.groups.RangeSettingGroup;
import random.meteor.util.setting.groups.SwingSettingGroup;
import random.meteor.util.system.Category;
import random.meteor.util.system.Mod;

public class FeetTrap extends Mod {


    PlaceSettingGroup placeSettingGroup = new PlaceSettingGroup(this);
    DelaySettingGroup delaySettingGroup = new DelaySettingGroup(this);
    RangeSettingGroup rangeSettingGroup = new RangeSettingGroup(this);
    SwingSettingGroup swingSettingGroup = new SwingSettingGroup(this);

    public FeetTrap() {
        super("feet-trap", Category.PVP);

    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        System.out.println("ticky");
    }

}
