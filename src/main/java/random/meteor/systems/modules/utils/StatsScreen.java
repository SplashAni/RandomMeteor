package random.meteor.systems.modules.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import random.meteor.systems.modules.RM.Stats;

import java.awt.*;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class StatsScreen extends Screen {
    public StatsScreen() {
        super(Text.of("h"));
    }

  /*  @Override
    public void initWidgets() {

        WButton customButton = theme.button("Delete");
        customButton.x = 10;
        customButton.y = 10;
        customButton.action = () -> {
            YesNoPrompt.create(theme, this.parent)
                .title("Warning!")
                .message("Clearing you stats cannot be undone")
                .message("All you progress will be lost")
                .onYes(() -> Config.get().prefix.set("."))
                .show();


        };
        add(customButton);
        WButton save = theme.button("Save");

        customButton.action = () -> {


        };
        add(customButton);
        add(save);
        this.window.x = 520;
        this.window.y = 608;
    }

   */

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        Stats s= Modules.get().get(Stats.class);
        int width = this.client.getWindow().getScaledWidth();
        int height = this.client.getWindow().getScaledHeight();
        int bWidth = 263;
        int bHeight = 300;

        int bX = (width - bWidth) / 2;
        int bY = (height - bHeight) / 2;
        DrawableHelper.fill(matrices, bX, bY, bX + bWidth, bY + bHeight, new Color(20, 20, 20, 200).getRGB());

        int borderColor = new Color(255, 255, 255, 200).getRGB(); // You can adjust the color as needed

        for (int i = 0; i < 4; i++) {
            int startX, startY, endX, endY;

            switch (i) {
                case 0 -> {
                    startX = bX - 1;
                    startY = bY - 1;
                    endX = bX + bWidth + 1;
                    endY = bY;
                }
                case 1 -> {
                    startX = bX - 1;
                    startY = bY;
                    endX = bX;
                    endY = bY + bHeight;
                }
                case 2 -> {
                    startX = bX + bWidth;
                    startY = bY;
                    endX = bX + bWidth + 1;
                    endY = bY + bHeight;
                }
                case 3 -> { // Bottom line
                    startX = bX - 1;
                    startY = bY + bHeight;
                    endX = bX + bWidth + 1;
                    endY = bY + bHeight + 1;
                }
                default -> startX = startY = endX = endY = 0;
            }

            DrawableHelper.fill(matrices, startX, startY, endX, endY, borderColor);
        }

        DrawableHelper.fill(matrices, bX, bY, bX + bWidth, bY + bHeight, new Color(20, 20, 20, 200).getRGB());

        String title = "Stats of " + mc.getNetworkHandler().getProfile().getName();
        int textWidth = (int) TextRenderer.get().getWidth(title);
        int textX = bX + (bWidth - textWidth) / 2;

        RenderSystem.setShaderTexture(0, mc.player.getSkinTexture());
        PlayerSkinDrawer.draw(matrices, bX + 208, 50, 20, true, false);
        TextRenderer.get().render(title, textX, -5, new meteordevelopment.meteorclient.utils.render.color.Color(Color.WHITE));

        String[] info = {
            "Kills: ",
            "Deaths: ",
            "Highest killstreak: ",
            "Highest deathstreak: ",
            "Your elo: "
        };

        int lineHeight = (int) TextRenderer.get().getHeight();
        int incrementY = 45;

        for (String line : info) {
            TextRenderer.get().render(line, textX, incrementY, new meteordevelopment.meteorclient.utils.render.color.Color(Color.WHITE));
            incrementY += lineHeight + 5;
        }

        GuiUtils.renderButton(matrices,268,250,"Delete",-62);
        GuiUtils.renderButton(matrices,350,250,"Reset",-62);

        super.render(matrices, mouseX, mouseY, delta);
    }
}
