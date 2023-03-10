package random.meteor.Modules.combat;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import random.meteor.Main;

public class AutoNuke extends Module {
    FindItemResult tnt = InvUtils.findInHotbar(Items.TNT);
    FindItemResult flint = InvUtils.findInHotbar(Items.FLINT_AND_STEEL);


    public AutoNuke() {
        super(Main.COMBAT, "auto-nuke", "places tnt ontop of players heads LOl");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if(!tnt.found() && !flint.found()){
            toggle();
        }
        else {
            // afganistan goes here
        }
    }
}
