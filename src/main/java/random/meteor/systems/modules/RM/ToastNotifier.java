package random.meteor.systems.modules.RM;

import meteordevelopment.meteorclient.events.entity.DamageEvent;
import meteordevelopment.meteorclient.events.entity.EntityAddedEvent;
import meteordevelopment.meteorclient.events.entity.EntityRemovedEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
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

    @EventHandler
    public void onTick() {

        if(toSend.isEmpty()) return;


        for(String s : toSend){
            sendToast(s);
        }

    }

    @EventHandler
    public void onDamage(DamageEvent event) {
        if (event.entity.getUuid() == null) return;

        assert mc.player != null;

        if (event.entity.getUuid().equals(mc.player.getUuid()) && damage.get()) { /*gradle demon im 😰🥶*/
            if (event.amount > 1) return; /*we dont want small shit plz :sob:*/
            toSend.add(("You took " + event.amount + "damage " + "from " + event.entity));
        }
    }

    @EventHandler
    private void entityRemoved(EntityRemovedEvent event) {
        assert mc.player != null;
        if (!event.entity.getUuid().equals(mc.player.getUuid()) && visualRange.get()) {
            toSend.add((event.entity.getEntityName() + " has left you visual range."));
        }
    }

    @EventHandler
    private void entityAdded(EntityAddedEvent event) {
        assert mc.player != null;
        if (!event.entity.getUuid().equals(mc.player.getUuid()) && visualRange.get()) {
            toSend.add((event.entity.getEntityName() + " has entered you visual range."));
        }
    }

    @EventHandler
    private void onPacketReceive(PacketEvent.Receive event) {
        if (!death.get()) return;

        if (event.packet instanceof DeathMessageS2CPacket packet) {
            assert mc.world != null;
            if (mc.world.getEntityById(packet.getEntityId()) == mc.player) {
                toSend.add("You have died because of \"" + packet.getMessage() + "\"");
            }
        }
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
