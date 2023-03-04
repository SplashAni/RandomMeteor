package random.meteor.Modules.misc;

import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.misc.BetterChat;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import random.meteor.Main;

import static random.meteor.Utils.Utils.*;

public class AutoLeak extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> coords = sgGeneral.add(new BoolSetting.Builder()
            .name("coords")
            .description("sends current coords to server")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> coordsBypass = sgGeneral.add(new BoolSetting.Builder()
            .name("better-chat-bypass")
            .description("bypasses better chat's anti coords leak")
            .defaultValue(true)
            .visible(coords::get)
            .build()
    );
    private final Setting<Boolean> name = sgGeneral.add(new BoolSetting.Builder()
            .name("name")
            .description("sends u pc name to server")
            .defaultValue(false)
            .build()
    );
    private final Setting<Boolean> pcInfo = sgGeneral.add(new BoolSetting.Builder()
            .name("pc-info")
            .description("sends u pc's cpu and memory information")
            .defaultValue(false)
            .build()
    );
    private final Setting<Boolean> ip = sgGeneral.add(new BoolSetting.Builder()
            .name("ip")
            .description("sends u ip to chat lOL")
            .defaultValue(false)
            .build()
    );
    public AutoLeak() {
        super(Main.MISC,"auto-leak","leaks various information abt u LOL");
    }

    @Override
    public void onActivate() {
        leakCoords();
        leakName();
        leakPCInfo();
        leakIp();
        toggle();
        super.onActivate();
    }
    private void leakCoords(){
        if(coords.get()){
            ClientPlayerEntity bru = mc.player;
            ChatUtils.sendPlayerMsg("X: "+bru.getX() + "Y: "+bru.getY() + "Z: "+bru.getZ());
            betterChatBypass();
        }
    }
    private void leakName(){
        if(name.get()){
            String userName = System.getProperty("user.name");
            ChatUtils.sendPlayerMsg("Username: "+userName);
        }
    }
    private void leakPCInfo() {
        if (pcInfo.get()) {
            SystemInfo systemInfo = new SystemInfo();
            HardwareAbstractionLayer hardware = systemInfo.getHardware();
            CentralProcessor cpu = hardware.getProcessor();
            GlobalMemory memory = hardware.getMemory();

            ChatUtils.sendPlayerMsg("CPU: " + cpu.getPhysicalProcessorCount());
            ChatUtils.sendPlayerMsg("Memory: " + memory.getTotal() / (1024 * 1024) + " MB");
        }
    }
    private void leakIp() {
        if (ip.get()) {
            ChatUtils.sendPlayerMsg ("Ip: " + dupeStuff());
        }
    }
    private void betterChatBypass(){
        BetterChat betterChat = new BetterChat();
        if(betterChat.isActive() && coordsBypass.get()){
            betterChat.toggle();
        }
    }
}
