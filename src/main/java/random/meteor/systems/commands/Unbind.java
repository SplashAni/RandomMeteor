package random.meteor.systems.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.commands.arguments.ModuleArgumentType;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import net.minecraft.command.CommandSource;

public class Unbind extends Command {
    public Unbind() {
        super("unbind", "Unbinds the module");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("module", ModuleArgumentType.create()).executes(context -> {
            context.getArgument("module", Module.class).keybind.set(Keybind.none());

            info(context.getArgument("module",Module.class).name.concat( "has been unbinded."));

            return SINGLE_SUCCESS;
        }));
    }
}
