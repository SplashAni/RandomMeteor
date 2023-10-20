package random.meteor.systems.modules;

import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import random.meteor.Main;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ToastNotifier extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgNotifications = settings.createGroup("Notifications");

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("msg-delays")
        .defaultValue(10)
        .min(1)
        .sliderRange(1, 60)
        .build()
    );

    /*notificcations*/
    private final Setting<Boolean> damage = sgNotifications.add(new BoolSetting.Builder()
        .name("damage")
        .defaultValue(false)
        .build()
    );
    private final Setting<Boolean> visualRange = sgNotifications.add(new BoolSetting.Builder()
        .name("visual-range")
        .defaultValue(false)
        .build()
    );
    private final Setting<Boolean> death = sgNotifications.add(new BoolSetting.Builder()
        .name("death")
        .defaultValue(false)
        .build()
    );


    public ToastNotifier() {
        super(Main.RM, "toast-notifier", "uses windows notifciation system to notify u");
    }

    public List<String> toSend = new ArrayList<>();


    int ticks;

    @Override
    public void onActivate() {
        sendToast("hi");
        super.onActivate();
    }

    public boolean sendToast(String info) {
        try {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
            TrayIcon trayIcon = new TrayIcon(image, "ok");
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip("");

            tray.add(trayIcon);

            TrayIcon.MessageType mt = TrayIcon.MessageType.INFO;

            trayIcon.displayMessage("Toast notifier", info, mt);
            return true;
        } catch (AWTException e) {
            e.printStackTrace();
            return false;
        }
    }
}
