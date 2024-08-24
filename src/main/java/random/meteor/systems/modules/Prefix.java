package random.meteor.systems.modules;

import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import random.meteor.systems.Mod;

public class Prefix extends Mod {/* what bullshit code is this 💀💀💀💀💀*/
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<mode> m = sgGeneral.add(new EnumSetting.Builder<mode>()
        .name("mode")
        .defaultValue(mode.Normal)
        .build());
    private final Setting<SettingColor> color = sgGeneral.add(new ColorSetting.Builder()
        .name("Prefix Color")
        .description("The color of the prefix.")
        .defaultValue(new SettingColor(255, 128, 69)) //colors no work??, yes because walper is so silly what the fuck
        .build()
    );
    private final Setting<String> prefix = sgGeneral.add(
        new StringSetting.Builder()
            .name("Prefix Text")
            .defaultValue("modules")
            .build()
    );
    private final Setting<String> l = sgGeneral.add(
       new StringSetting.Builder()
            .name("Left")
            .defaultValue("[")
            .build()
    );
    private final Setting<String> r = sgGeneral.add(
       new StringSetting.Builder()
            .name("Right")
            .defaultValue("] ")
            .build()
    );

    private final SettingGroup sgOverride = settings.createGroup("Override",true);

    private final Setting<Boolean> overrideMeteor = sgOverride.add(new BoolSetting.Builder()
            .name("override-meteor")
            .description("overrides meteors prefix")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> otherAddons = sgOverride.add(new BoolSetting.Builder()
            .name("override-others")
            .defaultValue(false)
            .build()
    );
    private final Setting<String> bypassPackage = sgOverride.add(new StringSetting.Builder()
            .name("package-name")
            .description("")
            .defaultValue("example.addon")
            .visible(otherAddons::get)
            .build()
    );    public Prefix(){
        super("prefix", "");
    }


    @Override
    public void onActivate() {
        applyPrefix();

        super.onActivate();
    }

    @Override
    public void onDeactivate() {
        ChatUtils.unregisterCustomPrefix("random.meteor");
        ChatUtils.unregisterCustomPrefix("meteordevelopment");
        if(otherAddons.get()){
            ChatUtils.unregisterCustomPrefix(bypassPackage.get());
        }
        super.onDeactivate();
    }
    @EventHandler
    public void onTick(TickEvent.Pre event){
        prefix();
    }


    public Text prefix(){
        MutableText raw = Text.literal(prefix.get());

        switch (m.get()){

            case Normal -> {

                raw.getStyle().withColor(TextColor.fromRgb(color.get().getPacked()));

            }
            case Rainbow -> {
                raw = rainbow(prefix.get());
            }
        }


        /*
        * other format
        * */
        MutableText left = Text.literal(l.get());
        left.formatted();
        MutableText right = Text.literal(r.get());
        MutableText built = Text.literal("");
        left.getStyle().withColor(TextColor.fromFormatting(Formatting.GRAY));
        right.getStyle().withColor(TextColor.fromFormatting(Formatting.GRAY));
        built.append(left);
        built.append(raw);
        built.append(right);
        return built;
    }
    public MutableText rainbow(String text) {
        String drawString = text;
        MutableText drawText = Text.literal("");
        int hue = MathHelper.floor((System.currentTimeMillis() % 5000L) / 5000.0F * 360.0F);

        for (char c : drawString.toCharArray()) {
            int finalHue = hue;
            drawText.append(Text.literal(Character.toString(c)).styled(s -> s.withColor(MathHelper.hsvToRgb(finalHue / 360.0F, 1.0F, 1.0F))));
            hue += 100 / drawString.length();
            if (hue >= 360) hue %= 360;
        }
        return drawText;
    }

    @EventHandler
    public void onRender(Render2DEvent event){
        prefix();
    }
    public void applyPrefix(){

       if(overrideMeteor.get()){
           register("meteordevelopment");
       }
       if(otherAddons.get()){
           register(bypassPackage.get());
       }

       register("random.meteor");
    }
    public void register(String name){
        ChatUtils.registerCustomPrefix(name,() -> prefix());
    }

    public enum mode{
        Normal,
        Rainbow
    }
}
