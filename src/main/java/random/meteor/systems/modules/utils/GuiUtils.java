package random.meteor.systems.modules.utils;

import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;

public class GuiUtils {
    public static void renderButton(MatrixStack matrices, int bX, int bY, String buttonText, int textY) {
        int paddingX = 10;
        int paddingY = 5;

        int textWidth = (int) TextRenderer.get().getWidth(buttonText);
        int textHeight = 8;

        int bWidth = textWidth + 2 * paddingX;
        int bHeight = textHeight + 2 * paddingY;

        DrawableHelper.fill(matrices, bX, bY, bX + bWidth, bY + bHeight, new Color(20, 20, 20, 200).getRGB());

        DrawableHelper.fill(matrices, bX - 1, bY - 1, bX + bWidth + 1, bY, Color.WHITE.getRGB());
        DrawableHelper.fill(matrices, bX - 1, bY + bHeight, bX + bWidth + 1, bY + bHeight + 1, Color.WHITE.getRGB());
        DrawableHelper.fill(matrices, bX - 1, bY, bX, bY + bHeight, Color.WHITE.getRGB());
        DrawableHelper.fill(matrices, bX + bWidth, bY, bX + bWidth + 1, bY + bHeight, Color.WHITE.getRGB());

        int textX = bX + paddingX;
        int adjustedTextY = bY + paddingY + textY; // Adjust the Y-coordinate of the text
        TextRenderer.get().render(buttonText, textX, adjustedTextY, new meteordevelopment.meteorclient.utils.render.color.Color(Color.WHITE.getRGB()));
    }
}
