package random.meteor.systems.modules;

import meteordevelopment.meteorclient.systems.commands.Command;
import meteordevelopment.meteorclient.systems.commands.Commands;
import meteordevelopment.meteorclient.systems.modules.Module;
import random.meteor.systems.modules.combat.*;
import meteordevelopment.meteorclient.systems.modules.Modules;
import random.meteor.systems.modules.commands.*;
import random.meteor.systems.modules.misc.*;

import java.util.ArrayList;
import java.util.List;

public class  Manager {
    private static void addModules(){

        // Modules here
        List<Module> m = new ArrayList<>();
        m.add(new PearlPhase());
        m.add(new Prefix());
        m.add(new BlockClap());
        m.add(new AutoPvp());
        m.add(new LeftClickArmor());
        m.add(new AutoPussy());
        m.add(new AutoKys());
        m.add(new AutoCum());
        m.add(new AutoRekit());
        m.add(new PopCrash());
        m.add(new AutoReply());
        m.add(new LitematicaPrinter());
        m.add(new Excavator());
        m.add(new CustomFov());
        m.add(new AutoPos());
        m.add(new AutoChunkBan());
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
        c.add(new Stop());
        c.add(new Ping());
        c.add(new Title());
    }
    private static void addHud(){

        // hud here

    }
    public static void load(){
        addModules();
        addCommands();
        addHud();
    }
}

