package random.meteor.events;

import jdk.jfr.Event;
import net.minecraft.client.gui.DrawContext;

public class KickEvent extends Event {
    public static KickEvent INSTANCE = new KickEvent();

    public static KickEvent get(){
        return INSTANCE;
    }

}
