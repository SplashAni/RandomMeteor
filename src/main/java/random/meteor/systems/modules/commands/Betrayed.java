package random.meteor.systems.modules.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.systems.commands.Command;
import meteordevelopment.meteorclient.systems.friends.Friends;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class Betrayed extends Command {
    public Betrayed() {
        super("betrayed","oh lord");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            Friends.get().forEach(friend -> Friends.get().remove(friend));
            return SINGLE_SUCCESS;
        });
    }
}
