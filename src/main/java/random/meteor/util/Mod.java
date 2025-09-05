package random.meteor.util;

import meteordevelopment.meteorclient.systems.modules.Module;
import random.meteor.Main;

public class Mod extends Module {
    String name, desc;
    Category category; // will be used to represent the moduel catgegory in future...;

    public Mod(String name, String desc, Category category) {
        super(Main.RM, name, desc);
        this.name = name;
        this.desc = desc;
        this.category = category;
    }

    public Mod(String name, Category category) {
        super(Main.RM, name, "No description yet...");
        this.name = name;
        this.desc = "";
        this.category = category;
    }
}
