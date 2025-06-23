package random.meteor.systems;

import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.systems.modules.Modules;
import random.meteor.systems.commands.*;
import random.meteor.systems.modules.*;

import java.util.ArrayList;
import java.util.List;

public class Manager {
    public List<Mod> modules = new ArrayList<>();
    public List<Command> commands = new ArrayList<>();
    public Manager(){
        addModules();
        modules.forEach(module -> Modules.get().add(module));
        addCommands();
        commands.forEach(Commands::add);
    }
    private void addModules() {
        add(new PearlPhase());
        add(new CrepperAura());
        add(new Prefix());
        add(new ItemRenderer());
        add(new PistonPush());
        add(new BlockClap());
        add(new BowCart());
        add(new ChatEmojis());
        add(new CustomFov());
        add(new AutoChunkBan());
        add(new TntAura());
        add(new AutoGold());
        add(new Twerk());
        add(new MinecartAura());
        add(new AutoSnowball());
        add(new BurrowEsp());
        add(new PistonAura());
        add(new Blocker());
        add(new PistonBurrow());
        add(new DeathEffect());
        add(new Multitask());
        add(new AutoDupe());
        add(new DeathEffect());
        add(new AutoMine());
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
        add(new TTS());

    }

    public void add(Mod module) {
        if (!modules.contains(module)) modules.add(module);
    }

    public void add(Command command) {
        if (!commands.contains(command)) commands.add(command);
    }



}

