package random.meteor.systems.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.text2speech.Narrator;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;

public class Tts extends Command {
    public Tts() {
        super("tts","Plays the provided message as text");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("message", StringArgumentType.greedyString()).executes(context -> {
            String msg = context.getArgument("message", String.class);

            Narrator n = Narrator.getNarrator();

            n.say(msg,true);

            return 0;
        }));
    }

}
