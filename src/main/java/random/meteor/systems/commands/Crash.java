package random.meteor.systems.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;

public class Crash extends Command {
    public Crash() {
        super("crash","Closes you game");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            mc.close();
            return SINGLE_SUCCESS;
        });
    }
}
