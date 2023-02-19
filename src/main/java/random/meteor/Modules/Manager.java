package random.meteor.Modules;

import random.meteor.Modules.combat.BlockClap;
import random.meteor.Modules.combat.HoleSnap;
import random.meteor.Modules.combat.PearlPhase;
import meteordevelopment.meteorclient.systems.modules.Modules;

public class Manager {
    private static void addModules(){
        // Modules here

        Modules.get().add(new PearlPhase());
        Modules.get().add(new HoleSnap());
        Modules.get().add(new BlockClap());
    }
    private static void addCommands(){

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

