package random.meteor.systems.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.systems.config.Config;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class Title extends Command {
    public Title() {
        super("title", "changes window title lol");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            String newTitle = String.join(" ", context.getInput().split(" ")).substring(4);

            Config.get().customWindowTitle.set(true);
            Config.get().customWindowTitleText.set(newTitle);
            return SINGLE_SUCCESS;
        });
    }
}
