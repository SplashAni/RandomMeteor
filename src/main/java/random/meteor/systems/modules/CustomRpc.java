package random.meteor.systems.modules;

import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import random.meteor.Main;

public class CustomRpc extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<String> id = sgGeneral.add(new StringSetting.Builder()
        .name("new-id")
        .description("you own id bruh")
        .defaultValue("835240968533049424")
        .build()
    );

    public CustomRpc() {
        super(Main.RM, "custom-rpc", "mixins into discord presences app id");
        runInMainMenu = true; // no funny shit bro
    }

    @Override
    public void onActivate() {
        this.toggle();

        super.onActivate();
    }

    public Long getId(){
        if(id.get().length() != 18) return 835240968533049424L;
        else return Long.valueOf(id.get());
    }

}

