package random.meteor.systems.modules.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.mixin.ClientPlayNetworkHandlerAccessor;
import meteordevelopment.meteorclient.systems.commands.Command;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.utils.misc.MeteorStarscript;
import meteordevelopment.starscript.Script;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.message.LastSeenMessagesCollector;
import net.minecraft.network.message.MessageBody;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;

import java.time.Instant;

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
