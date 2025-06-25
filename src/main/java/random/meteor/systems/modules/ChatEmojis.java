package random.meteor.systems.modules;

import meteordevelopment.meteorclient.events.meteor.KeyEvent;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.ChatScreen;
import org.lwjgl.glfw.GLFW;
import random.meteor.events.ChatRenderEvent;
import random.meteor.systems.Mod;
import random.meteor.utils.ChatFieldDuck;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatEmojis extends Mod {
    private final Set<Emoji> emojis = new HashSet<>();
    private final Pattern pattern = Pattern.compile(":(\\w+):");
    private boolean isTabPressed;

    public ChatEmojis() {
        super("chat-emojis");

        /*https://www.webfx.com/tools/emoji-cheat-sheet/ <--- ive only tested a few please make a pr if you find more*/
        registerEmoji("skull", "☠");
        registerEmoji("smile", "☺");
        registerEmoji("heart", "❤");
        registerEmoji("star", "⭐");
        registerEmoji("fire", "\uD83D\uDD25");
    }

    @EventHandler
    public void onChatRender(ChatRenderEvent event) {
        if (event.cartX == -1 || event.caretY == -1 || event.currentText == null) return;

        String text = event.currentText;

        if (isTabPressed && mc.currentScreen instanceof ChatScreen chatScreen) {


            String replacedText = pattern.matcher(text).replaceAll(match -> {
                String name = match.group(1).toLowerCase();
                return emojis.stream()
                    .filter(e -> e.name.equals(name))
                    .findFirst()
                    .map(e -> e.content)
                    .orElse(match.group(0));
            });

            if (!replacedText.equals(text)) {
                ((ChatFieldDuck) chatScreen).randomMeteor$setText(replacedText);
            }

            isTabPressed = false;
        }

        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String emojiName = matcher.group(1).toLowerCase();

            Emoji emoji = emojis.stream()
                .filter(e -> e.name.equals(emojiName))
                .findFirst()
                .orElse(null);

            if (emoji == null) continue;

            int startIndex = matcher.start();
            int endIndex = matcher.end();

            int xStart = mc.textRenderer.getWidth(text.substring(0, startIndex)) + 4;
            int xEnd = mc.textRenderer.getWidth(text.substring(0, endIndex)) + 4;

            int color = new Color(77, 203, 123, 197).getRGB();

            event.context.fill(xStart, event.caretY - 1, xEnd, event.caretY + 9, color);

            int boxWidth = xEnd - xStart;

            int emojiWidth = mc.textRenderer.getWidth(emoji.content);

            int emojiX = xStart + (boxWidth / 2) - (emojiWidth / 2);

            int emojiY = event.caretY - 12;

            event.context.drawTextWithShadow(mc.textRenderer, emoji.content, emojiX, emojiY, Color.WHITE.getRGB());
        }
    }


    private void registerEmoji(String name, String content) {
        emojis.add(new Emoji(name, content));
    }

    @EventHandler
    public void onKeyPress(KeyEvent event) {
        if (event.key == GLFW.GLFW_KEY_TAB) {
            if (event.action == KeyAction.Press) {
                isTabPressed = true;
            } else if (event.action == KeyAction.Release) {
                isTabPressed = false;
            }
        }

        if ((mc.currentScreen instanceof ChatScreen)) return;

        if (event.key == GLFW.GLFW_KEY_SEMICOLON) {
            if ((event.modifiers & GLFW.GLFW_MOD_SHIFT) != 0) {
                mc.setScreen(new ChatScreen(""));
            }
        }
    }


    private record Emoji(String name, String content) {
    }
}
