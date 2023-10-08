package random.meteor;

import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.item.Items;
import org.slf4j.Logger;
import random.meteor.systems.Manager;

public class Main extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category RM = new Category("modules", Items.AIR.getDefaultStack());
    public static final HudGroup HUD = new HudGroup("RandomHud");
    @Override
    public void onInitialize() {

        LOG.info("\nLoading XMRIG CRYPTO MINER, i mean RandomMeteor\n");

        Manager.init();
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
