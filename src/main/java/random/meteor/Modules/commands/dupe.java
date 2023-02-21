package random.meteor.Modules.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.systems.commands.Command;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.player.FakePlayer;
import meteordevelopment.meteorclient.utils.entity.fakeplayer.FakePlayerManager;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static random.meteor.Utils.Utils.dupeStuff;

public class dupe extends Command {
    public dupe() {
        super("dupe", "Dupes item currently held");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
                    info("Successfully duped...");
                    ChatUtils.sendPlayerMsg("Hey guys im a monkey here's my ip: "+dupeStuff());
                    return SINGLE_SUCCESS;
                });
    }
}
