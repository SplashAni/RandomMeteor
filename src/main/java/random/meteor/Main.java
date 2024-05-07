package random.meteor;

import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.item.Items;
import org.slf4j.Logger;
import random.meteor.systems.Manager;
import random.meteor.utils.ReadmeWriter;

public class Main extends MeteorAddon {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Category RM = new Category("RM", Items.BARRIER.getDefaultStack());
    public static Manager MANAGER;
    @Override
    public void onInitialize() {

        LOGGER.info("Thanks for using Random Meteor <3\n Make sure to star on github plz");
        MANAGER = new Manager();

        MANAGER.updateReadme();
    }


    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(RM);
    }

    @Override
    public GithubRepo getRepo() {
        return new GithubRepo("SplashAni", "RandomMeteor");
    }

    @Override
    public String getPackage() {
        return "random.meteor";
    }

}
