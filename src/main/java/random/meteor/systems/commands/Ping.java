package random.meteor.systems.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.utils.player.PlayerUtils.getPing;

public class Ping extends Command {
    public Ping() {
        super("ping", "returns you ping");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            info("You ping is: " +getPing());
            return SINGLE_SUCCESS;
        });
    }
}
