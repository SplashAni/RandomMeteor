package random.meteor.systems.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.systems.friends.Friends;
import net.minecraft.command.CommandSource;

public class Betrayed extends Command {
    public Betrayed() {
        super("betrayed","Unfriends everyone");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            Friends.get().forEach(friend -> Friends.get().remove(friend));
            return SINGLE_SUCCESS;
        });
    }
}
