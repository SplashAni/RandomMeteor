package random.meteor.Modules;

import meteordevelopment.meteorclient.systems.commands.Commands;
import random.meteor.Modules.combat.*;
import meteordevelopment.meteorclient.systems.modules.Modules;
import random.meteor.Modules.commands.Dupe;
import random.meteor.Modules.commands.Panic;
import random.meteor.Modules.commands.Unbind;
import random.meteor.Modules.misc.AutoCum;
import meteordevelopment.meteorclient.systems.config.Config;
import random.meteor.Modules.misc.AutoKys;
import random.meteor.Modules.misc.AutoPussy;
import random.meteor.Modules.misc.LeftClickArmor;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class  Manager {
    private static void addModules(){
        if (mc.player.getUuidAsString().equals ("d939e2b1-f9c3-49b3-99d0-cbdc53002d94")){
         Config.get().customWindowTitle.set(true);
         Config.get().customWindowTitleText.set("I AM SPLASHANI AND SEX WOMEN STRONK EZZZZ");
        }
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

    }
    private static void addCommands(){
        // Commands here
        Commands c = Commands.get();

        
        c.add(new Dupe());
        c.add(new Unbind());
        c.add(new Panic());
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

