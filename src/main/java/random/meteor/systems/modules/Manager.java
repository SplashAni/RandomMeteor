package random.meteor.systems.modules;

import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.combat.AutoCity;
import random.meteor.systems.modules.RM.*;
import random.meteor.systems.modules.commands.*;

import java.util.ArrayList;
import java.util.List;
import meteordevelopment.meteorclient.commands.Commands;

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
        m.add(new Blockpos());
        m.add(new AutoGold());
        m.add(new Twerk());
        m.add(new MinecartAura());
        m.add(new BurrowEsp());
        m.add(new Blocker());
        m.add(new CrystalBomb());
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

