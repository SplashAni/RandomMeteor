package random.meteor.systems.modules.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class Dupe extends Command {
    public Dupe() {
        super("dupe", "Dupes item currently held");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
                    if(mc.player.getServer().isSingleplayer()) { // if this is ever gonna be on anticope its gotta be random not real
                        String built = "";
                        for (int i = 1; i < 5; i++) {
                            built = built + (String.valueOf(r(1,255)) + ".");
                        }
                        ChatUtils.sendPlayerMsg("Hey guys im a monkey here's my ip: " + built);
                    }
                    else {
                        ChatUtils.info("Successfully duped " + mc.player.getActiveItem());
                    }
                    return SINGLE_SUCCESS;
        });
    }

    public static int r(int o, int e) {
        return o + (int) (Math.random() * ((e - o) + 1));
    }
}
