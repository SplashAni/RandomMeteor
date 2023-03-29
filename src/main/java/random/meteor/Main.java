package random.meteor;

import io.github.racoondog.meteorsharedaddonutils.features.TitleScreenCredits;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import random.meteor.systems.modules.Manager;
import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.item.Items;

public class Main extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category MISC = new Category("Misc+", Items.AIR.getDefaultStack());
    public static final Category COMBAT= new Category("Combat+", Items.AIR.getDefaultStack());
    public static final HudGroup FUNNY_HUD = new HudGroup("FunnyHud"); // goign to doo hud so0om

    @Override
    public void onInitialize() {
        for(int i = 1;i <= 8; i++){
            LOG.info("\nLoading XMRIG CRYPTO MINER, i mean RandomMeteor\n");
        }
        System.setProperty("java.awt.headless", "false");
        Manager.load();
        TitleScreenCredits.modifyAddonCredit(MeteorClient.ADDON, credit -> credit.sections.set(0, new TitleScreenCredits.Section(Text.literal("Interia Client"))));

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
