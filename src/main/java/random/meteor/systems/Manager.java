package random.meteor.systems;

import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.PostInit;
import meteordevelopment.meteorclient.utils.PreInit;
import random.meteor.systems.commands.*;
import random.meteor.systems.modules.*;

import java.util.ArrayList;
import java.util.List;

public class Manager {
    List<Module> modules = new ArrayList<>();
    List<Command> commands = new ArrayList<>();

    private void addModules() {
        add(new PearlPhase());
        add(new CrepperAura());
        add(new Prefix());
        add(new ItemRenderer());
        add(new PistonPush());
        add(new Anti32k());
        add(new BlockClap());
        add(new CustomFov());
        add(new AutoChunkBan());
        add(new TntAura());
        add(new AutoGold());
        add(new Twerk());
        add(new MinecartAura());
        add(new AutoSnowball());
        add(new BurrowEsp());
        add(new Blocker());
        add(new PistonBurrow());
        add(new PistonAura());
        add(new AutoCum());
        add(new AutoRun());
        add(new CrystalBomb());
        add(new AutoXd());
        add(new Multitask());
        add(new InstantMend());
        add(new AutoCum());
        add(new AutoMine());
        add(new PlayerTp());
        add(new MeteorSpoof());
        add(new CustomRpc());
        add(new MinecartSpeed());
        add(new FakeKick());
    }

    private void addCommands() {
        add(new Betrayed());
        add(new Dupe());
        add(new Unbind());
        add(new Panic());
        add(new Center());
        add(new Crash());
        add(new Ping());
        add(new Title());
        add(new Tts());

    }

    public void add(Module module) {
        if (!modules.contains(module)) modules.add(module);
    }

    public void add(Command command) {
        if (!commands.contains(command)) commands.add(command);
    }
    public void init() {
        addModules();
        modules.forEach(module -> Modules.get().add(module));
        addCommands();
        commands.forEach(Commands::add);
    }

}

