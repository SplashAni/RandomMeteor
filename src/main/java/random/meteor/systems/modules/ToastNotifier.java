import meteordevelopment.meteorclient.events.entity.EntityAddedEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import random.meteor.Main;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ToastNotifier extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgNotifications = settings.createGroup("Notifications");

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder().name("msg-delays").defaultValue(10).min(1).sliderRange(1, 60).build());

    /*notifications*/
    private final Setting<Boolean> damage = sgNotifications.add(new BoolSetting.Builder().name("damage").defaultValue(false).build());
    private final Setting<Boolean> visualRange = sgNotifications.add(new BoolSetting.Builder().name("visual-range").defaultValue(false).build());
    private final Setting<Boolean> death = sgNotifications.add(new BoolSetting.Builder().name("death").defaultValue(false).build());


    public ToastNotifier() {
        super(Main.RM, "toast-notifier", "uses windows notification system to notify you");
    }

    public List<String> queue = new ArrayList<>();
    int ticks;

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        ticks++;
        if (ticks >= delay.get()) {
            ticks = 0;
            if (!queue.isEmpty()) {
                sendToast(queue.get(0));
                queue.remove(0);
            }
        }
    }

    @EventHandler
    public void onEntityAdded(EntityAddedEvent event) {
        if (visualRange.get()) {
            queue.add(event.entity.getEntityName() + " has entered your visual range.");
        }
    }

    public void sendToast(String info) {
        try {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
            TrayIcon trayIcon = new TrayIcon(image, "ok");
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip("");

            tray.add(trayIcon);

            TrayIcon.MessageType mt = TrayIcon.MessageType.INFO;

            trayIcon.displayMessage("Toast notifier", info, mt);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }
}
