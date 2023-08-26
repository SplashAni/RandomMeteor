package random.meteor.systems.modules;

import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import random.meteor.systems.modules.RM.*;
import random.meteor.systems.modules.commands.*;

import java.util.ArrayList;
import java.util.List;

public class  Manager {
    private static void addModules(){

        // Modules here
        List<Module> m = new ArrayList<>();
        m.add(new PearlPhase());
        m.add(new Prefix());
        m.add(new PistonPush());
        m.add(new BlockClap());
        m.add(new CustomFov());
        m.add(new AutoChunkBan());
        m.add(new TntAura());
        m.add(new TargetEsp());;
        m.add(new Blockpos());
        m.add(new AutoGold());
        m.add(new Twerk());
        m.add(new MinecartAura());
        m.add(new BurrowEsp());
        m.add(new Blocker());
        m.add(new PistonBurrow());
        m.add(new CrystalBomb());
        m.add(new GodHand());
        m.add(new InstantMend());
        m.add(new Stats());
        m.add(new AutoBed());
        m.add(new FakeKick());
        m.add(new PistonAura());

        m.forEach(module -> Modules.get().add(module));
    }
    private static void addCommands(){
        // Commands here

        Commands.add(new Betrayed());
        Commands.add(new Dupe());
        Commands.add(new Unbind());
        Commands.add(new Panic());
        Commands.add(new Center());
        Commands.add(new Crash());
        Commands.add(new Ping());
        Commands.add(new Title());
    }
    private static void addHud(){

    }
    public static void load(){
        addModules();
        addCommands();
        addHud();
    }
}

