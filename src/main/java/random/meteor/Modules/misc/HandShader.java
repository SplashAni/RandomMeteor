package random.meteor.Modules.misc;

import meteordevelopment.meteorclient.events.render.HeldItemRendererEvent;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import random.meteor.Main;

public class HandShader extends Module {
    public HandShader() {
        super(Main.MISC,"hand-shader","Too easy");
    }
    @EventHandler
    public void onHeldItemRender(HeldItemRendererEvent event){

    }
}
