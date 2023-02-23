package random.meteor.Modules;

import meteordevelopment.meteorclient.systems.commands.Commands;
import random.meteor.Modules.combat.*;
import meteordevelopment.meteorclient.systems.modules.Modules;
import random.meteor.Modules.commands.Dupe;
import random.meteor.Modules.commands.Panic;
import random.meteor.Modules.commands.Unbind;
import random.meteor.Modules.misc.LeftClickArmor;

public class Manager {
    private static void addModules(){
        // Modules here
        Modules.get().add(new PearlPhase());
        Modules.get().add(new HoleSnap());
        Modules.get().add(new BlockClap());
        Modules.get().add(new AutoPvp());
        Modules.get().add(new LeftClickArmor());


    }
    private static void addCommands(){
        // Commands here
        Commands.get().add(new Dupe());
        Commands.get().add(new Unbind());
        Commands.get().add(new Panic());
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

