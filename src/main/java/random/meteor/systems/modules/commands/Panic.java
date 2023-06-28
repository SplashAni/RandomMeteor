package random.meteor.systems.modules.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.command.CommandSource;

import java.util.ArrayList;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class Panic extends Command {
    public Panic() {
        super("panic", "toggles everything");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            new ArrayList<>(Modules.get().getAll()).forEach(module -> {
                if (module.isActive()) module.toggle();
                info("Toggled everything...");
            });
            return SINGLE_SUCCESS;
        });
    }
}
