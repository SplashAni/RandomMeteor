package random.meteor.Modules;

import random.meteor.Modules.combat.PearlPhase;
import meteordevelopment.meteorclient.systems.modules.Modules;

public class Manager {
    private static void addModules(){
        // Modules here

        Modules.get().add(new PearlPhase());

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

