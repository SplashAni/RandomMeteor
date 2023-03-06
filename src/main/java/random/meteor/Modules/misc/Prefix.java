package random.meteor.Modules.misc;

import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import random.meteor.Main;

public class Prefix extends Module {
    public Prefix(){
        super(Main.MISC, "Random Meteor Prefix", "real!1!1");
    }
    private SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<SettingColor> PrefixColor = sgGeneral.add(new ColorSetting.Builder()
        .name("Prefix Color")
        .description("The color of the prefix.")
        .defaultValue(new SettingColor(255, 128, 0))
        .build()
    );
    private Setting<String> PrefixText = sgGeneral.add(
        new StringSetting.Builder()
            .name("Prefix Text")
            .defaultValue("RandomMeteor")
            .build()
    );
    private Setting<String> Left = sgGeneral.add(
       new StringSetting.Builder()
            .name("Left")
            .defaultValue("[")
            .build()
    );
    private Setting<String> Right = sgGeneral.add(
       new StringSetting.Builder()
            .name("Right")
            .defaultValue("] ")
            .build()
    );


    public Text prefixerededyengrish(){
        MutableText leprefix = net.minecraft.text.Text.literal(PrefixText.get());
        leprefix.getStyle().withColor(TextColor.fromRgb(PrefixColor.get().getPacked()));
        MutableText left = Text.literal(Left.get());
         MutableText right = Text.literal(Right.get());
         MutableText built = Text.literal("");
         left.getStyle().withColor(TextColor.fromFormatting(Formatting.GRAY));
        right.getStyle().withColor(TextColor.fromFormatting(Formatting.GRAY));
        built.append(left);
        built.append(leprefix);
        built.append(right);
       return built;
    }


    @Override
    public void onActivate() {
         bru();
    }
    @Override
    public void onDeactivate() {
        ChatUtils.unregisterCustomPrefix("random.meteor.Modules");
    }

    public void bru(){
        if (isActive()) {
            ChatUtils.registerCustomPrefix("random.meteor.Modules", this::prefixerededyengrish);}
    }
}
