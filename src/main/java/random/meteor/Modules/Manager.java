package random.meteor.Modules;

import meteordevelopment.meteorclient.systems.commands.Commands;
import random.meteor.Modules.combat.*;
import meteordevelopment.meteorclient.systems.modules.Modules;
import random.meteor.Modules.commands.*;
import random.meteor.Modules.misc.*;
import meteordevelopment.meteorclient.systems.config.Config;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class  Manager {
    private static void addModules(){
        // Modules here
        Modules m = Modules.get();
        
        m.add(new PearlPhase());
        m.add(new HoleSnap());
        m.add(new BlockClap());
        m.add(new AutoPvp());
        m.add(new LeftClickArmor());
        m.add(new AutoPussy());
        m.add(new AutoKys());
        m.add(new AutoCum());
        m.add(new AutoRekit());
        m.add(new PopCrash());
        m.add(new AutoLeak());

    }
    private static void addCommands(){
        // Commands here
        Commands c = Commands.get();

        
        c.add(new Dupe());
        c.add(new Unbind());
        c.add(new Panic());
        c.add(new Center());
        c.add(new Ping());
        c.add(new Crash());

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

