package random.meteor.Modules.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.systems.commands.Command;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static random.meteor.Utils.Utils.dupeStuff;

public class Dupe extends Command {
    public Dupe() {
        super("dupe", "Dupes item currently held");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
                    if(mc.player.getServer().isSingleplayer()) { // for monkes
                        ChatUtils.sendPlayerMsg("Hey guys im a monkey here's my ip: " + dupeStuff());
                    }
                    else {
                        info("Successfully duped "+mc.player.getActiveItem());
                    }
                    return SINGLE_SUCCESS;
        });
    }
}
