package random.meteor.systems.modules;

import meteordevelopment.meteorclient.events.game.SendMessageEvent;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import random.meteor.Main;

public class AutoXd extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Double> renderSpeed = sgGeneral.add(new DoubleSetting.Builder()
            .name("render-speed")
            .description("The speed at which the render animation progresses.")
            .defaultValue(1)
            .min(0.1)
            .max(10)
            .sliderMin(0.1)
            .sliderMax(1)
            .build()
    );

    private boolean render;
    private long startTime;

    public AutoXd() {
        super(Main.RM, "auto-xd", "zsMorgen");
    }

    @EventHandler
    public void onMsg(SendMessageEvent event) {
        String msg = event.message;
        if (msg.contains("xd")) {
            render = true;
            startTime = System.currentTimeMillis();
        }
    }

    @EventHandler
    public void render(Render2DEvent event) {
        if (render) {
            long timer = System.currentTimeMillis() - startTime;
            MatrixStack matrices = event.drawContext.getMatrices();
            double renderMultiplier = renderSpeed.get();
            int widthGoal = 256;
            int heightGoal = 256;
            int scale = 30 * 100;
            if (timer <= scale) {
                int centerX = (event.screenWidth - widthGoal) / 2;
                int centerY = (int) (event.screenHeight - (timer * renderMultiplier));

                matrices.push();
                matrices.translate(centerX, centerY, 0);
                event.drawContext.drawTexture( new Identifier("random", "xd.png"), 0, 0, 0, 0, widthGoal, heightGoal);
                matrices.pop();
            } else {
                render = false;
            }
        }
    }
}