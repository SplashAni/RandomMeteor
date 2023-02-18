package random.meteor;

import random.meteor.Modules.Manager;
import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.item.Items;
import org.slf4j.Logger;

public class Main extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category MISC = new Category("Misc+", Items.AIR.getDefaultStack());
    public static final Category COMBAT= new Category("Combat+", Items.AIR.getDefaultStack());

    @Override
    public void onInitialize() {
        LOG.info("Initializing Meteor Main Template");

        Manager.load();
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(MISC);
        Modules.registerCategory(COMBAT);
    }

    @Override
    public String getPackage() {
        return "random.meteor";
    }
}
