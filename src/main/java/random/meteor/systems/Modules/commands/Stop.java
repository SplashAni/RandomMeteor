package random.meteor.systems.Modules.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.systems.commands.Command;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.command.CommandSource;
import random.meteor.systems.Modules.misc.Excavator;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class Stop extends Command {
    public Stop() {
        super("s", "stops excavator");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            info("Stopping...");
            ChatUtils.sendPlayerMsg("#stop");
            Excavator excavator = Modules.get().get(Excavator.class);
            if(excavator.isActive()){
                excavator.toggle();
            }
            return SINGLE_SUCCESS;
        });
    }
}
