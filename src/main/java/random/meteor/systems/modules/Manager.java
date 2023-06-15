package random.meteor.systems.modules;

import meteordevelopment.meteorclient.systems.commands.Commands;
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
        m.add(new BlockClap());
        m.add(new CustomFov());
        m.add(new AutoChunkBan());
        m.add(new TntAura());
        m.add(new KillAuraBetter());
        m.add(new AutoGold());
        m.add(new Twerk());
        m.add(new BurrowEsp());
        m.forEach(module -> Modules.get().add(module));
    }
    private static void addCommands(){
        // Commands here
        Commands c = Commands.get();

        c.add(new Betrayed());
        c.add(new Dupe());
        c.add(new Unbind());
        c.add(new Panic());
        c.add(new Center());
        c.add(new Crash());
        c.add(new Ping());
        c.add(new Title());
    }
    private static void addHud(){

    }
    public static void load(){
        addModules();
        addCommands();
        addHud();
    }
}

