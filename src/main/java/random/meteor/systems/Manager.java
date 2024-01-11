package random.meteor.systems;

import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import random.meteor.systems.commands.*;
import random.meteor.systems.modules.*;

import java.util.ArrayList;
import java.util.List;

public class Manager {
    private static void addModules() {

        List<Module> m = new ArrayList<>();
        m.add(new PearlPhase());
        m.add(new CrepperAura());
        m.add(new Prefix());
        m.add(new ItemRenderer());
        m.add(new PistonPush());
        m.add(new Anti32k());
        m.add(new BlockClap());
        m.add(new CustomFov());
        m.add(new AutoChunkBan());
        m.add(new TntAura());
        m.add(new AutoGold());
        m.add(new Twerk());
        m.add(new MinecartAura());
        m.add(new AutoSnowball());
        m.add(new BurrowEsp());
        m.add(new Blocker());
        m.add(new PistonBurrow());
        m.add(new PistonAura());
        m.add(new AutoCum());
        m.add(new AutoRun());
        m.add(new CrystalBomb());
        m.add(new AutoXd());
        m.add(new Multitask());
        m.add(new InstantMend());
        m.add(new AutoCum());
        m.add(new AutoMine());
        m.add(new PlayerTp());
        m.add(new MeteorSpoof());
        m.add(new CustomRpc());
        m.add(new FakeKick());
        m.forEach(module -> Modules.get().add(module));
    }

    private static void addCommands() {
        Commands.add(new Betrayed());
        Commands.add(new Dupe());
        Commands.add(new Unbind());
        Commands.add(new Panic());
        Commands.add(new Center());
        Commands.add(new Crash());
        Commands.add(new Ping());
        Commands.add(new Title());
        Commands.add(new Tts());
    }

    private static void addHud() {
    }

    public static void init() {
        addModules();
        addCommands();
        addHud();
    }
}

