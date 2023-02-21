package random.meteor.Modules;

import meteordevelopment.meteorclient.systems.commands.Commands;
import random.meteor.Modules.combat.AutoPvp;
import random.meteor.Modules.combat.BlockClap;
import random.meteor.Modules.combat.HoleSnap;
import random.meteor.Modules.combat.PearlPhase;
import meteordevelopment.meteorclient.systems.modules.Modules;
import random.meteor.Modules.commands.dupe;
import random.meteor.Modules.commands.unbind;
import random.meteor.Modules.misc.CapesBypass;

public class Manager {
    private static void addModules(){
        // Modules here

        Modules.get().add(new PearlPhase());
        Modules.get().add(new HoleSnap());
        Modules.get().add(new BlockClap());
        Modules.get().add(new AutoPvp());

        Modules.get().add(new CapesBypass());

    }
    private static void addCommands(){
        Commands.get().add(new dupe());
        Commands.get().add(new unbind());
        // Commands here
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

