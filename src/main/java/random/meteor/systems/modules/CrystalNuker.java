package random.meteor.systems.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import random.meteor.Main;
import random.meteor.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class CrystalNuker extends Module {
    public CrystalNuker() {
        super(Main.RM,"crystal-nuker","places crystals in range bro");
    }
    List list = new ArrayList<>();
    @EventHandler
    public void onTick(TickEvent.Pre event){
    }
    public void add(){
        List<BlockPos> poses = Utils.placeableBlocks(3);

        for(BlockPos p : poses){
            if(!(Utils.state(p) == Blocks.OBSIDIAN) || !(Utils.state(p) == Blocks.BEDROCK)) return;

        }

    }
}
