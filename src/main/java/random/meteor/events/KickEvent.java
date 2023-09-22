package random.meteor.events;

import jdk.jfr.Event;

public class KickEvent extends Event {
    public static KickEvent INSTANCE = new KickEvent();
    public static KickEvent get(){
        return INSTANCE;
    }
}
