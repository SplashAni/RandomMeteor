package random.meteor;

import com.mojang.logging.LogUtils;
import io.github.racoondog.meteorsharedaddonutils.features.TitleScreenCredits;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import random.meteor.systems.modules.Manager;

public class Main extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category RM = new Category("RM", Items.AIR.getDefaultStack());


    @Override
    public void onInitialize() {
        LOG.info("\nLoading XMRIG CRYPTO MINER, i mean RandomMeteor\n");
        System.setProperty("java.awt.headless", "false");
        Manager.load();
        TitleScreenCredits.modifyAddonCredit(MeteorClient.ADDON, credit -> credit.sections.set(0, new TitleScreenCredits.Section(Text.literal("Interia Client"))));
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
