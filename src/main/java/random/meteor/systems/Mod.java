package random.meteor.systems;

import meteordevelopment.meteorclient.systems.modules.Module;
import random.meteor.Main;

public abstract class Mod extends Module {
    public String showcase;

    public Mod(String name, String description) {
        super(Main.RM, name, description);
    }

    public Mod(String name) {
        super(Main.RM, name, "No description yet");
    }

    public Mod(String name, String description, String showcase) {
        super(Main.RM, name, description);
        this.showcase = showcase;
    }

}
