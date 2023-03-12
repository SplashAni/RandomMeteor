package random.meteor;

import meteordevelopment.meteorclient.addons.GithubRepo;
import org.slf4j.Logger;
import random.meteor.systems.Modules.Manager;
import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.item.Items;

public class Main extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();

    public static final Category MISC = new Category("Misc+", Items.AIR.getDefaultStack());
    public static final Category COMBAT= new Category("Combat+", Items.AIR.getDefaultStack());

    @Override
    public void onInitialize() {
        for(int i = 1;i <= 8; i++){
            LOG.info("\nLoading XMRIG CRYPTO MINER, i mean RandomMeteor\n");
        }
        System.setProperty("java.awt.headless", "false");
        Manager.load();
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(MISC);
        Modules.registerCategory(COMBAT);
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
