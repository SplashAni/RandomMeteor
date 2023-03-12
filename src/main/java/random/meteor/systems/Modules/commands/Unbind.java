package random.meteor.systems.Modules.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.systems.commands.Command;
import meteordevelopment.meteorclient.systems.commands.arguments.ModuleArgumentType;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class Unbind extends Command {
    public Unbind() {
        super("unbind", "Unbinds the module");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("module", ModuleArgumentType.create()).executes(context -> {
            context.getArgument("module", Module.class).keybind.set(Keybind.none());
            info("Successfully unbinded module!");
            return SINGLE_SUCCESS;
        }));
    }
}
