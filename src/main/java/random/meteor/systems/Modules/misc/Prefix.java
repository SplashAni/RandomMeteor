package random.meteor.systems.Modules.misc;

import meteordevelopment.meteorclient.settings.*;
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
        super(Main.MISC, "prefix", "real!1!1");
    }
    private SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<SettingColor> PrefixColor = sgGeneral.add(new ColorSetting.Builder()
        .name("Prefix Color")
        .description("The color of the prefix.")
        .defaultValue(new SettingColor(255, 128, 69)) //colors no work??
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
    private final Setting<Boolean> meteorBypass = sgGeneral.add(new BoolSetting.Builder()
            .name("bypass-meteor")
            .description("overrides meteors prefix")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> addonBypass = sgGeneral.add(new BoolSetting.Builder()
            .name("addon-bypass")
            .description("overrides meteors prefix")
            .defaultValue(false)
            .build()
    );
    private final Setting<String> otherBypasses = sgGeneral.add(new StringSetting.Builder()
            .name("addons-bypss")
            .description("")
            .defaultValue("example.addon")
            .visible(addonBypass::get)
            .build()
    );

    public Text prefix(){
        MutableText leprefix = net.minecraft.text.Text.literal(PrefixText.get());
        leprefix.getStyle().withColor(TextColor.fromRgb(PrefixColor.get().getPacked()));
        MutableText left = Text.literal(Left.get());
        left.formatted();
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
         applyPrefix();
    }
    @Override
    public void onDeactivate() {
        ChatUtils.unregisterCustomPrefix("random.meteor.Modules");
        ChatUtils.unregisterCustomPrefix("meteordevelopment");
        if(addonBypass.get()){
            ChatUtils.unregisterCustomPrefix(otherBypasses.get());
        }
    }

    public void applyPrefix(){
        if (isActive()) {
            if(meteorBypass.get()){
                ChatUtils.registerCustomPrefix("meteordevelopment", this::prefix);
            }
            if(addonBypass.get()){
                ChatUtils.registerCustomPrefix(otherBypasses.get(),this::prefix);
            }
            ChatUtils.registerCustomPrefix("random.meteor.Modules", this::prefix);}
    }
}
