package random.meteor.manager;

public abstract class Manager {
    boolean events;

    public void onInitialize() {

    }

    public void setEvents(boolean events) {
        this.events = events;
    }
}
